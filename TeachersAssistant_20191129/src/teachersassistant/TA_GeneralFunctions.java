/**
 * FileName: TA_AlertPage.java
 * Author: Stephen James
 * Date: 11/12/19
 * Course: CMSC-495
 * 
 * Objective: To create a pop-up window used to prompt the user of errors/warnings.
*/

// Package
package teachersassistant;

// Main Class

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;

public class TA_GeneralFunctions {
    
    // Method to check a configuration file for the program to cache the school name and other informaiton
    // Create checkConfig() method - use a File object as a param (param will come from the settings page where the path is stored)
    public static TreeMap<String, String> checkConfig(){    
        TreeMap<String, String> configContents = new TreeMap<>();
        // Try opening the TA_Config.txt file
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader("C:\\ProgramData\\TeachersAssistant\\TA_Config.txt"));
            String lineText = null;
            
            // Iterate through the file
            while((lineText = reader.readLine()) != null){
                
                if(lineText.isEmpty())
                    continue;
                else{
                    if(lineText.contains("=")){
                        switch(lineText.substring(0 ,lineText.indexOf("="))){
                            case "~SchoolName":
                                configContents.put("SchoolName", lineText.substring(lineText.indexOf("=")+1));
                                break;
                            default:
                                System.out.println("No default Database found.");
                                break;
                        }
                    }
                    
                }
                
            }
            // If no SchoolName is found, return null
            return configContents;
            
        }
        catch(FileNotFoundException f){
            System.out.println("File not found. Running startup process again...");
            // configureTA();
            f.printStackTrace();
            return null;
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
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
                                //writer.append(schoolName_ARG);
                                //lineText.replace("=", "=" + schoolName_ARG);
                                //writer.flush();
                                writer.write(schoolName_ARG);
                                writer.flush();
                                break;
                            default:
                                System.out.println("School not found yet...");
                                break;
                        }
                    }
                    else{
                        // Do nothing
                    } 
                }
            }
            // Return 
            return true;
        }
        catch(FileNotFoundException f){
            System.out.println("File not found. Running startup process again...");
            // configureTA();
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
        
        String greeting = "# WARNING: Do NOT delte this file. If this file is deleted, the configuration process must be redone.#\n" +
                            "#													#\n" +
                            "#													#\n" +
                            "# This TA_Config.txt file is the configuration file used for the Teachers Assistant applicaiton.	#\n" +
                            "# 	Data preceeded by \"~\" are configuration variables. Contact your designated school TA Admin	#\n" +
                            "# 	with any questions should you need to edit this file's contents.				#\n" +
                            "#########################################################################################################";
        
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
            writer.println(greeting);
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
