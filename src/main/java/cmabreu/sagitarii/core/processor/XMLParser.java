package cmabreu.sagitarii.core.processor;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class XMLParser {
	private Document doc;
	
	
	private String getTagValue(String sTag, Element eElement) throws Exception{
		try {
			NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	        Node nValue = (Node) nlList.item(0);
			return nValue.getNodeValue();
		} catch ( Exception e ) {
			throw e;
		}
	}

	
	private List<String> getSourceData( String sourceData ) {
		List<String> inputData = new ArrayList<String>();
		String line = "";
		for( int x = 0; x < sourceData.length(); x++  ) {
			String character = String.valueOf( sourceData.charAt(x) );
			if ( !character.equals("\n")  ) {
				line = line + character;
			} else {
				inputData.add( line );
				line = "";
			}
		}
		if ( line.length() > 0 ) {
			inputData.add( line );
		}
		return inputData;
	}
	
	public List<Activation> parseActivations( String xml ) throws Exception {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputSource is = new InputSource( new StringReader(xml) );
		doc = dBuilder.parse( is );
		doc.getDocumentElement().normalize();
		
		NodeList pipeTag = doc.getElementsByTagName("pipeline");
		Node pipeConf = pipeTag.item( 0 );
		Element pipeElement = (Element) pipeConf;
		String pipeSerial = pipeElement.getAttribute("serial");
		String fragment = pipeElement.getAttribute("fragment");
		String experiment = pipeElement.getAttribute("experiment");
		String workflow = pipeElement.getAttribute("workflow");
		int pipelineId = Integer.valueOf( pipeElement.getAttribute("id") );
		
		List<Activation> resp = new ArrayList<Activation>();
		NodeList mapconfig = doc.getElementsByTagName("activity");
		for ( int x = 0; x < mapconfig.getLength(); x++ ) {
			try {
				Node mpconfig = mapconfig.item(x);
				Element mpElement = (Element) mpconfig;
				
				String sourceData = "";
				try { sourceData = getTagValue("inputData", mpElement); } catch ( Exception e3 ) {  }
				int order = Integer.valueOf( getTagValue("order", mpElement) );
				String serial = getTagValue("serial", mpElement);
				String command = getTagValue("command", mpElement);
				String type = getTagValue("type", mpElement);
				String executor = getTagValue("executor", mpElement);
				String executorType = getTagValue("executorType", mpElement);
				String targetTable = getTagValue("targetTable", mpElement);
				
				
				Activation activation = new Activation();
				activation.setWorkflow(workflow);
				activation.setType(type);
				activation.setExperiment(experiment);
				activation.setPipelineSerial(pipeSerial);
				activation.setPipelineId( pipelineId );
				activation.setSourceData( getSourceData( sourceData ) );
				activation.setCommand(command);
				activation.setFragment(fragment);
				activation.setOrder(order);
				activation.setActivitySerial(serial);
				activation.setXmlOriginalData( xml );
				activation.setExecutor( executor );
				activation.setExecutorType( executorType );
				activation.setTargetTable( targetTable );
				resp.add(activation);
				
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		Collections.sort( resp );
		return resp;
		
	}
	
	

}



