package br.cefetrj.sagitarii.teapot;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

/**
 * Interface to Sagitarii API
 * 
 * @author Carlos Magno Oliveira de Abreu
 *
 */
public class SagitariiInterface {
	private String sagitariiHostURL;
	private HttpClient client;
	private String securityToken;

	@SuppressWarnings("deprecation")
	public SagitariiInterface( String sagitariiHostURL, String user, String password ) {
		this.sagitariiHostURL = sagitariiHostURL;
		client = new DefaultHttpClient();
		securityToken = getSecurityToken( user, password );
	}
	
	public String createNewExperiment( String workflowAlias ) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append( generateJsonPair("SagitariiApiFunction", "apiCreateExperiment") + "," ); 
		sb.append( generateJsonPair("workflowTag", workflowAlias) + "," );
		sb.append( generateJsonPair("securityToken", securityToken) ); 
		sb.append("}");
		return execute( sb.toString() );
	}
	
	private String getSecurityToken( String user, String password ) {
		StringBuilder sb = new StringBuilder();
		User userData = null;
		sb.append("{");
		sb.append( generateJsonPair("SagitariiApiFunction", "apiGetToken") + "," ); 
		sb.append( generateJsonPair("user", user) + "," ); 
		sb.append( generateJsonPair("password", password) ); 
		sb.append("}");
		
		String result = execute( sb.toString() );
		
		if ( !result.equals("") ) {
			Gson gson = new Gson();
			userData = gson.fromJson( result, User.class );
		}
		return userData.getToken();		
	}

	
	public String startExperiment( String experimentSerial ) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append( generateJsonPair("SagitariiApiFunction", "apiStartExperiment") + "," ); 
		sb.append( generateJsonPair("experimentSerial", experimentSerial) + "," );
		sb.append( generateJsonPair("securityToken", securityToken) ); 
		sb.append("}");
		return execute( sb.toString() );
	}
	
	public List<Experiment> getMyExperiments() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append( generateJsonPair("SagitariiApiFunction", "apiGetExperiments") + "," ); 
		sb.append( generateJsonPair("securityToken", securityToken) ); 
		sb.append("}");
		
		String result = execute( sb.toString() );

		Gson gson = new Gson();
		ExperimentData data = gson.fromJson( result, ExperimentData.class );
		List<Experiment> experiments = data.getData();
		
		return experiments;
	}	

	public List<Workflow> getWorkflows() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append( generateJsonPair("SagitariiApiFunction", "apiGetWorkflows") + "," ); 
		sb.append( generateJsonPair("securityToken", securityToken) ); 
		sb.append("}");
		
		String result = execute( sb.toString() );

		Gson gson = new Gson();
		WorkflowData data = gson.fromJson( result, WorkflowData.class );
		List<Workflow> workflows = data.getData();
		
		return workflows;
	}	
	
	/** =======================================================================================
	 * 
	 * 
	 * 		AUXILIARY METHODS
	 * 
	 * 
	 * ======================================================================================= 
	 */
	
	private String execute( String json ) {
		Parameter param = new Parameter("externalForm", json );
		List<Parameter> params = new ArrayList<Parameter>();
		params.add( param );
		return doPostStrings( params, "externalApi");
	}
	
	private String doPostStrings( List<Parameter> parameters, String action ) {
		String resposta = "SEM_RESPOSTA";
		try {
			String url = sagitariiHostURL + action;
			HttpPost post = new HttpPost(url);
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			for ( Parameter param : parameters ) {
				urlParameters.add(new BasicNameValuePair( param.name, param.value ) );
			}
			post.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));
			HttpResponse response = client.execute(post);
			int stCode = response.getStatusLine().getStatusCode();
			if ( stCode != 200) {
				resposta = "ERRO_" + stCode;
			} else {
				HttpEntity entity = response.getEntity();
				InputStreamReader isr = new InputStreamReader(entity.getContent(), "UTF-8");
				resposta = convertStreamToString(isr);
				Charset.forName("UTF-8").encode(resposta);
				isr.close();
			}
		} catch (Exception ex) {
		    ex.printStackTrace();
		} 
		return resposta;
	}
	

	private String convertStreamToString(java.io.InputStreamReader is) {
	    java.util.Scanner s = new java.util.Scanner(is);
		s.useDelimiter("\\A");
	    String retorno = s.hasNext() ? s.next() : "";
	    s.close();
	    return retorno;
	}	

	private String generateJsonPair(String paramName, String paramValue) {
		return "\"" + paramName + "\":\"" + paramValue + "\""; 
	}

	
}
