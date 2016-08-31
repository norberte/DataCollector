package dataCollector;

import java.io.IOException;




import javax.xml.parsers.ParserConfigurationException;

public class DataCollector {
	static String[] xPathCalls;
	static String[] data;
	static String[] rawData;
	static int numberOfPages;
	static int reviewsOnLastPage;
	static int reviewCount;
	static String xml_path;
	static String review_text;
	static String[] airlineReview_links;
	static int[] IDcodes;
	
	static int i,j,k,l, reviewCounter;
	static String info;
	
	static HTML_XMLParser myParser = new HTML_XMLParser();
	static Initializer myInitializer = new Initializer();
	static Xpath_Storage myXPath = new Xpath_Storage();
	
	public static void main(String[] args) throws IOException, ParserConfigurationException, Exception {
		CSV_Files myCSV = new CSV_Files();
		WebScraper myScraper = new WebScraper();
		
		myInitializer.initializeLinks();
		airlineReview_links = myInitializer.getLinks();
		IDcodes = myInitializer.getID_codes();

		for (i = 0; i < airlineReview_links.length; i++) { // keeps track of web sites
			try {
				numberOfPages = myScraper.getNumberOfPagesPerLink(airlineReview_links[i]);
				if(numberOfPages == -1){
					myScraper.writeToExceptionLog("$$$$$ BAD LINK that was not evaluated: " + airlineReview_links[i]);
					continue;
				}
				reviewsOnLastPage = myScraper.getRemainingReviews();
				if(numberOfPages > 10){
					numberOfPages = 10;
					reviewsOnLastPage = 99;
				} else if(numberOfPages == 10 && reviewsOnLastPage == 100){
					reviewsOnLastPage = 99;
				}
				
				System.out.println("Airline link = "+ airlineReview_links[i]);
				System.out.println("Number of pages = " + numberOfPages);
				System.out.println("Reviews on last page = " + reviewsOnLastPage);
			} catch (IOException e) {
				System.out.println("ERROR ! Could not get the page number from WebScraper.");
				myScraper.writeToExceptionLog("##### MISSING LINK that was not evaluated: " + airlineReview_links[i]);
				continue;
			}
			
			for (j = 1; j <= numberOfPages; j++) { // keeps track of number of pages
				if(numberOfPages == 1){
					airlineReview_links[i] += "?sortby=post_date%3ADesc&pagesize=100";
				}
				xml_path = myParser.parseHTML(airlineReview_links[i], j);
				if(xml_path.equals("Bad Link")){
					myScraper.writeToExceptionLog("$$$$$ BAD LINK that was not evaluated: " + airlineReview_links[i]);
					continue;
				}
				System.out.println("Page = " + j);
				if(j == numberOfPages){
					System.out.println();
				}
				
				for (reviewCounter = 1; reviewCounter <= 100; reviewCounter++) { // counts up to 100 per page
					myCSV.setIDSufix(((j-1) * 100) + reviewCounter);
					if(j == numberOfPages && reviewCounter > reviewsOnLastPage){
						break;
					}
					xPathCalls = myXPath.getXPathArray(reviewCounter);
					data = new String[18];
					for (k = 0; k < xPathCalls.length; k++) { // takes care of scraping the data
						// for collecting text reviews only
						//if(k == 5){
						//	review_text = myScraper.getReviewTextData(xPathCalls[k], xml_path); // review text only
						//	if(j == numberOfPages && reviewCounter == reviewsOnLastPage){
						//		myCSV.collectReviews(review_text, true); 
						//	} else {
						//		myCSV.collectReviews(review_text, false);
						//	}
						//	review_text = "";
						//}
						
						switch(k){
							case 0: data[0] = myScraper.getDataByTextValue(xPathCalls[k], xml_path).replaceAll(",", ";"); break; // airline
							case 1: break; // 1 is review object
							case 2: data[1] = myScraper.getDataByTextValue(xPathCalls[k], xml_path); break; // rating
							case 3: data[2] = myScraper.getDataByTextValue(xPathCalls[k], xml_path).replaceAll(",", ";"); break; // author
							case 4: data[3] = myScraper.getExtractionData(xPathCalls[k], xml_path).replaceAll(",", ";"); break; // extraction -> special case (country#date)
							case 5: data[17] = appendDQ(myScraper.getReviewTextData(xPathCalls[k], xml_path).replaceAll("\"", "'").replaceAll("//t", "").replaceAll("  ", "")); break; // review text in quotation
						default : System.out.println("Something went wrong when getting the data.");
						}
					}
					 //splitting the extraction
					data[3] = data[3].replaceAll("\\n",""); // get rid of extra new line in the extraction
					data[3] = data[3].replaceAll("\\t",""); // get rid of extra tabs in the extraction
					
					data[4] = myScraper.getCountryData(data[3]); // country of originality of reviewer extracted
					data[3] = myScraper.getDateData(data[3]); // publishing date of the review extracted
					
					rawData = myScraper.getDataSet(xPathCalls[1], xml_path);
					
					for (l = 0; l < rawData.length; l++) {
						data[l + 5] = rawData[l]; // joining data and rawData all into data
					}
					
					if(i == 0 && j == 1 && reviewCounter == 1){ // create the header, but only at the beginning of the file
						myCSV.writeFullHeaderToCSV(); // if you want a CSV file with all the data
					}
					myCSV.writeAllDataToCSV(data); 
					//myCSV.collectReviews(review_text, false); // for text reviews only
				}
			}
		}
		myCSV.closeStream();
		myScraper.closeStream();
		
		// calculate average time
	}
	private static String appendDQ(String str) {
	    return "\"" + str + "\"";
	}
	
	public static int getIDCode(){
		return IDcodes[i];
	}
	
	public static String getInfo(){
		info = "Website: " + airlineReview_links[i] + System.lineSeparator();
		info = info + "Page Number : " + j + System.lineSeparator();
		info = info + reviewCounter + ".th review on the page" + System.lineSeparator();
		switch(k){
		case 0: info = info + "The problem is with the airline name." + System.lineSeparator(); break;
		case 1: info = info + "The problem is with the xPath."  + System.lineSeparator(); break;
		case 2: info = info + "The problem is with the rating." + System.lineSeparator(); break;
		case 3: info = info + "The problem is with the author." + System.lineSeparator(); break;
		case 4: info = info + "The problem is the extraction." + System.lineSeparator(); break;
		case 5: info = info + "The problem could be with the review text or with data below the review." + System.lineSeparator(); break;
		}
		return info;
	}
	/*
	 * switch for Manual Input ;
	 * numberOfPages = 1;
			
			switch(i){
			case 0: case 1: case 2: case 3: case 4: reviewsOnLastPage = 1; break;
			case 5: case 6: case 7: case 8: reviewsOnLastPage = 10; break;
			case 9: case 10: case 11: case 12: case 13: reviewsOnLastPage = 2; break;
			case 14: case 15: case 16: case 17: case 18: case 19: case 36: reviewsOnLastPage = 3; break;
			case 20: case 21: case 22: case 23: case 24: case 25: case 26: reviewsOnLastPage = 4; break;
			case 27: reviewsOnLastPage = 5; break;
			case 28: reviewsOnLastPage = 6; break;
			case 29: case 30: case 31: reviewsOnLastPage = 7; break;
			case 32: reviewsOnLastPage = 8; break;
			case 33: case 34: case 35: reviewsOnLastPage = 9; break;
			default: System.out.println("Error in the switch !");
			}
	 * 
	 * 
	private static void displayTime(ArrayList <Double> marks){
		Iterator<Double> myIterator = marks.iterator();
		
		while(myIterator.hasNext()){
			System.out.print(myIterator.next() + " ... ");
		}
	}

	private static double calculateAverage(ArrayList <Double> marks) {
		  Double sum = 0.0;
		  if(!marks.isEmpty()) {
		    for (Double mark : marks) {
		        sum += mark;
		    }
		    return sum.doubleValue() / marks.size();
		  }
		  return sum;
		}
		
		*/
}
