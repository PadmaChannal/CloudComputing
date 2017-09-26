package packg.file.parse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataExtraction {

	public static void main(String[] args) {
		
		String fileName = "/2016 MS FALL/Courses/CSE5334 Data Mining/Assignments/UniversityDataSet/university.data.txt";
		 // This will reference one line at a time
        String line = null;

		try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            List<DataExtractionUniversityData> list = new ArrayList<DataExtractionUniversityData>();
            DataExtractionUniversityData data = null;
            String[] keyValue = null;
            while((line = bufferedReader.readLine()) != null) {
            	line = line.toLowerCase();
            	// If line starts with % (which is a comment) then ignore it
            	if(line.indexOf("%") == 0) {
            		continue;
            	}
            	if(line.contains("def-instance")) {
            		if(data != null) {
            			list.add(data);
            		}
            		data = new DataExtractionUniversityData();
            		keyValue = line.split("def-instance ");
            		data.setName(keyValue[1]);
            	}
            	else if(line.contains("state ")) {
            		keyValue = line.split("state ");
            		data.setState(keyValue[1]);
            	}
            	else if(line.contains("location ")) {
            		keyValue = line.split("location ");
            		data.setLocation(keyValue[1]);
            	}
            	else if(line.contains("control ")) {
            		keyValue = line.split("control ");
            		data.setControl(keyValue[1]);
            	}
            	else if(line.contains("no-of-students thous:")) {
            		keyValue = line.split(":");
            		data.setNumOfStudents(keyValue[1]);
            	}
            	else if(line.contains("male:female ratio:")) {
            		keyValue = line.split("ratio:");
            		data.setMaleFemaleRatio(keyValue[1]);
            	}
            	else if(line.contains("student:faculty ratio:")) {
            		keyValue = line.split("ratio:");
            		data.setStudentFacultyRatio(keyValue[1]);
            	}
            	else if(line.contains("sat verbal ")) {
            		keyValue = line.split("verbal ");
            		data.setSatVerbal(keyValue[1]);
            	}
            	else if(line.contains("sat math ")) {
            		keyValue = line.split("math ");
            		data.setSatMath(keyValue[1]);
            	}
            	else if(line.contains("expenses thous$:")) {
            		keyValue = line.split(":");
            		data.setExpensesInThousands(keyValue[1]);
            	}
            	else if(line.contains("percent-financial-aid ")) {
            		keyValue = line.split("aid ");
            		data.setPercentFinanceAid(keyValue[1]);
            	}
            	else if(line.contains("no-applicants thous:")) {
            		keyValue = line.split(":");
            		data.setNumApplicantsInThoudands(keyValue[1]);
            	}
            	else if(line.contains("percent-admittance ")) {
            		keyValue = line.split("admittance ");
            		data.setPercentAdmittance(keyValue[1]);
            	}
            	else if(line.contains("percent-enrolled ")) {
            		keyValue = line.split("enrolled ");
            		data.setPercentEnrolled(keyValue[1]);
            	}
            	else if(line.contains("academics scale:")) {
            		keyValue = line.split(":");
            		data.setAcademicsScale(keyValue[1]);
            	}
            	else if(line.contains("social scale:")) {
            		keyValue = line.split(":");
            		data.setSocialScale(keyValue[1]);
            	}
            	else if(line.contains("quality-of-life scale:")) {
            		keyValue = line.split(":");
            		data.setQualityOfLifeScale(keyValue[1]);
            	}
            	else if(line.contains("academic-emphasis ")) {
            		keyValue = line.split("emphasis ");
            		if(null!= data.getAcademicEmphasis() && !"".equalsIgnoreCase(data.getAcademicEmphasis()))
            			data.setAcademicEmphasis(data.getAcademicEmphasis() + ";" +(keyValue[1]));
            		else {
            			data.setAcademicEmphasis((keyValue[1]));
            		}
            	}
            }   
            list.add(data);
            
            // Always close files.
            bufferedReader.close();

            //display(list);
            convertListToCsv(list);
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");                  
        }

	}

	private static void convertListToCsv(List<DataExtractionUniversityData> list) {
		//System.out.println(list.size()); 
		System.out.println("Name,State,Location,Control,NumOfStudentsInThousands,MaleFemaleRatio,StudentFacultyRatio,SatVerbal,SatMath,ExpensesInThousand,"
				+ "PercentFinanceAid,NumApplicantsInThousands,PercentAdmittance,PercentEnrolled,AcademicsScale,SoclialScale,QualityOfLifeScale,AcademicEmphasis");
		for(DataExtractionUniversityData d: list) {
			System.out.println(
							d.getName() + "," +
							d.getState() + "," +
							d.getLocation() + "," +
							d.getControl() + "," +
							d.getNumOfStudents() + "," +
							d.getMaleFemaleRatio() + "," +
							d.getStudentFacultyRatio() + "," +
							d.getSatVerbal() + "," +
							d.getSatMath() + "," +
							d.getExpensesInThousands() + "," +
							d.getPercentFinanceAid() + "," +
							d.getNumApplicantsInThoudands() + "," +
							d.getPercentAdmittance() + "," +
							d.getPercentEnrolled() + "," +
							d.getAcademicsScale() + "," +
							d.getSocialScale() + "," +
							d.getQualityOfLifeScale() + "," +
							d.getAcademicEmphasis()
						);
		}
	}
}
