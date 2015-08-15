
package br.cefetrj.sagitarii.action;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.misc.PathFinder;

@Action(value="viewText", results= {  
	    @Result(location = "viewText.jsp", name = "ok")}, interceptorRefs= { @InterceptorRef("seguranca")}
)  

@ParentPackage("default")
public class ViewTextAction extends BasicActionClass {
	private String textContent;
	private String fileName;
	
	private String readFile(String fileName) throws IOException {
		String path = PathFinder.getInstance().getPath() + "/" + fileName;
		
		BufferedReader br = new BufferedReader( new FileReader(path) );
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}	
	
	public String execute () {
		
		try {
			textContent = readFile( fileName );
		} catch ( Exception e ) {
			textContent = e.getMessage();
		}
		
		return "ok";
	}
	
	public String getTextContent() {
		return textContent;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
}
