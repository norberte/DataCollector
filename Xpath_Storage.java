package dataCollector;

public class Xpath_Storage {
	private String[] data = new String[6];
	
	public String[] getXPathArray(int counter){
		data[0] = "//*[@id=\"main\"]/section[1]/div/section/div[2]/div[1]/div[1]/h1"; // airline
		data[1] = "//*[@id=\"main\"]/section[3]/div[1]/article/article[1]"; // default review xPath
		if(counter != 1){  
			data[1] = getIncrementedXPath(counter, data[1]); 
		}
		data[2] = data[1] + "/div[1]/span[1]"; // rating
		data[3] = data[1] + "/div[2]/h3/span"; // author
		data[4] = data[1] + "/div[2]/h3/text()[2]"; // extraction
		data[5] = data[1] + "/div[2]/div[1]/text()"; // text
		return data;
	}
	
	public String getIncrementedXPath(int counter, String xPath){
		int startIndex = xPath.lastIndexOf("[");
		return xPath.substring(0, startIndex+1) + counter + "]";
	}
}


