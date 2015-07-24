package cmabreu.sagitarii.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.DataReceiver;
import cmabreu.sagitarii.core.TableAttribute;
import cmabreu.sagitarii.misc.DateLibrary;
import cmabreu.sagitarii.persistence.entity.Experiment;
import cmabreu.sagitarii.persistence.entity.FileLight;
import cmabreu.sagitarii.persistence.entity.Relation;
import cmabreu.sagitarii.persistence.entity.User;
import cmabreu.sagitarii.persistence.entity.Workflow;
import cmabreu.sagitarii.persistence.services.ExperimentService;
import cmabreu.sagitarii.persistence.services.FileService;
import cmabreu.sagitarii.persistence.services.RelationService;
import cmabreu.sagitarii.persistence.services.UserService;
import cmabreu.sagitarii.persistence.services.WorkflowService;

import com.google.gson.GsonBuilder;
import com.opensymphony.xwork2.ActionContext;

public class ExternalApi {
	private Logger logger = LogManager.getLogger( this.getClass().getName() );


	@SuppressWarnings("unchecked")
	public String execute( String data ) throws Exception {
		logger.debug("external API called");
		
		if ( data != null  ) {
			GsonBuilder builder = new GsonBuilder();
			Map<String,Object> map = ( Map<String,Object>)builder.create().fromJson( data , Object.class);
			
			String command = (String)map.get("SagitariiApiFunction");
			String securityToken = (String)map.get("securityToken");

			logger.debug( "data: " + data );
			
			if ( command.equals("apiGetToken") ) {
				return getToken( map );
			}
			
			if ( ( securityToken!= null ) && ( !securityToken.equals("") ) ) {
				User user = getUserByToken( securityToken );
				if ( user != null ) {
				
					logger.debug( user.getFullName() + " : command " + command );
					
					if ( command.equals("apiReceiveData") ) {
						return receiveData( map );
					}
					
					if ( command.equals("apiRequestNewUser") ) {
						return requestNewUser( map );
					}

					if ( command.equals("apiCreateExperiment") ) {
						return createExperiment( map, user );
					}

					if ( command.equals("apiGetExperiments") ) {
						return getExperiments( map, user );
					}
					
					if ( command.equals("apiStartExperiment") ) {
						return startExperiment( map );
					}
					if ( command.equals("apiGetFilesExperiment") ) {
						return getFilesExperiment( map );
					}
					if ( command.equals("apiCreateTable") ) {
						return createTable( map );
					}
				
				} else {
					return  "RETURN_INVALID_SECURITY_TOKEN";
				}
			}
			
		} else {
			return  "RETURN_EMPTY_DATA";
		}
		return "RETURN_UNKNOWN_COMMAND";
	}
	
	
	private String createTable( Map<String, Object> map ) {
		String tableName = (String)map.get("tableName");
		String tableDescription = (String)map.get("tableDescription");
		try {
			if ( !tableName.equals("") ) {
				List<TableAttribute> attributes = new ArrayList<TableAttribute>();
				
				for ( Map.Entry<String, Object> entry : map.entrySet() ) {
					String key = entry.getKey();
					String value = (String)entry.getValue();
					
					try {
						TableAttribute attribute = new TableAttribute();
						attribute.setName( key );
						attribute.setType( TableAttribute.AttributeType.valueOf( value ) );
						attributes.add( attribute );
					} catch ( Exception ignoreNotValidAttributes ) {
						// If catch, this is not a valid TableAttribute.AttributeType 
					}
					
				}
	
				Relation rel = new Relation();
				rel.setDescription( tableDescription );
				rel.setName( tableName );
				
				RelationService ts = new RelationService();
				ts.insertTable( rel, attributes );

				return "RETURN_OK";

			} else {
				return "RETURN_ERROR_INVALID_TABLE_NAME";
			}
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
			return formatMessage( e.getMessage() );
		}
		
	}
	
	private String requestNewUser( Map<String, Object> map ) {
		try {
			String fullName = (String)map.get("fullName");
			String username = (String)map.get("username");
			String password = (String)map.get("password");
			String mailAddress = (String)map.get("mailAddress");
			if( !username.equals("") && !mailAddress.equals("") && !password.equals("") ) {
				UserService es = new UserService();
				User user = es.requestAccess(fullName, username, password, mailAddress);
				return user.getPassword();
			}
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
			return formatMessage( e.getMessage() );
		}
		return "RETURN_REQUEST_ERROR";
	}
	
	private String startExperiment( Map<String, Object> map ) {
		try {
			String experimentSerial = (String)map.get("experimentSerial");
			if( !experimentSerial.equals("") ) {
				ExperimentService es = new ExperimentService();
				Experiment experiment = es.getExperiment( experimentSerial );
				es.newTransaction();
				experiment = es.runExperiment( experiment.getIdExperiment() );
				return experiment.getTagExec();
			}
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
			return formatMessage( e.getMessage() );
		}
		return "RETURN_INVALID_EXPERIMENT_SERIAL";
	}
	
	
	private String getExperiments( Map<String, Object> map, User user ) {
		Set<Experiment> experiments = new HashSet<Experiment>();
		try {
			ExperimentService es = new ExperimentService();
			experiments = es.getList( user );
			
			StringBuilder data = new StringBuilder();
			String dataPrefix = "";
			data.append("[");
			for ( Experiment experiment : experiments ) {
				String startDate = DateLibrary.getInstance().getDateHourTextHuman( experiment.getLastExecutionDate() ); 
				data.append(dataPrefix + "{");
				data.append( generateJsonPair( "tagExec", experiment.getTagExec() ) + "," );
				data.append( generateJsonPair( "startDate", startDate ) + "," );
				data.append( generateJsonPair( "status", experiment.getStatus().toString() )  + ","  );
				data.append( generateJsonPair( "elapsedTime", experiment.getElapsedTime() ) );
				dataPrefix = ",";
				data.append("}");
			}
			data.append("]");
			
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append( addArray("data", data.toString() ) );
			sb.append("}");
			
			return formatMessage( sb.toString() );
			
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
			return formatMessage( e.getMessage() );
		}
		
	}

	private String getFilesExperiment( Map<String, Object> map ) {
		try {
			String experimentSerial = (String)map.get("experimentSerial");
			if( ( experimentSerial != null ) && (!experimentSerial.equals("") ) ) {
				ExperimentService es = new ExperimentService();
				Experiment experiment = es.getExperiment( experimentSerial );
				
				String activityTag = (String)map.get("activityTag");
				String rangeStart = (String)map.get("rangeStart");
				String rangeEnd = (String)map.get("rangeEnd");
				
				FileService fs = new FileService();
				Set<FileLight> files = fs.getList( experiment.getIdExperiment(), activityTag, rangeStart, rangeEnd );
				
				StringBuilder data = new StringBuilder();
				String dataPrefix = "";
				data.append("[");
				for ( FileLight file : files ) {
					data.append( dataPrefix + "{");
					data.append( generateJsonPair( "fileName" , file.getFileName() ) + "," ); 
					data.append( generateJsonPair( "fileId", String.valueOf( file.getIdFile() ) ) ); 
					dataPrefix = ",";
					data.append("}");
				}
				data.append("]");
				
				StringBuilder sb = new StringBuilder();
				sb.append("{");
				sb.append( addArray("data", data.toString() ) ); 
				sb.append("}");

				return sb.toString();
			} else {
				return "RETURN_INVALID_EXPERIMENT_SERIAL";
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
			logger.error( e.getMessage() );
			return formatMessage( e.getMessage() );
		}
	}

	private String getToken( Map<String, Object> map ) {
		String token = "";
		try {
			String userName = (String)map.get("user");
			String password = (String)map.get("password");
			logger.debug("get token for user " + userName );
			UserService es = new UserService();
			User user = es.login(userName, password);
			token = UUID.randomUUID().toString().replaceAll("-", "");
			ActionContext.getContext().getSession().put( token, user );
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
			return formatMessage( e.getMessage() );
		}
		return token;
	}
	
	private User getUserByToken( String token ) {
		return (User)ActionContext.getContext().getSession().get( token );
	}
	
	private String createExperiment( Map<String, Object> map, User user ) {
		String workflowTag = (String)map.get("workflowTag");
		if( !workflowTag.equals("") ) {
			try {
				WorkflowService ws = new WorkflowService();
				Workflow wk = ws.getWorkflow( workflowTag );
				ExperimentService es = new ExperimentService();
				Experiment experiment = es.generateExperiment( wk.getIdWorkflow(), user );
				return formatMessage( experiment.getTagExec() );
			} catch ( Exception e ) {
				logger.error( e.getMessage() );
				return formatMessage( e.getMessage() );
			}
		}
		return "RETURN_INVALID_WORKFLOW";
	}

	private String addArray(String paramName, String arrayValue) {
		return "\"" + paramName + "\":" + arrayValue ; 
	}

	private String receiveData( Map<String, Object> map ) {
		String messageResult = "";
		List<String> contentLines = new ArrayList<String>();
		String prefix = "";
		
		String tableName = (String)map.get("tableName");
		String experimentSerial = (String)map.get("experimentSerial");

		StringBuilder columns = new StringBuilder();
		StringBuilder dataLine = new StringBuilder();

		try {
			@SuppressWarnings("unchecked")
			ArrayList<Map<String,String> > data = (ArrayList<Map<String,String> >)map.get("data");
			boolean columnsAdded = false;
			for ( Map<String,String> entry : data  ) {

				for ( Map.Entry<String, String> entryLine : entry.entrySet() ) {
					String key = entryLine.getKey();
					String value = entryLine.getValue();
					
					if( !columnsAdded) {
						columns.append( prefix + key );
					}
					
					dataLine.append( prefix + value );
					prefix = ",";
				}
				
				if( !columnsAdded) {
					contentLines.add( columns.toString() );
					columnsAdded = true;
				}
				
				contentLines.add( dataLine.toString() );
				dataLine.setLength( 0 );
				prefix = "";

			}

			try {
				if( !tableName.equals("") && !experimentSerial.equals("") ) {
					messageResult = new DataReceiver().receive( contentLines, tableName, experimentSerial );
				}
			} catch ( Exception e ) {
				logger.error( e.getMessage() );
				messageResult = e.getMessage();
			}
			
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		return formatMessage(messageResult);
		
	}
	
	private String formatMessage( String message ) {
		return message;
	}
	
	private String generateJsonPair(String paramName, String paramValue) {
		return "\"" + paramName + "\":\"" + paramValue + "\""; 
	}

	
}