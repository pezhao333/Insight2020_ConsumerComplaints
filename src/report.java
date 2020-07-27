import java.io.*;
import java.util.*;

public class report {
    public static void main(String[] args) throws FileNotFoundException {
        //Please refer to readme for overview/complete information of this code
		
		String inputDirectory = new String();
		String outputDirectory = new String();
		
		if(args.length > 1) {
			inputDirectory = args[0];
			outputDirectory = args[1];
		} else {
			throw new IllegalArgumentException ("No input/output directory given");
		}
		
		//Summary roadmap--------------------------------------------------------------
		//Read input --> find relevant columns indices, from header row, that are associated with product, date, and company of each complaint, disregard all other entries of each complaint
		//Construct an arraylist whose size matches the number of complaints in input, and every element is a string that consists of the product, date received, and company of that specific complaint only (accounts for edge cases of multiple lines, various syntax irregularities in customer complaint entry field)
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
		
		BufferedReader br = new BufferedReader(new FileReader (inputDirectory));
		try{
			while ((currentLine = br.readLine()) != null){
				
				if(lineReader == 1) {
					String[] headers = currentLine.split(","); 
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

					//Find all quotation mark index for each new line --> skip quotation mark that is NOT surrounded by comma --> that's most likely a "false" quotation NOT indicative of the ending of a field
					ArrayList <Integer> quoteIndices = new ArrayList <Integer>();
					while(quoteIndex >= 0){
						
						if(quoteIndex + 1 < currentLine.length()) {
							if(quoteIndex != 0) {
								if(Character.compare(currentLine.charAt(quoteIndex - 1),',') != 0 && Character.compare(currentLine.charAt(quoteIndex + 1),',') != 0) {

									currentLine = currentLine.substring(0, quoteIndex) + currentLine.substring(quoteIndex+1);
									quoteIndex = currentLine.indexOf("\"", quoteIndex);
									continue;
								}
							}
						}			
						quoteIndices.add(Integer.valueOf(quoteIndex));
						quoteIndex = currentLine.indexOf("\"", quoteIndex+1);
					}
					
					//Check for total number of quotation marks in this line - odd number of quotation marks indicate multiple lines of entries consisting of 1 complaint
					
					if(quoteIndices.size() % 2 != 0 && !multipleLines) {
						multipleLines = true;
						previousLine = currentLine; //Assumption here is that only user feedback will give new lines and/or give notations outside of set rules
						continue;
					}
			
					if(multipleLines){
						int quoteIndexMultipleLines = currentLine.indexOf("\"");
						
						if(quoteIndexMultipleLines < 0){
							lineReader++; //we're still going through consumer feedback, go to next line
							continue;
						} else {							
							if(quoteIndexMultipleLines + 1 < currentLine.length()) {
								while(quoteIndexMultipleLines >= 0){			
									if(quoteIndexMultipleLines + 1 < currentLine.length()) {
										if(quoteIndexMultipleLines != 0) {
											if(Character.compare(currentLine.charAt(quoteIndexMultipleLines - 1),',') != 0 && Character.compare(currentLine.charAt(quoteIndexMultipleLines + 1),',') != 0) {
												currentLine = currentLine.substring(0, quoteIndexMultipleLines) + currentLine.substring(quoteIndexMultipleLines+1);
												quoteIndexMultipleLines = currentLine.indexOf("\"", quoteIndexMultipleLines);
												continue;
											}
											break; //When you get to here you know that the quotation mark is "real"
										}
									}			
									
								}
							}
							
							//You've found your missing quotation, some of the user complaint content gets ignored, you now have a complete line of complaint, proceed as usual
							currentLine = previousLine + currentLine.substring(quoteIndexMultipleLines, currentLine.length());
							//Need to re-count quotation marks as you just appended extra contents from previous lines
							quoteIndices.clear();
							quoteIndex = currentLine.indexOf("\"");

							while(quoteIndex >= 0){
						
								if(quoteIndex + 1 < currentLine.length()) {
									if(quoteIndex != 0) {
										if(Character.compare(currentLine.charAt(quoteIndex - 1),',') != 0 && Character.compare(currentLine.charAt(quoteIndex + 1),',') != 0) {
											//System.out.println("get here?");
											currentLine = currentLine.substring(0, quoteIndex) + currentLine.substring(quoteIndex+1);
											quoteIndex = currentLine.indexOf("\"", quoteIndex);
											continue;
										}
									}
								}			
							quoteIndices.add(Integer.valueOf(quoteIndex));
							quoteIndex = currentLine.indexOf("\"", quoteIndex+1);
							}
							multipleLines = false;
						}
					}

					//Iterate through the 1 line complaint and replace all commas within "" with a placeholder symbol so that split(".") can operate accurately
					int i = 0;
					int j = i + 1;
						
					while (j < quoteIndices.size()){
						
						String replacement = currentLine.substring(quoteIndices.get(i),quoteIndices.get(j));
						replacement = replacement.replaceAll(",","^"); //there are probably better 1 character symbols out there to replace "," and not be something that customers accidentally input...
							
						currentLine = currentLine.substring(0, quoteIndices.get(i)) + replacement + currentLine.substring(quoteIndices.get(j));
						
						i = i + 2;
						j = j + 2;
					}
						
					//Split the line, and start constructing the 2D array for data analysis, and replace every element's caret back into comma
					List<String> lineEntries = Arrays.asList(currentLine.split(","));
					
					String summarizedLine = lineEntries.get(headerIndex[0]).replaceAll("\\^",",").toLowerCase() + " AND " + lineEntries.get(headerIndex[1]).replaceAll("\\^",",") + " AND " + lineEntries.get(headerIndex[2]).replaceAll("\\^",",").toLowerCase();

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
				FileWriter csvWriter = new FileWriter(outputDirectory);
			
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
		
		//The rest of the function here depends on 2 key things:
		//(i) The ouputCompiled list is SORTED properly
		//(ii) outputProduct, outputDate, and outputCompany all have the same size. And elements at the same index corresponds to the same complaint across the 3 arrayLists
		
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
							
							currentSingleCompanyCount++;
							
							if(currentSingleCompanyCount > currentMaxSingleCompanyCount){
								currentMaxSingleCompanyCount = currentSingleCompanyCount;
							}
						
						} else {
							
							currentTotalUniqueCompanyCount++;
							currentSingleCompanyCount = 1;
						
						}
				
				} else {
					//Scenario where product matches but are from different years, so we start a new line
					finalOutput.add(currentProduct + "," + currentDate + "," + totalComplaintCount + "," + currentTotalUniqueCompanyCount + "," + Math.round(((currentMaxSingleCompanyCount / (double)totalComplaintCount)) * 100.0));
					
					//reset variables
					totalComplaintCount = 1;
					currentTotalUniqueCompanyCount = 1;
					currentMaxSingleCompanyCount = 1;
				
				}
			} else {
				//Scenario product is a new one, so we start a new line
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
		
		//For debugging
		for(int i = 0; i < finalOutput.size(); i++) {
			System.out.println(finalOutput.get(i));
		}
		
		
		//Write output into a CSV
		try{
			FileWriter csvWriter = new FileWriter(outputDirectory);
			
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
}
