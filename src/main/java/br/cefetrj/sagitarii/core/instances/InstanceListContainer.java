package br.cefetrj.sagitarii.core.instances;

import java.util.ArrayList;
import java.util.List;

import br.cefetrj.sagitarii.persistence.entity.Instance;

public class InstanceListContainer {
	private List<InstanceList> lists;
	
	public void clear() {
		lists.clear();
	}
	
	public int size() {
		return lists.size();
	}
	
	public void addList( InstanceList list ) {
		lists.add( list );
	}
	
	public InstanceListContainer() {
		lists = new ArrayList<InstanceList>();
	}
	
	/**
	 * Get the size of the bigger list
	 * 
	 */
	private int getBiggerSize() {
		int res = 0;
		for ( InstanceList list : lists ) {
			if ( list.size() > res ) {
				res = list.size();
			}
		}
		return res;
	}
	
	public List<Instance> merge() {
		
		System.out.println("will merge lists");
		
		for ( InstanceList list : lists ) {
			System.out.println("List: " + list.getId() );
			for ( Instance i : list.getList() ) {
				System.out.println(" > " + i.getSerial() );
			}
		}
		
		System.out.println("------------------------------");
		
	    List<Instance> res = new ArrayList<Instance>();
	    int bigger = getBiggerSize();
	    for ( int index = 0; index < bigger; index++  ) {
	    	for ( InstanceList list : lists ) {
	    		if ( index < list.size() ) {
	    			Instance instance = list.get(index); 
	    			res.add( instance );
	    		}
	    	}
	    }
	    
	    System.out.println("Result:");
		for ( Instance i : res ) {
			System.out.println(" > " + i.getSerial() );
		}
	    
	    
	    return res;
	}

}
