package dataCollector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Initializer { 
	private String[] websites;
	private int[] ID;
	private int counter, websiteCounter = 0;
	private String line;
	private int index;
	private Scanner fileReader;
	private final String WEBSITES_FILE_PATH = "C:/Users/Norbert/Desktop/Code_Back_Up/Scraper&Crawler/websiteInitializer.txt";
	private final String XPATH_FILE_PATH = "C:/Users/Norbert/Desktop/Code_Back_Up/Scraper&Crawler/xPathInitializer.txt";
	
	public String[] getLinks(){
		return websites;
	}
	
	public int[] getID_codes(){
		return ID;
	}
	
	public void initializeLinks(){
		try {
			fileReader = new Scanner( new File( WEBSITES_FILE_PATH ) );
		} catch (FileNotFoundException e) {
			System.out.println("Error ! Could not find the website initializer file");
			e.printStackTrace();
			System.exit(0);
		}
		
		counter = 0;
		while(fileReader.hasNextLine()){
			if(counter == 0){
				try{
					websiteCounter = Integer.parseInt(fileReader.nextLine());
				}
				catch(NumberFormatException ex){
					System.out.println("Error @ Could not parse the number in the website initializer file");
					websiteCounter = 164; // approximate how many web sites there will be
				}
				websites = new String[websiteCounter];
				ID = new int[websiteCounter];
			}
			line = fileReader.nextLine();
			index = line.indexOf("#");
			
			websites[counter] = line.substring(0, index - 1);
			
			try{
				ID[counter] = Integer.parseInt( line.substring(index + 1) );
			}
			catch(NumberFormatException ex){
				System.out.println("Error @ Could not parse ID_code in the website initializer file");
				ID[counter] = 100; // default error ID code
			}
			counter++;
		}
	}
	
	public String[] initializeXPaths() {
		try {
			fileReader = new Scanner( new File( XPATH_FILE_PATH ) );
		} catch (FileNotFoundException e) {
			System.out.println("Error ! Could not find the xPath initializer file");
			e.printStackTrace();
		}
		
		return null;
	}
}
