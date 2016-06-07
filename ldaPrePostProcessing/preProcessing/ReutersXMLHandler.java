package preProcessing;

import java.io.IOException;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReutersXMLHandler {

	public static String readXMLDocumentText(final Path filePath,
			final boolean asHTML)
	{
	            	
        String text = "";

		try 
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (filePath.toFile());

	        // normalize text representation
	        //doc.getDocumentElement ().normalize ();

			NodeList headlineNodes = doc.getElementsByTagName("headline");
	        for (int iNode = 0; iNode < headlineNodes.getLength(); iNode++)
	        {
	            Node headlineNode = headlineNodes.item(iNode);

	            if (headlineNode.getNodeType() == Node.ELEMENT_NODE)
	            {
	            	Element headlineElement = (Element) headlineNode;
	            	
	            	if (asHTML)
	            		text += "<h4>" + headlineElement.getTextContent() + "</h4>";
	            	else
	            		text += headlineElement.getTextContent() + " ";
	            }
	        }
	        
	            	
			// <text><p> for <p> content does not start with '-' and contain the word newsroom
	        NodeList textNodes = doc.getElementsByTagName("text");
	        for (int iNode = 0; iNode < textNodes.getLength(); iNode++)
	        {
	            Node textNode = textNodes.item(iNode);

	            if (textNode.getNodeType() == Node.ELEMENT_NODE)
	            {
	            	Element textElement = (Element) textNode;

	            	NodeList pNodes = textElement.getElementsByTagName("p");
	            	for (int jNode = 0; jNode < pNodes.getLength(); jNode++)
	   	        	{
		   	            Node pNode = pNodes.item(jNode);

		   	            if (pNode.getNodeType() == Node.ELEMENT_NODE)
		   	            {
		   	               Element pElement = (Element) pNode;
		   	               String content = pElement.getTextContent();

		   	               if (asHTML)
		   	               {
		   	            	   text += "<p>" + content + "</p>";
		   	               }
		   	               else if (!content.toLowerCase().contains("newsroom"))
		   	               {
		   	            	   text += content + " ";
		   	               }
		   	            }
	   	        	}
	            }
	        }		
		} 
		catch (SAXException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ParserConfigurationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (asHTML)
		{
			text = text.replace("\"", "\\\"");
		}
		
		return text;
	}
	
	public static String readXMLDocumentMeta(final Path filePath)
	{
		String data = filePath.getFileName().toString().split("\\.")[0];
		
		try 
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (filePath.toFile());

	        // normalize text representation
	        //doc.getDocumentElement ().normalize ();

			// <newsitem itemid="4943" id="root" date="1996-08-20" xml:lang="en">
	        Element root = doc.getDocumentElement();
	        if(root.hasAttribute("date"))
	        {
	        	data += " " + root.getAttribute("date");
	        }
	        else
	        {
	        	data += " ERROR_DATE";
	        }
	        
	        // <metadata></codes><codes class="bip:topics:1.0"><code code="E11">
	        NodeList metaDataNodes = doc.getElementsByTagName("metadata");
	        
	        if(metaDataNodes.getLength() == 0)
	        {
	        	data += " ERROR_TOPIC";
	        }
	        else
	        {
		        for (int iNode = 0; iNode < metaDataNodes.getLength(); iNode++)
		        {
		            Node metaDataNode = metaDataNodes.item(iNode);
	
		            if (metaDataNode.getNodeType() == Node.ELEMENT_NODE)
		            {
		            	Element metaDataElement = (Element) metaDataNode;
		            	
		            	NodeList codesNodes = metaDataElement.getElementsByTagName("codes");
		            	
		   	            boolean bTopicNode = false;
	
		   	            for (int jNode = 0; jNode < codesNodes.getLength(); jNode++)
		   	        	{
			   	            Node codesNode = codesNodes.item(jNode);
	
			   	            if (codesNode.getNodeType() == Node.ELEMENT_NODE)
			   	            {
			   	            	Element codesElement = (Element) codesNode;
				   	            
			   	            	// <codes class="bip:topics:1.0">
			   	            	if(codesElement.hasAttribute("class")
			   	            			&& codesElement.getAttribute("class").contains("topics"))
				   		        {
			   	            		bTopicNode = true;
				   	            	NodeList codeNodes = codesElement.getElementsByTagName("code");
				   	            	
				   	            	if(codeNodes.getLength() == 0)
					    	        {
					    	        	data += " ERROR_TOPIC";
					    	        }
				   	            	else
				   	            	{
						            	for (int kNode = 0; kNode < codeNodes.getLength(); kNode++)
						   	        	{
							   	            Node codeNode = codeNodes.item(kNode);
		
							   	            if (codeNode.getNodeType() == Node.ELEMENT_NODE)
							   	            {
							   	               Element codeElement = (Element) codeNode;
							   	               
							   	               //<code code="GCAT">
							   	               if(codeElement.hasAttribute("code"))
							   	               {
							   	            	   data += " " + codeElement.getAttribute("code");
							   	               }
							   	               else
							   	               {
							   	            	   data += " ERROR_TOPIC";
							   	               }
							   	            }
						   	        	}
				   	            	}
				   		        }
			   	            }
		   	        	}
		   	            
		   	            if (!bTopicNode)
		   	            {
		    	        	data += " ERROR_TOPIC";
		    	        }
		            }
		        }
	        }
		} 
		catch (SAXException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ParserConfigurationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data;
	}
}
