//Internal packages/libraries needed
import java.io.*;
import java.util.*;

public class report {
    public static void main(String[] args) throws FileNotFoundException {
        //Please refer to readme for overview/complete information of this code
		
		String dir1 = new String();
		String dir2 = new String();
		
		if(args.length > 1) {
			dir1 = args[0];
			dir2 = args[1];
		} else {
			throw IllegalArgumentException ("No input/output directory given")
		}
		
		//Summary roadmap--------------------------------------------------------------
		//Read input --> find relevant columns associated with product, date, and company, disregard all other entries
		//Construct an arraylist whose size matches the number of complaints in input, and every element is a string that consists of the product, date received, and company of that specific complaint only
		//Sort the previous arraylist alphabetically
		//Iterate through arraylist, and generate output arraylist during iteration by calculating relevant metrics
		//Generate output csv (report.csv) from output arraylist
		//-----------------------------------------------------------------------------
		
		//Variables/objects associated with reading the header of the input file
		int [] headerIndex = {-1, -1, -1};	
		String header = new String(); 		
		
		//Variables/objects associated with reading the rest of the input file
		int lineReader = 1;					
		String previousLine = new String();	
		String currentLine = new String();
		boolean multipleLines = false;
		
		//Variables/objects for extracting relevant information from input
		List <String> outputCompiled = new ArrayList <String> ();	
		//Each string element of outputCompiled is as: [product (lower cased)] AND [date] AND [company (lower cased)] - outputCompiled will also be sorted alphabetically
		
		//Variables/objects for calculating relevant metrics and generating output
		List <String> outputDate = new ArrayList <String> ();
		List <String> outputProduct = new ArrayList <String> ();
		List <String> outputCompany = new ArrayList <String> ();
		//Each list above will have the same size, where outputDate.get(i), outputProduct.get(i), and outputCompany.get(i) comes from the same line of complaint from input csv
		//At the same index, each element of 3 lists are essentially parsed from the outputCompiled list of the same index
		
		//This list contains the final output for report.csv, actual csv is generated from iterating through this list and printing out every line
		List <String> finalOutput = new ArrayList <String> ();
		
		
		//Section 1: this section is meant to import input file, and parse out every line in 2 different ways
		BufferedReader br = new BufferedReader(new FileReader (dir1));//need to change directory here
		try{
			while ((currentLine = br.readLine()) != null){
				
				//For loop debugging
				System.out.println("iterates");
				//System.out.println(multipleLines);
				
				if(lineReader == 1) { //We're reading the header --> code will first figure out the relevant column numbers
					String[] headers = currentLine.split(","); //initialize column with dummy ints	
					for(int i = 0; i < headers.length; i++){
						switch(headers[i]) {
						case "Product": headerIndex[0] = i;
							break;
						case "Date received": headerIndex[1] = i;
							break;
						case "Company": headerIndex[2] = i;
							break;
						}
					}
				
				} else {
				
					//Edge case 1: Empty line --> go to next line
					if(currentLine.length() == 0) {
						lineReader++;
						continue;
					}
					
					int quoteIndex = currentLine.indexOf("\"");

					//Find all quotation mark index for each new line
					ArrayList <Integer> quoteIndices = new ArrayList <Integer>();
					while(quoteIndex >= 0){
						quoteIndices.add(Integer.valueOf(quoteIndex));
						quoteIndex = currentLine.indexOf("\"", quoteIndex+1);
					}
					
					for(int i = 0; i < quoteIndices.size(); i ++) {
						System.out.println(quoteIndices.get(i));
					}
					
					//Check for total number of quotation marks in this line - odd number of quotation marks indicate multiple lines of entries consisting of 1 complaint
					if(quoteIndices.size() % 2 != 0 && !multipleLines) {
						multipleLines = true;
						previousLine = currentLine;
						continue;
					}
									
					if(multipleLines){
						if(currentLine.indexOf("\"") < 0){
							lineReader++; //we're still going through consumer feedback, go to next line
							continue;
						} else {
							//You've found your missing quotation, some of the user complaint content gets ignored, you now have a complete line of complaint, proceed as usual
							currentLine = previousLine + currentLine.substring(currentLine.indexOf("\""), currentLine.length());
							
							//Need to re-count quotation marks as you just appended extra contents from previous lines
							quoteIndices.clear();
							quoteIndex = currentLine.indexOf("\"");
							while(quoteIndex >= 0){
								//System.out.println(quoteIndex);
								quoteIndices.add(Integer.valueOf(quoteIndex));
								quoteIndex = currentLine.indexOf("\"", quoteIndex+1);
							}
							
							multipleLines = false;
						}
					}

					int i = 0;
					int j = i + 1;
						
					while (j < quoteIndices.size()){
						
						String replacement = currentLine.substring(quoteIndices.get(i),quoteIndices.get(j));
						replacement = replacement.replaceAll(",","^"); //this is not very good coding convention...
							
						currentLine = currentLine.substring(0, quoteIndices.get(i)) + replacement + currentLine.substring(quoteIndices.get(j));
						
						i = i + 2;
						j = j + 2;
					}
						
					//Method #3 - split the line, and start constructing the 2D array for data analysis, and replace every element's caret back into comma
					List<String> lineEntries = Arrays.asList(currentLine.split(","));
					
					String summarizedLine = lineEntries.get(headerIndex[0]).replaceAll("\\^",",").toLowerCase() + " AND " + lineEntries.get(headerIndex[1]).replaceAll("\\^",",").toLowerCase() + " AND " + lineEntries.get(headerIndex[2]).replaceAll("\\^",",").toLowerCase();
					outputCompiled.add(summarizedLine);
				}
				lineReader++;
			}
		} catch (IOException e) {
			System.out.println("Having problems reading input file");
		}
		
		//Edge case 2 --> file only has header
		if(outputCompiled.size() == 0){
			System.out.println("Input file is empty after header");
			try{
				FileWriter csvWriter = new FileWriter("report.csv");
			
				csvWriter.append("Input file is empty after header");
			
				csvWriter.flush();
				csvWriter.close();			
			} catch(IOException e) {
				System.out.println("Cannot write file");
			}
			return;
		}
		
		//Sort compiled list
		Collections.sort(outputCompiled);

		for(int i = 0; i < outputCompiled.size(); i++) {

			List<String> currentSummarizedLine = Arrays.asList(outputCompiled.get(i).split(" AND "));
			
			outputProduct.add(currentSummarizedLine.get(0));
			outputDate.add(currentSummarizedLine.get(1).substring(0,4)); //you care about the year ONLY --> throw away rest of the "date" category
			outputCompany.add(currentSummarizedLine.get(2));
			
		}
		
		//After this point, outputProduct, outputDate, and outputCompany all have the same size. And elements at the same index corresponds to each other across the 3 arraylists
		//This makes subsequent calculation and output much easier - we'll compile, evaluate and construct output at the same time, all with the same while loop
		
		int counter = 1;
		
		String currentProduct = outputProduct.get(0);
		String currentDate = outputDate.get(0);
		String currentCompany = outputCompany.get(0);
		
		int totalComplaintCount = 1;
		int currentTotalUniqueCompanyCount = 1;
		double currentSingleCompanyCount = 1; //for percentage
		double currentMaxSingleCompanyCount = 1; //for percentage
		
		while(counter < outputProduct.size()) {
			
			if(outputProduct.get(counter).equals(currentProduct)) {
				
				if(outputDate.get(counter).equals(currentDate)){
						
						//same product, same year, add total complaint count
						totalComplaintCount++;
						
						if(outputCompany.get(counter).equals(currentCompany)) {
							
							//Add to the single 
							currentSingleCompanyCount++;
							
							if(currentSingleCompanyCount > currentMaxSingleCompanyCount){
								currentMaxSingleCompanyCount = currentSingleCompanyCount;
							}
						
						} else {
							
							currentTotalUniqueCompanyCount++;
							currentSingleCompanyCount = 1;
						
						}
				
				} else {
					
					//summarize previous line
					finalOutput.add(currentProduct + "," + currentDate + "," + totalComplaintCount + "," + currentTotalUniqueCompanyCount + "," + Math.round(((currentMaxSingleCompanyCount / (double)totalComplaintCount)) * 100.0));
					//reset variables
					totalComplaintCount = 1;
					currentTotalUniqueCompanyCount = 1;
					currentMaxSingleCompanyCount = 1;
				
				}
			} else {
				
				finalOutput.add(currentProduct + "," + currentDate + "," + totalComplaintCount + "," + currentTotalUniqueCompanyCount + "," + Math.round(((currentMaxSingleCompanyCount / (double)totalComplaintCount)) * 100.0));
				//reset variables
				totalComplaintCount = 1;
				currentTotalUniqueCompanyCount = 1;
				currentMaxSingleCompanyCount = 1;
			
			}
							
			currentProduct = outputProduct.get(counter);
			currentDate = outputDate.get(counter);
			currentCompany = outputCompany.get(counter);
			
			counter ++;
		}
		
		finalOutput.add(currentProduct + "," + currentDate + "," + totalComplaintCount + "," + currentTotalUniqueCompanyCount + "," + Math.round(((currentMaxSingleCompanyCount / (double)totalComplaintCount)) * 100.0));
		
		for(int i = 0; i < finalOutput.size(); i++) {
			System.out.println(finalOutput.get(i));
		}
		
		//Write the arraylist into a CSV
		try{
			FileWriter csvWriter = new FileWriter("dir2");
			
			for(int i = 0; i < finalOutput.size(); i++) {
				csvWriter.append(finalOutput.get(i));
				csvWriter.append("\n");
			}
			
			csvWriter.flush();
			csvWriter.close();
			
		} catch(IOException e) {
			System.out.println("Cannot write file");
		}
		
    }
	/*public static int [] defineRelevantHeaderElements (String header){
		int counter = 0;
		while (counter <= 3) //We're looking for 3 different elements: Date received, product, company;
		
	}*/
}