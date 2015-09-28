package br.cefetrj.sagitarii.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.instances.InstanceList;
import br.cefetrj.sagitarii.core.instances.InstanceListContainer;
import br.cefetrj.sagitarii.core.types.FragmentStatus;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.entity.Fragment;
import br.cefetrj.sagitarii.persistence.entity.Instance;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.InstanceService;

public class InstanceBuffer {
	private int bufferSize;
	private Queue<Instance> instanceInputBuffer;
	private Queue<Instance> instanceJoinInputBuffer;
	private Queue<Instance> instanceOutputBuffer;
	private Queue<Instance> instanceTempOutputBuffer;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	private List<Experiment> runningExperiments;
	private InstanceListContainer listContainer;

	private boolean canServe() {
		boolean result = ( instanceTempOutputBuffer == null ) || 
				( ( instanceTempOutputBuffer != null ) && ( instanceTempOutputBuffer.size() == 0 ) );
		return result;
	}
	
	public boolean isEmpty() {
		return ( getInstanceJoinInputBufferSize() == 0 ) && ( getInstanceInputBufferSize() == 0 ); 
	}
	
	public synchronized void transferOutputBufferToMemory() {
		logger.debug("saving output buffer to memory: " + instanceInputBuffer.size() + " instances.");
		instanceTempOutputBuffer = new LinkedList<Instance>( instanceInputBuffer );
		instanceInputBuffer.clear();
	}
	
	public synchronized void restoreOutputBufferFromMemory() {
		logger.debug("restoring output buffer from memory: " + instanceTempOutputBuffer.size() + " instances.");
		for ( Instance instance : instanceTempOutputBuffer ) {
			instanceInputBuffer.add( instance );
		}
		instanceTempOutputBuffer.clear();
	}
	
	private void processAndInclude( List<Instance> preBuffer ) {
		for( Instance instance : preBuffer ) {
			if( instance.getType().isJoin() ) {
				instanceJoinInputBuffer.add(instance);
			} else {
				instanceInputBuffer.add(instance);
			}
		}
	}
	
	public void loadBuffers() throws Exception {
		int runningExperimentCount = runningExperiments.size(); 
		if ( runningExperimentCount == 0 ) return;
		int sliceSize;
		listContainer.clear();

		if ( getInstanceInputBufferSize() < ( bufferSize / 3 ) ) {
			logger.debug("loading COMMON buffer...");
			sliceSize = ( bufferSize - getInstanceInputBufferSize() ) / runningExperimentCount + 1;
			for ( Experiment experiment : runningExperiments ) {
				List<Instance> common = loadCommonBuffer( sliceSize, experiment );
				if ( common != null ) {
					listContainer.addList( new InstanceList(common, experiment.getTagExec()) );
				}
			}
			if ( listContainer.size() > 0 ) {
				instanceInputBuffer.addAll( listContainer.merge() );
			} 
		}
	
		listContainer.clear();
		
		if ( getInstanceJoinInputBufferSize() < ( bufferSize / 5 ) ) {
			logger.debug("loading SELECT buffer...");
			sliceSize = ( bufferSize - getInstanceJoinInputBufferSize() ) / runningExperimentCount + 1;
			for ( Experiment experiment : runningExperiments ) {
				List<Instance> select = loadJoinBuffer( sliceSize, experiment);
				if ( select != null ) {
					listContainer.addList( new InstanceList(select, experiment.getTagExec()) );
				}
			}
			if ( listContainer.size() > 0 ) {
				instanceInputBuffer.addAll( listContainer.merge() );
			}
		}
		
	}
	
	
	public int getInstanceInputBufferSize() {
		return instanceInputBuffer.size();
	}
	
	public int getInstanceJoinInputBufferSize() {
		return instanceJoinInputBuffer.size();
	}
	
	public int getInstanceTempOutputBufferSize() {
		return instanceTempOutputBuffer.size();
	}

	/**
	 * Remove all null elements of buffer
	 * Note: The buffer cannot have null elements, so
	 * if we're here, something very bad is in course...
	 * 
	 */
	private void sanitizeBuffer() {
		int total = 0;
		Iterator<Instance> i = instanceInputBuffer.iterator();
		while ( i.hasNext() ) {
			Instance req = i.next(); 
			if ( req == null ) {
				i.remove();
				total++;
			}
		}
		logger.warn(total + " null instances removed from buffer. This is not a normal behaviour.");
	}
	
	private Instance getNextInstance() {
		return getNextInstance( this.runningExperiments );
	}
	
	public synchronized Instance getNextInstance( List<Experiment> runningExperiments ) {
		
		if ( !canServe() ) return null;
		
		this.runningExperiments = runningExperiments;
		Instance next = instanceInputBuffer.poll();
		if ( next != null ) {
			if ( hasOwner(next) ) {
				instanceOutputBuffer.add( next );
			} else {
				return getNextInstance();
			}
		} else {
			if ( instanceInputBuffer.size() > 0 ) {
				logger.error("null instance detected in output buffer. Running sanitization...");
				sanitizeBuffer();
			} 
		}
		return next;
	}
	
	public void setBufferSize( int bufferSize ) {
		this.bufferSize = bufferSize;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}

	public InstanceBuffer() {
		this.instanceInputBuffer = new LinkedList<Instance>();
		this.instanceJoinInputBuffer = new LinkedList<Instance>();
		this.instanceOutputBuffer = new LinkedList<Instance>();
		this.bufferSize = 1000;
		this.listContainer = new InstanceListContainer();
	}
	
	public void removeFromOutputBuffer( Instance instance ) {
		for ( Instance pipe : instanceOutputBuffer ) {
			if ( pipe.getSerial().equals( instance.getSerial() ) ) {
				instanceOutputBuffer.remove( pipe );
				break;
			}
		}
	}
	
	public boolean experimentIsStillQueued( Experiment exp ) {
		for( Fragment frag : exp.getFragments() ) {
			for( Instance pipe : instanceOutputBuffer  ) {
				if( pipe.getIdFragment() == frag.getIdFragment() ) {
					return true;
				}
			}
			
			for( Instance pipe : instanceInputBuffer  ) {
				if( pipe.getIdFragment() == frag.getIdFragment() ) {
					return true;
				}
			}
			for( Instance pipe : instanceJoinInputBuffer  ) {
				if( pipe.getIdFragment() == frag.getIdFragment() ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public synchronized void reloadAfterCrash( List<Experiment> runningExperiments ) {
		this.runningExperiments = runningExperiments;
		logger.debug("after crash reloading " + runningExperiments.size() + " experiments.");
		try {
			try {
				InstanceService instanceService = new InstanceService();
				processAndInclude( instanceService.recoverFromCrash() );
				logger.debug( getInstanceInputBufferSize() + " common instances recovered.");
				logger.debug( getInstanceJoinInputBufferSize() + " JOIN instances recovered.");
			} catch ( NotFoundException e ) {
				logger.debug("no instances to recover");
			}
			
		} catch ( Exception e) {
			logger.error( e.getMessage() );
		} 
		logger.debug("after crash reload done.");
	}
	
	public Instance getNextJoinInstance() {
		Instance next = instanceJoinInputBuffer.poll();
		if ( next != null ) {
			logger.debug("serving join instance " + next.getSerial() );
			instanceOutputBuffer.add(next);
		}
		return next;
	}

	public void returnToBuffer( Instance instance ) {
		logger.debug("instance refund: " + instance.getSerial() );
		if ( instanceOutputBuffer.remove( instance ) ) {
			if ( instance.getType().isJoin() ) {
				instanceJoinInputBuffer.add( instance );
				logger.debug(" > to the join buffer" );
			} else {
				instanceInputBuffer.add( instance );
				logger.debug(" > to the common buffer" );
			}
		}
	}
	
	
	/**
	 *	Discard instances in buffer that have no experiments (deleted) 
	 */
	private boolean hasOwner( Instance instance ) {
		for ( Experiment exp : runningExperiments ) {
			for( Fragment frag : exp.getFragments() ) {
				if ( instance.getIdFragment() == frag.getIdFragment() ) {
					return true;
				}
			}
		}
		logger.warn("owner of instance " + instance.getSerial() + " not found. Will discard this instance.");
		return false;
	}
	
	public Queue<Instance> getInstanceInputBuffer() {
		return new LinkedList<Instance>( instanceInputBuffer );
	}

	public Queue<Instance> getInstanceOutputBuffer() {
		return new LinkedList<Instance>( instanceOutputBuffer );
	}

	public Queue<Instance> getInstanceTempOutputBuffer() {
		return instanceTempOutputBuffer;
	}

	public Queue<Instance> getInstanceJoinInputBuffer() {
		return new LinkedList<Instance>( instanceJoinInputBuffer );
	}
	
	private Fragment getRunningFragment( Experiment experiment ) {
		for ( Fragment frag : experiment.getFragments() ) {
			if ( frag.getStatus() == FragmentStatus.RUNNING ) {
				return frag;
			}
		}
		return null;
	}
	
	private List<Instance> loadJoinBuffer( int count, Experiment experiment ) {
		List<Instance> preBuffer = null;
		logger.debug("loading SELEC buffer");
		try {
			Fragment running = getRunningFragment( experiment );
			if ( running == null ) {
				logger.debug("no SELECT fragments running");
			} else {
				logger.debug("running SELECT fragment found: " + running.getSerial() );
				InstanceService ps = new InstanceService();
				preBuffer = ps.getHeadJoin( count, running.getIdFragment() );
			}
		} catch (NotFoundException e) {
			logger.debug("no running SELECT instances found for experiment " + experiment.getTagExec() );
		} catch (Exception e) {
			logger.error( e.getMessage() );
		}
		
		logger.debug("SELECT buffer size: " + instanceJoinInputBuffer.size() );
		return preBuffer;
		
	}

	private List<Instance> loadCommonBuffer( int count, Experiment experiment ) {
		List<Instance> preBuffer = null;
		logger.debug("loading common buffer...");
		try {
			Fragment running = getRunningFragment( experiment );
			if ( running == null ) {
				logger.debug("no fragments running");
			} else {
				logger.debug("running fragment found: " + running.getSerial() );
				InstanceService ps = new InstanceService();
				preBuffer = ps.getHead( count, running.getIdFragment() );
			}
		} catch (NotFoundException e) {
			logger.debug("no running instances found for experiment " + experiment.getTagExec() );
		} catch ( Exception e) {
			logger.error( e.getMessage() );
		} 

		logger.debug("common buffer size: " + instanceInputBuffer.size() );
		return preBuffer;
	}
	
	

}
