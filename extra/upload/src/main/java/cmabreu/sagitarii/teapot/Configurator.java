package cmabreu.sagitarii.teapot;

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

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cmabreu.sagitarii.teapot.comm.ProxyInfo;

public class Configurator {

	private String hostURL;
	private ProxyInfo proxyInfo;
	private int useProxy;
	private Document doc;
	private int fileSenderDelay;
	private String storageHost;
	private int storagePort;
	private SystemProperties systemProperties;
	private Logger logger = LogManager.getLogger( this.getClass().getName()  );

	public SystemProperties getSystemProperties() {
		return this.systemProperties;
	}
	
	public int getFileSenderDelay() {
		return fileSenderDelay;
	}
	
	public String getStorageHost() {
		return storageHost;
	}
	
	public int getStoragePort() {
		return storagePort;
	}
	
	public ProxyInfo getProxyInfo() {
		return proxyInfo;
	}

	public boolean useProxy() {
		return this.useProxy == 1;
	}
	
	public String getHostURL() {
		return hostURL;
	}

	private String getTagValue(String sTag, Element eElement) throws Exception{
		try {
			NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	        Node nValue = (Node) nlList.item(0);
			return nValue.getNodeValue();
		} catch ( Exception e ) {
			logger.error("Element " + sTag + " not found in configuration file.");
			throw e;
		}
	 }
	
	public String getValue(String container, String tagName) {
		String tagValue = "";
		try {
			NodeList postgis = doc.getElementsByTagName(container);
			Node pgconfig = postgis.item(0);
			Element pgElement = (Element) pgconfig;
			tagValue = getTagValue(tagName, pgElement) ; 
		} catch ( Exception e ) {
		}
		return tagValue;
	}

	
	public Configurator(String file) throws Exception {
		try {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
		} catch (Exception e) {
			logger.error("XML file " + file + " not found.");
		}	
		systemProperties = new SystemProperties();
	}
	
	public void loadMainConfig()  {
			
			NodeList mapconfig = doc.getElementsByTagName("cluster");
			Node mpconfig = mapconfig.item(0);
			Element mpElement = (Element) mpconfig;
			try {
				hostURL = getTagValue("hostURL", mpElement);
				storageHost = getTagValue("storageHost", mpElement);
				storagePort = Integer.valueOf( getTagValue("storagePort", mpElement) );
				fileSenderDelay = Integer.valueOf( getTagValue("fileSenderDelay", mpElement) );
				useProxy = Integer.parseInt( getValue("proxy", "useProxy") );
				
				if (useProxy == 1) {
					proxyInfo = new ProxyInfo();
					proxyInfo.setHost( getValue("proxy", "proxy-host") );
					proxyInfo.setPort( Integer.parseInt(getValue("proxy", "proxy-port"))  );
					proxyInfo.setPassword( getValue("proxy", "proxy-password") );
					proxyInfo.setUser( getValue("proxy", "proxy-user") );
				} 			
			} catch ( Exception e ) {
				System.out.println( e.getMessage() );
			}
			
			
	}
	
	
}
