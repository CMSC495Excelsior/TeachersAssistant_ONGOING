/**
 * FileName: TA_GeneralFunctions.java
 * Author: Stephen James
 * Date: 11/27/19
 * Course: CMSC-495
 * 
 * Objective: To create a utility class for misc. functions (i.e. checking configuration files)
*/

// Package
package teachersassistant;

// Import statements
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;

// Main class
public class TA_GeneralFunctions {
    
    // Method to check a configuration file for the program to cache the school name and other informaiton.
    public static TreeMap<String, String> checkConfig(){  
        // Save tempTreeMap for the configuration contents
        TreeMap<String, String> configContents = new TreeMap<>();
        // Try opening the TA_Config.txt file
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader("C:\\ProgramData\\TeachersAssistant\\TA_Config.txt"));
            String lineText = null;
            
            // Iterate through the file
            while((lineText = reader.readLine()) != null){
                
                // Check the line contents
                if(lineText.isEmpty())
                    continue;
                else{
                    if(lineText.contains("=")){
                        switch(lineText.substring(0 ,lineText.indexOf("="))){
                            case "~SchoolName":
                                configContents.put("SchoolName", lineText.substring(lineText.indexOf("=")+1));
                                break;
                            default:
                                System.out.println("No configuration data found on current line.");
                                break;
                        }
                    }
                    
                }
                
            }
            // Return TreeMap (will be empty if no configuration data is found)
            return configContents;
            
        }
        // Catch all exceptions
        catch(FileNotFoundException f){
            System.out.println("Config file not found.");
            f.printStackTrace();
            return null;
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
        // Always try closing the reader object
        finally{
            try{
                if(reader != null){
                    reader.close();
                }
            }
            catch(Exception t){
                t.printStackTrace();
                return null;
            }
        }
    }
    
    
    // Method used to update the schoolName in the Config file
    public static boolean updateSchoolName(String schoolName_ARG){ 
        // String value for file path
        String filePath = "C:\\ProgramData\\TeachersAssistant\\TA_Config.txt";
        
        // Try opening the TA_Config.txt file
        BufferedReader reader = null;
        PrintWriter writer = null; 
        try{
            reader = new BufferedReader(new FileReader(filePath));
            String lineText = null;
            writer = new PrintWriter(new FileOutputStream(filePath, true));
            
            // Iterate through the file
            while((lineText = reader.readLine()) != null){
                
                if(lineText.isEmpty())
                    continue;
                else{
                    if(lineText.contains("=")){
                        switch(lineText.substring(0 ,lineText.indexOf("="))){
                            case "~SchoolName":
                                System.out.println("Updating school name from \' " + lineText.substring(lineText.indexOf("=")+1)
                                        + "\' to \'" + schoolName_ARG + "\'");
                                // NOTE: This causes the school name to be written on the next line.
                                writer.write(schoolName_ARG);
                                writer.flush();
                                break;
                            default:
                                System.out.println("School not found yet...");
                                break;
                        }
                    } 
                }
            }
            // Return 
            return true;
        }
        catch(FileNotFoundException f){
            System.out.println("Config file not found.");
            f.printStackTrace();
            return false;
        }
        catch(IOException e){
            e.printStackTrace();
            return false;
        }
        finally{
            // Close reader/writer
            try{
                if(reader != null)
                    reader.close();
                if(writer != null)
                    writer.close();
            }
            catch(Exception t){
                t.printStackTrace();
                return false;
            }
        }
    }
    
    
    // Method to completely re-create the Config file - used only when re-creating the DB
    public static boolean createConfig(String schoolName){
        // Temp String variables
        String filePath = "C:\\ProgramData\\TeachersAssistant\\TA_Config.txt";
        
        String[] greeting = {
            "# WARNING: Do NOT delte this file. If this file is deleted, the configuration process must be redone.   #\n",
            "#													#\n",
            "#													#\n",
            "# This TA_Config.txt file is the configuration file used for the Teachers Assistant applicaiton.	#\n",
            "# 	Data preceeded by \"~\" are configuration variables. Contact your designated school TA Admin	#\n",
            "# 	with any questions should you need to edit this file's contents.				#\n",
            "#########################################################################################################"
        };
        
        // Try opening the TA_Config.txt file to make sure it doesn't exist
        File oldConfig = null;
        PrintWriter writer = null;
        try{
            oldConfig = new File(filePath);
            
            // Delete the oldConfig if it exists
            if(oldConfig.exists())
                oldConfig.delete();
            
            // Create new config file
            writer = new PrintWriter(filePath);
            // Iterate through the entire greeting message and add each line to the file
            for(String lineText : greeting){
                // Add the line to the config file
                writer.println(lineText);
            }
            // Start printing variables
            writer.println();
            writer.println("# SchoolName attribute used to set the schoolName in the Teacher's Assistant app");
            writer.println("~SchoolName=" + schoolName);
            writer.flush();
            
            // Return
            return true;            
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        finally{
            try{
                if(writer != null)
                    writer.close();
            }
            catch(Exception t){
                t.printStackTrace();
                return false;
            }
        }
    }
    
}
