package packg.file.parse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyUtilities {
	public static List<String> readFile(String fileName) {
		String line = null;
		List<String> file = null;
		try {
           // FileReader reads text files in the default encoding.
           FileReader fileReader = new FileReader(fileName);

           // Always wrap FileReader in BufferedReader.
           BufferedReader bufferedReader = new BufferedReader(fileReader);
           file = new ArrayList<String>();
           while((line = bufferedReader.readLine()) != null) {
        	   file.add(line);
           }
           
           // Always close files.
            bufferedReader.close();
		}
        catch(FileNotFoundException ex) {
        	ex.printStackTrace();
        }
        catch(IOException ex) {
        	ex.printStackTrace();                  
        }
		
		return file;
	}

	public static boolean isNumberInBetween(int number, int start, int end) {
		if(number > start && number < end) {
			return true;
		}
		else {
			return false;
		}
	}
}
