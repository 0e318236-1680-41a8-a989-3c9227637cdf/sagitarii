package cmabreu.sagitarii.teapot.comm;
/**
 * Copyright 2015 Carlos Magno Abreu
 * magno.mabreu@gmail.com 
 *
 * Licensed under the Apache  License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required  by  applicable law or agreed to in  writing,  software
 * distributed   under the  License is  distributed  on  an  "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the  specific language  governing  permissions  and
 * limitations under the License.
 * 
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.teapot.Configurator;
import cmabreu.sagitarii.teapot.SystemProperties;

public class Communicator  {
	private WebClient webClient;
    private String macAddress;	
	private Logger logger = LogManager.getLogger( this.getClass().getName() );

	public Communicator( Configurator gf, SystemProperties tm ) throws Exception {
		
		webClient = new WebClient(gf);
		try {
			this.macAddress = URLEncoder.encode(tm.getMacAddress(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw e;
		}
	}

	public String doPost( String targetAction, String parameter, String content) {
		String resposta = "COMM_ERROR";
		try { 
			webClient.doPost(targetAction, parameter, content);
			resposta = "OK";
		} catch ( Exception e ) {
			logger.error("post error:   " + e.getMessage() );
			logger.error("error detail: " + parameter );
		} 
		return resposta;
	}
	
	/**
	* Send a GET request to Sagitarii
	* 
	* Exemple : targetAction = "myStrutsAction", parameters = "name=foo&sobrenome=Bar"
	*/
	public String send( String targetAction, String parameters ) {
		String resposta = "COMM_ERROR";
		try { 
			resposta = webClient.doGet(targetAction, parameters );
		} catch ( Exception e ) {
			logger.error("get error:    " + e.getMessage() );
			logger.error("error detail: " + parameters );
		} 
		return resposta;
	}
	
	
}
