package cmabreu.sagitarii.core.sockets;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FileXMLParser {
	private Document doc;
	
	/*
	private String getTagValue(String sTag, Element eElement) throws Exception{
		try {
			NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	        Node nValue = (Node) nlList.item(0);
			return nValue.getNodeValue();
		} catch ( Exception e ) {
			throw e;
		}
	}
	*/
	
	public List<ReceivedFile> parseDescriptor( String xmlFile ) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse( xmlFile );
		doc.getDocumentElement().normalize();

		NodeList pipeTag = doc.getElementsByTagName("session");
		Node pipeConf = pipeTag.item( 0 );
		Element pipeElement = (Element) pipeConf;
		String sessionSerial = pipeElement.getAttribute("id");
		String targetTable = pipeElement.getAttribute("targetTable");
		String experimentSerial = pipeElement.getAttribute("experiment");
		String instance = pipeElement.getAttribute("instance");
		String activity = pipeElement.getAttribute("activity");
		String macAddress = pipeElement.getAttribute("macAddress");
		String fragment = pipeElement.getAttribute("fragment");
		
		List<ReceivedFile> resp = new ArrayList<ReceivedFile>();
		NodeList mapconfig = doc.getElementsByTagName("file");
		for ( int x = 0; x < mapconfig.getLength(); x++ ) {
			try {
				Node mpconfig = mapconfig.item(x);
				Element mpElement = (Element) mpconfig;

				String fileName = mpElement.getAttribute("name");
				String type = mpElement.getAttribute("type");

				ReceivedFile receivedFile = new ReceivedFile();
				receivedFile.setExperimentSerial(experimentSerial);
				receivedFile.setFileName(fileName);
				receivedFile.setSessionSerial(sessionSerial);
				receivedFile.setTargetTable(targetTable);
				receivedFile.setType(type);
				receivedFile.setMacAddress(macAddress);
				receivedFile.setInstance(instance);
				receivedFile.setActivity(activity);
				receivedFile.setFragment(fragment);
				
				resp.add(receivedFile);
				
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		return resp;
		
	}
	
	

}
