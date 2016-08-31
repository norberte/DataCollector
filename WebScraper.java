package dataCollector;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class WebScraper {
	private int numberOfReviews;
	private String xml_path;
	//private final String X_PATH = "//*[@id=\"main\"]/section[1]/div/section/div[2]/div[2]/div[2]/div[1]/span"; // for number of pages per airline
	private final String X_PATH = "//*[@id=\"main\"]/section[4]/div/article/div/text()";
	private NodeList childNodes;
	private String texxt; 
	private int indexx, indexx_1, indexx_2;
	private org.w3c.dom.Document dc; 
	private NodeList nodes; 
	private javax.xml.xpath.XPath newXPath;
	private String extraction;
	private NamedNodeMap attributes;
	private String data;
	private String dataDescription;
	private String myXPath;
	private String awesomeXPath;
	private double pages;
	private int index, index_2, index3;
	private int remainingReviews = 0;
	private int starNumber;
	private String[] rawData = new String[12];
	private int crashNumber = 0;
	private String crashLog;
	private final String exceptionLog = "C:/Users/Norbert/Desktop/Code_Back_Up/Scraper&Crawler/exceptionLog.txt";
	private BufferedWriter writeToExceptionLog = new BufferedWriter(new FileWriter(exceptionLog,true));
	
	private static HTML_XMLParser myParser = new HTML_XMLParser();
	public WebScraper() throws IOException{
		numberOfReviews = 0;
	}
	
	public void writeToExceptionLog(String content){
		crashNumber++;
		try {
			writeToExceptionLog.write("NullPointer " + crashNumber);
			writeToExceptionLog.newLine();
			writeToExceptionLog.write(content);
			writeToExceptionLog.newLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error ! Could not write nullPointers to exceptionLog file.");
		}
	}
	
	public void closeStream(){
		try {
			writeToExceptionLog.close();
		} catch (IOException e) {
			System.out.println("Could not close stream !");
		}
	}
	
	public String[] getDataSet(String base_XPath, String fileName){
		for (int i = 0; i < rawData.length; i++) {
			rawData[i] = "NA";
		}
		try {
			dc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileName);
			newXPath = XPathFactory.newInstance().newXPath();
			nodes = (NodeList) newXPath.evaluate(base_XPath + "/div[2]/div[2]/table/tbody", dc, XPathConstants.NODESET );
			childNodes = nodes.item(0).getChildNodes();
		} catch (NullPointerException ex){
			writeToExceptionLog(DataCollector.getInfo());
		} catch (Exception ex) {
			System.out.println("ERROR! Could not get the raw data!");
			Logger.getLogger(WebScraper.class.getName()).log(Level.SEVERE, null, ex);
		}

		for (int j = 0; j < childNodes.getLength(); j++) {
			try{
				dataDescription = childNodes.item(j).getTextContent().replaceAll("\\s","");
				myXPath = org.joox.JOOX.$(childNodes.item(j)).xpath();
				
				if(dataDescription.equals("")){
					continue;
				} else  {
					index = myXPath.lastIndexOf("article["); // reference to "a" in the last "article[1]"
					index_2 = myXPath.indexOf("/", index); // find "/" after index
				}
			}catch(NullPointerException ex){
				crashLog = DataCollector.getInfo();
				crashLog = crashLog + "XPath object content :" + dataDescription + System.lineSeparator();
				writeToExceptionLog(crashLog);
			}
			

			if(dataDescription.contains("Aircraft")){
				rawData[0] = getRawData(base_XPath,dc).replaceAll(",", "&");
			} else if(dataDescription.contains("TypeOfTraveller")){
				rawData[1] = getRawData(base_XPath,dc).replaceAll(",", "&");
			} else if(dataDescription.contains("CabinFlown")){
				rawData[2] = getRawData(base_XPath,dc).replaceAll(",", "&");
			} else if(dataDescription.contains("Route")){
				rawData[3] = getRawData(base_XPath,dc).replaceAll(",", "&");
			} else if(dataDescription.contains("Recommended")){
				rawData[11] = getRawData(base_XPath,dc);
			} else if(dataDescription.contains("SeatComfort")){
				rawData[4] = getStarData(base_XPath,dc);
			} else if(dataDescription.contains("CabinStaffService")){
				rawData[5] = getStarData(base_XPath,dc);
			} else if(dataDescription.contains("Food&Beverages")){
				rawData[6] = getStarData(base_XPath,dc);
			} else if(dataDescription.contains("InflightEntertainment")){
				rawData[7] = getStarData(base_XPath,dc);
			} else if(dataDescription.contains("GroundService")){
				rawData[8] = getStarData(base_XPath,dc);
			} else if(dataDescription.contains("Wifi&Connectivity")){
				rawData[9] = getStarData(base_XPath,dc);
			} else if(dataDescription.contains("ValueForMoney")){
				rawData[10] = getStarData(base_XPath,dc);
			}
		}
		return rawData;
	}
	
	public String getDataByAttributeValue(String xPath, String filePath){
		try {
			dc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filePath);
			newXPath = XPathFactory.newInstance().newXPath();
			int starNumber = 0;
			try{
				nodes = (NodeList) newXPath.evaluate(xPath, dc, XPathConstants.NODESET);
				data = nodes.item(0).getAttributes().item(0).getTextContent();
			}
			catch(NullPointerException ex){
				crashLog = DataCollector.getInfo();
				crashLog = crashLog + "XPath:" + xPath + System.lineSeparator();
				writeToExceptionLog(crashLog);
				return "#ERROR";
			}
			
			while (data.equals("star fill")) {
				int index = xPath.lastIndexOf("[");
				try {
					starNumber = Integer.parseInt(xPath.substring(index + 1,xPath.length() - 1));
				} catch (NumberFormatException ex) {
					System.out.println("ERROR !!! Can not convert star-span number to an integer!");
				}
				xPath = xPath.substring(0, xPath.length() - 2) + ++starNumber + "]";
				if(starNumber == 6){
					break;
				}
				nodes = (NodeList) newXPath.evaluate(xPath, dc,XPathConstants.NODESET);
				attributes = nodes.item(0).getAttributes();
				data = attributes.item(0).getTextContent();
			}
			return (starNumber - 1) + "";
		} catch (Exception ex) {
			Logger.getLogger(WebScraper.class.getName()).log(Level.SEVERE, null, ex);
		}
		return "#ERROR";
	}
	
	public int getNumberOfPagesPerLink(String link) throws IOException{
		xml_path = myParser.parseHTML(link, 1);
		if(xml_path.equals("Bad Link")){
			writeToExceptionLog("$$$$$ BAD LINK that was not evaluated: " + link);
			return -1;
		}
		try {
			dc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml_path);
			newXPath = XPathFactory.newInstance().newXPath();
			nodes = (NodeList) newXPath.evaluate(X_PATH, dc, XPathConstants.NODESET);
			//childNodes = nodes.item(0).getChildNodes();

			try{
				texxt = "";
				//numberOfReviews = Integer.parseInt(childNodes.item(0).getTextContent());
				texxt = nodes.item(0).getTextContent();
				indexx = texxt.indexOf("of");
				indexx_1 = texxt.indexOf(" ", indexx);
				indexx_2 = texxt.indexOf(" ", indexx_1+1);
				numberOfReviews = Integer.parseInt(texxt.substring(indexx_1 + 1, indexx_2));
				System.out.println("Number of reviews = " + numberOfReviews );
			}catch(NumberFormatException ex){
				System.out.println("ERROR ! String Not A Number");
			}
		} catch (Exception ex) {
			Logger.getLogger(WebScraper.class.getName()).log(Level.SEVERE, null, ex);
		}
		remainingReviews = numberOfReviews % 100;
		pages = numberOfReviews / 100.0;
		if(pages > 0.0){
			return (int) Math.ceil(pages);
		} else {
			System.out.println("ERROR! Something went really wrong with the number of reviews !");
			System.exit(0);
			return 0;
		}
	}

	public String getExtractionData(String xPath, String filePath){
		try {
			dc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filePath);
			newXPath = XPathFactory.newInstance().newXPath();
			nodes = (NodeList) newXPath.evaluate(xPath, dc, XPathConstants.NODESET);
			extraction = nodes.item(0).getTextContent();
			int startIndex = extraction.indexOf("(") ;
			int endIndex = extraction.indexOf(")");
			try{
				return extraction.substring(startIndex + 1, endIndex) + "#" + extraction.substring(endIndex + 2);
			}
			catch(NullPointerException ex){
				crashLog = DataCollector.getInfo();
				crashLog = crashLog + "XPath:" + xPath + System.lineSeparator();
				writeToExceptionLog(crashLog);
				return "NA#NA";
			}catch(StringIndexOutOfBoundsException ex){
				return "NA#" + extraction.replaceAll("\\t","").replaceAll("\\n", "");
			}
		} catch (Exception ex) {
			Logger.getLogger(WebScraper.class.getName()).log(Level.SEVERE, null, ex);
		}
		return "#ERROR";
	}
	
	private String getRawData(String base_XPath, org.w3c.dom.Document dc){
		newXPath = XPathFactory.newInstance().newXPath();
		awesomeXPath = base_XPath + myXPath.substring(index_2) + "/td[2]";
		try {
			nodes = (NodeList) newXPath.evaluate(awesomeXPath, dc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			System.out.println("Could not evaluate the new XPATH !");
			System.out.println("new path  = " + awesomeXPath);
			e.printStackTrace();
		}
		return nodes.item(0).getTextContent();
	}
	
	private String getStarData(String base_XPath, org.w3c.dom.Document dc){
		try {
			newXPath = XPathFactory.newInstance().newXPath();
			awesomeXPath = base_XPath + myXPath.substring(index_2) + "/td[2]/span[1]";
			starNumber = 0;			
			
			try {
				nodes = (NodeList) newXPath.evaluate(awesomeXPath, dc, XPathConstants.NODESET);
				if(nodes.item(0).getTextContent().equals("N/A")){
					return "NA";
				}
				data = nodes.item(0).getAttributes().item(0).getTextContent();
			} catch (XPathExpressionException e) {
				System.out.println("Could not evaluate the new XPATH !");
				System.out.println("new path  = " + awesomeXPath);
				e.printStackTrace();
			} catch(NullPointerException ex){
				crashLog = DataCollector.getInfo();
				if(!data.equals("") || !data.equalsIgnoreCase(null)){
					crashLog = crashLog + "XPath content :" + data + System.lineSeparator();
				}
				writeToExceptionLog(crashLog);
				
				if(nodes.item(0).getTextContent().equals("")){
					return nodes.item(0).getTextContent();
				} else {
					return "NA";
				}
			}
			
			while (data.equals("star fill")) {
				index3 = awesomeXPath.lastIndexOf("[");
				try {
					starNumber = Integer.parseInt(awesomeXPath.substring(index3 + 1,awesomeXPath.length() - 1));
				} catch (NumberFormatException ex) {
					System.out.println("ERROR !!! Can not convert star-span number to an integer!");
				}
				awesomeXPath = awesomeXPath.substring(0, awesomeXPath.length() - 2) + ++starNumber + "]";
				if(starNumber == 6){
					break;
				}
				nodes = (NodeList) newXPath.evaluate(awesomeXPath, dc ,XPathConstants.NODESET);
				data = nodes.item(0).getAttributes().item(0).getTextContent();
			}
			return (starNumber - 1) + "";
		} catch (Exception ex) {
			Logger.getLogger(WebScraper.class.getName()).log(Level.SEVERE, null, ex);
		}
		return "#ERROR";
	}
	
	public String getReviewTextData(String xPath, String filePath){
		try {
			dc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filePath);
			newXPath = XPathFactory.newInstance().newXPath();
			nodes = (NodeList) newXPath.evaluate(xPath, dc, XPathConstants.NODESET);
			return nodes.item(0).getTextContent();
		} catch (Exception ex) {
			Logger.getLogger(WebScraper.class.getName()).log(Level.SEVERE, null, ex);
		}
		return "#ERROR";
	}

	public String getDataByTextValue(String xPath, String filePath){
		try {
			dc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filePath);
			newXPath = XPathFactory.newInstance().newXPath();
			nodes = (NodeList) newXPath.evaluate(xPath, dc, XPathConstants.NODESET);
			childNodes = nodes.item(0).getChildNodes();
			return childNodes.item(0).getTextContent();
		} catch(NullPointerException ex){
			try {
				xPath = xPath.substring(0, xPath.lastIndexOf("/"));
				newXPath = XPathFactory.newInstance().newXPath();
				nodes = (NodeList) newXPath.evaluate(xPath, dc, XPathConstants.NODESET);
				if(nodes.item(0).getTextContent().equals("na")){
					return "NA";
				}
			} catch (XPathExpressionException e) {
				System.out.println("Wrong xPath in na-rating.");
			} catch (NullPointerException ee){
				// ignore NullPointer, since it will be written to the crashLog
			}
			crashLog = DataCollector.getInfo();
			crashLog = crashLog + "XPath:" + xPath + System.lineSeparator();
			writeToExceptionLog(crashLog);
			return "NA";
		} catch (Exception ex) {
			Logger.getLogger(WebScraper.class.getName()).log(Level.SEVERE, null, ex);
		}
		return "#ERROR";	
	}

	public String getCountryData(String data){
		return data.substring(0, data.indexOf("#"));
	}
	
	public String getDateData(String data){
		return data.substring(data.indexOf("#") + 1);
	}
	
	public int getRemainingReviews(){
		return remainingReviews;
	}

}
