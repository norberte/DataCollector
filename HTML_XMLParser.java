package dataCollector;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;

public class HTML_XMLParser {
	private File xmlFile; private Throwable x;
	private org.jsoup.nodes.Document jsoupDoc = null; 
	private W3CDom w3cDom; private org.w3c.dom.Document w3cDoc;
	private int index, endIndex,index_2; 
	private boolean timeOut;
	private String xml_path, fileName; 
	private HtmlCleaner cleaner; private CleanerProperties props; 
	private PrettyXmlSerializer xml; private TagNode tagNode;
	private TransformerFactory tFactory; private Transformer transformer; 
	private DOMSource source; private StreamResult result;
	
	public String parseHTML(String link, int pageNumber) throws IOException{
		timeOut = true;
		index = link.indexOf("/", 40);
		endIndex = link.indexOf("/", index + 1);
		fileName = link.substring(index + 1, endIndex) + "_" + pageNumber;
		
		// get next page if it is not 1
		if(pageNumber != 1){
			link = getNextPage(link, pageNumber);
		}
		// if it is the first page, the xml file is already created when 
		// WebScraper.getNumberOfPagesPerLink(String link) is executed
		
		// IMPROVE HERE !!!
		while(timeOut){
			try {
				jsoupDoc = Jsoup.connect(link).get();
				timeOut = false;
			} catch(SocketTimeoutException ex){
				timeOut = true;
			} catch(MalformedURLException exx){
				System.out.println("ERROR in getting the jSoup DOM from " + link);
				return "Bad Link";
			} catch (IOException e) {
				timeOut = true;
			}
		}
		
		w3cDom = new W3CDom();
		w3cDoc = w3cDom.fromJsoup(jsoupDoc);
		

		xmlFile = new File("C:/Users/Norbert/Desktop/XML Files/" + fileName + ".xml");
		try {
			tFactory = TransformerFactory.newInstance();
			transformer = tFactory.newTransformer();

			source = new DOMSource(w3cDoc);
			result = new StreamResult(xmlFile);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException tce) {
			System.out.println("* Transformer Factory error");
			System.out.println(" " + tce.getMessage());
			x = tce;
			if (tce.getException() != null)
				x = tce.getException();
			x.printStackTrace();
		} catch (TransformerException te) {
			System.out.println("* Transformation error");
			System.out.println(" " + te.getMessage());
			x = te;
			if (te.getException() != null)
				x = te.getException();
			x.printStackTrace();
		}

		cleaner = new HtmlCleaner();
		props = cleaner.getProperties();
		props.setTranslateSpecialEntities(true); // set some properties to non-default values
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);

		xml = new PrettyXmlSerializer(props);
		tagNode = new HtmlCleaner(props).clean(xmlFile); // do parsing

		// serialize to xml file
		xml_path = "C:/Users/Norbert/Desktop/XML Files/" + fileName + ".xml";
		xml.writeToFile(tagNode, xml_path, "utf-8");

		return xml_path;
	}
	
	private String getNextPage(String link, int pageNumber){
		//http://www.airlinequality.com/airline-reviews/air-canada/?sortby=post_date%3ADesc&pagesize=100
		//http://www.airlinequality.com/airline-reviews/air-canada/page/2/?sortby=post_date%3ADesc&pagesize=100
		index_2 = link.indexOf("?");
		return link.substring(0, index_2) + "page/" + pageNumber + "/" + link.substring(index_2);
		 
	}
	
}
