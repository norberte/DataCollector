package dataCollector;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CSV_Files {
	private String CSV_FULL_HEADER = "ID,airlineName,rating,authorOfReview,dateOfReview,originalityOfReviewer,aircraft,travellerType,cabinFlown,route,seatComfort,cabinStaffService,food&Beverages,inflightEntertainment,groundService,wifi&Connectivity,valueForMoney,recommended,text";
	private final String FULL_CSV_PATH = "C:/Users/Norbert/Desktop/MIs.csv";
	private final String txtFile = "C:/Users/Norbert/Desktop/reviews.txt";
	private BufferedWriter writeEverythingToCSV = new BufferedWriter(new FileWriter(FULL_CSV_PATH,true));
	private BufferedWriter writeTextToTXT = new BufferedWriter(new FileWriter(txtFile,true));
	private String content;
	// private String previousContent;
	private String reviewsFrom1Airline;
	private int ID_prefix, ID_sufix, ID;
	
	public CSV_Files() throws IOException {
		
	}
	
	public int getIDPrefix(){
		return DataCollector.getIDCode();
	}
	
	public void setIDSufix(int num){
		ID_sufix = num;
	}
	
	public void createID(){
		ID_prefix = getIDPrefix();
		ID = (ID_prefix * 1000) + ID_sufix;
	}
	
	public int getID(){
		return ID;
	}
	
	public void closeStream(){
		try {
			writeEverythingToCSV.close();
			writeTextToTXT.close();
		} catch (IOException e) {
			System.out.println("Could not close stream !");
		}
	}
	
	private String getFullCSVHeader(){
		return CSV_FULL_HEADER;
	}
	
	public void writeFullHeaderToCSV(){
		try {
			writeEverythingToCSV.write(getFullCSVHeader());
			writeEverythingToCSV.newLine();
		} catch (IOException e) {
			System.out.println("ERROR ! Could not write the CSV header to CSV file !");
			e.printStackTrace();
		}
	}
	
	public void collectReviews(String content, boolean endOfReviews){
		reviewsFrom1Airline = reviewsFrom1Airline + content + System.lineSeparator(); 
		// "*** * ***" is my review separator
		if(endOfReviews){
			writeTextToTXTFile(reviewsFrom1Airline);
		}
	}
	
	public void writeTextToTXTFile(String reviews){
		try {
			writeTextToTXT.write(reviews);
			writeTextToTXT.newLine();
			writeTextToTXT.write("### # ###"); // "### # ###" is my airline separator
			writeTextToTXT.newLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error ! Could not write reviews to txt file.");
		}
	}
	
	public void writeAllDataToCSV(String[] data){
		createID();
		content = getID() + ",";
		for (int i = 0; i < data.length; i++) {
			if(i == data.length - 1){
				content = content + data[i];
			} else {
				content = content + data[i] + ",";
			}
		}
		try {
			writeEverythingToCSV.write(content);
			writeEverythingToCSV.newLine();	
		} catch (IOException e) {
			System.out.println("Error ! Could not write content to CSV file !");
			e.printStackTrace();
		}
	}
	
	public String getPathToFullCSV(){
		return FULL_CSV_PATH;
	}
	
}
