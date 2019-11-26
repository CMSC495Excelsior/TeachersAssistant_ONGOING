/**
 * FileName: TA_WordFunctions.java
 * Author: Stephen James
 * Date: 11/20/19
 * Course: CMSC-495
 * 
 * Objective: To create the Utility Class used to generate reports.
*/

// Package
package teachersassistant;

// Import statements
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javafx.scene.paint.Color;
import org.apache.log4j.BasicConfigurator;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

// Main class
public class TA_WordFunctions {
    
    // Class variables
    private static XWPFDocument document;
    private static File localFile;

    // Method to create the DEFAULT Student reports (includes the basic info on each student)
    public static void makeStudentReport_Default(ArrayList<TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>>> masterStudentList)throws Exception{
        // Capture the current date
        LocalDate currentDate = LocalDate.now();


        // Configure logging
        BasicConfigurator.configure();

        // Begin constructing the document
        try{            
            // Iterate through top level
            for(TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>> studentMap_TOP : masterStudentList){
                // Update Student Name (used for file naming)
                String studentFullName = "";
                // Create new document (one for each student)
                document = new XWPFDocument();
                
                // Iterate through TOP map entries
                for(Map.Entry<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>> studentIDMap : studentMap_TOP.entrySet()){
                    // Print out studentID
                    System.out.println("Student ID: " + studentIDMap.getKey());
                    // Create a paragraph for the title section
                    XWPFParagraph titleParagraph = makeNewParagraph(document,ParagraphAlignment.CENTER, true, 1000);
                    XWPFRun titleRun = makeNewParagraphRun(titleParagraph, "Report Card", true, 20, UnderlinePatterns.SINGLE);
                    
                    XWPFParagraph studentSectionTitlePara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 100);
                    XWPFRun studentSectionTitleRun = makeNewParagraphRun(studentSectionTitlePara, "Student Information:", true, 13, UnderlinePatterns.SINGLE);
                    
                    XWPFParagraph studentIDParagraph = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                    XWPFRun studentIDRun = makeNewParagraphRun(studentIDParagraph, "Student ID: " + studentIDMap.getKey(), false, 12, null);
                    
                    // Iterate through studentIDMap entries
                    for(TreeMap<String, ArrayList<TreeMap<String, String>>> studentMapList : studentIDMap.getValue()){
                        // Iterate through Map entries
                        for(Map.Entry<String, ArrayList<TreeMap<String, String>>> studentTopicMap : studentMapList.entrySet()){
                            // Count variable for formatting the WordDoc
                            int courseInfoMapCount = 0;
                            // Print Map name
                            System.out.println("\tMap Name: " + studentTopicMap.getKey());
                            // Iterate through each studentTopicMap
                            for(TreeMap<String, String> bottomTopicMap : studentTopicMap.getValue()){
                                // Enter Switch
                                switch(studentTopicMap.getKey()){
                                    case "StudentInformation":
                                        studentFullName = bottomTopicMap.get("LastName") + "_" + bottomTopicMap.get("FirstName") + studentIDMap.getKey(); 
                                        XWPFParagraph namePara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun nameRun = makeNewParagraphRun(namePara, "\tFull Name: " + bottomTopicMap.get("FirstName") + " " +
                                                bottomTopicMap.get("MiddleName") + " " + bottomTopicMap.get("LastName"), false, 12, null);
                                        
                                        XWPFParagraph gradeLevelPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun gradeLevelRun = makeNewParagraphRun(gradeLevelPara, "\tGrade Level: " + bottomTopicMap.get("GradeLevel"), false, 12, null);
                                        
                                        XWPFParagraph majorPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 500);
                                        XWPFRun majorRun = makeNewParagraphRun(majorPara, "\tMajor: " + bottomTopicMap.get("Major"), false, 12, null);
                                        break;
                                        
                                    case "CourseInformation":
                                        System.out.println("\n\nInside CourseInformation\n\n");
                                        
                                        // Create Char for the courseGrade
                                        char officialCourseGrade = 'F';
                                        String courseGradeString = bottomTopicMap.get("FinalCourseGrade");
                                        float courseDigit = Float.parseFloat(courseGradeString) * 100;
                                        System.out.println("The FINAL course grade is: " + courseDigit);
                                        
                                        // Reassign officialCourseGrade
                                        officialCourseGrade = (courseDigit > 89.99) ? 'A' : officialCourseGrade;
                                        officialCourseGrade = (courseDigit > 79.99 && courseDigit < 90.00) ? 'B' : officialCourseGrade;
                                        officialCourseGrade = (courseDigit > 69.99 && courseDigit < 80.00) ? 'C' : officialCourseGrade;
                                        officialCourseGrade = (courseDigit > 59.99 && courseDigit < 70.00) ? 'D' : officialCourseGrade;
                                        officialCourseGrade = (courseDigit < 59.99) ? 'F' : officialCourseGrade;
                                        
                                        // If there's more than 1 courseInformation map, we only need to print the course section title once
                                        if(courseInfoMapCount < 1){
                                            XWPFParagraph courseSectionTitle = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 100);
                                            XWPFRun courseSectionRun = makeNewParagraphRun(courseSectionTitle, "Registered Courses:", true, 13, UnderlinePatterns.SINGLE);
                                        }
                                        
                                        XWPFParagraph courseIDPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun courseIDRun = makeNewParagraphRun(courseIDPara, "Course ID: " + bottomTopicMap.get("CourseID"), false, 12, null);
                                        
                                        XWPFParagraph courseSubjectPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun courseSubjectRun = makeNewParagraphRun(courseSubjectPara, "\tCourse Name: " + bottomTopicMap.get("CourseSubject") + " " 
                                               + bottomTopicMap.get("GradeLevel") + "-" + bottomTopicMap.get("CourseNumber"), false, 12, null);
                                        
                                        XWPFParagraph courseSemesterPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun courseSemesterRun = makeNewParagraphRun(courseSemesterPara, "\tSemester Taken: " + bottomTopicMap.get("Semester"), false, 12, null);
                                        
                                        XWPFParagraph courseGradePara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun courseGradeRun = makeNewParagraphRun(courseGradePara, "\tFinal Course Grade: " + officialCourseGrade + " (" + courseDigit + "%)", true, 12, null);
                                        
                                        courseInfoMapCount++;
                                        break;
                                }
                                // Iterate through each entry
                                for(Map.Entry<String, String> bottomTopicMapEntry : bottomTopicMap.entrySet()){
                                    // Print everything out
                                    System.out.println("\t\t"+bottomTopicMapEntry.getKey() + ": " + bottomTopicMapEntry.getValue());
                                }
                            }
                        }
                    }
                }
                // Save document and write it out
                localFile = new File("C:\\Users\\Public\\Documents\\Reports\\" + studentFullName + ".docx");
                FileOutputStream out = new FileOutputStream(localFile);
                //paragraphs = document.getParagraphs();
                document.write(out);
                out.close();

                System.out.println(localFile.toString() + " written successfully!");
            }
            // Send user a confirmation message
            TA_AlertPage.alert("Report Success", "Your reports have been generated at:\nC:\\Users\\Public\\Documents\\Reports\\", Color.CORAL);
        }catch (Exception e){
            System.out.println(e);
            // Throw new exception to tell the "SaveDocPage" to let the user know they entered an
            // invalid file name or path
            throw new Exception(e);
        }
    
    }
    
    
    // Method that will be used to create a more detailed report (DEVELOPMENT ONGOING)
    public static void makeStudentReport_Custom(ArrayList<TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>>> masterStudentList)throws Exception{
        // Capture the current date
        LocalDate currentDate = LocalDate.now();

        // Configure logging
        BasicConfigurator.configure();

        // Begin constructing document
        try{            
            // Iterate through top level
            for(TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>> studentMap_TOP : masterStudentList){
                // Update Student Name (used for file naming)
                String studentFullName = "";
                // Create new document (one for each student)
                document = new XWPFDocument();
                
                // Iterate through TOP map entries
                for(Map.Entry<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>> studentIDMap : studentMap_TOP.entrySet()){
                    // Print out studentID
                    System.out.println("Student ID: " + studentIDMap.getKey());
                    // Create a paragraph for the title section
                    XWPFParagraph titleParagraph = makeNewParagraph(document,ParagraphAlignment.CENTER, true, 1000);
                    XWPFRun titleRun = makeNewParagraphRun(titleParagraph, "Report Card", true, 20, UnderlinePatterns.SINGLE);
                    
                    XWPFParagraph studentSectionTitlePara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 100);
                    XWPFRun studentSectionTitleRun = makeNewParagraphRun(studentSectionTitlePara, "Student Information:", true, 13, UnderlinePatterns.SINGLE);
                    
                    XWPFParagraph studentIDParagraph = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                    XWPFRun studentIDRun = makeNewParagraphRun(studentIDParagraph, "Student ID: " + studentIDMap.getKey(), false, 12, null);
                    
                    // Iterate through studentIDMap entries
                    for(TreeMap<String, ArrayList<TreeMap<String, String>>> studentMapList : studentIDMap.getValue()){
                        // Iterate through Map entries
                        for(Map.Entry<String, ArrayList<TreeMap<String, String>>> studentTopicMap : studentMapList.entrySet()){
                            // Count variable for formatting the WordDoc
                            int courseInfoMapCount = 0;
                            // Print Map name
                            System.out.println("\tMap Name: " + studentTopicMap.getKey());
                            // Iterate through each studentTopicMap
                            for(TreeMap<String, String> bottomTopicMap : studentTopicMap.getValue()){
                                // Enter Switch
                                switch(studentTopicMap.getKey()){
                                    case "StudentInformation":
                                        studentFullName = bottomTopicMap.get("LastName") + "_" + bottomTopicMap.get("FirstName") + studentIDMap.getKey(); 
                                        XWPFParagraph namePara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun nameRun = makeNewParagraphRun(namePara, "\tFull Name: " + bottomTopicMap.get("FirstName") + " " +
                                                bottomTopicMap.get("MiddleName") + " " + bottomTopicMap.get("LastName"), false, 12, null);
                                        
                                        XWPFParagraph gradeLevelPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun gradeLevelRun = makeNewParagraphRun(gradeLevelPara, "\tGrade Level: " + bottomTopicMap.get("GradeLevel"), false, 12, null);
                                        
                                        XWPFParagraph majorPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 500);
                                        XWPFRun majorRun = makeNewParagraphRun(majorPara, "\tMajor: " + bottomTopicMap.get("Major"), false, 12, null);
                                        break;
                                        
                                    case "CourseInformation":
                                        System.out.println("\n\nInside CourseInformation\n\n");
                                        
                                        // Create Char for the courseGrade
                                        char officialCourseGrade = 'F';
                                        String courseGradeString = bottomTopicMap.get("FinalCourseGrade");
                                        float courseDigit = Float.parseFloat(courseGradeString) * 100;
                                        System.out.println("The FINAL course grade is: " + courseDigit);
                                        
                                        // Reassign officialCourseGrade
                                        officialCourseGrade = (courseDigit > 89.99) ? 'A' : officialCourseGrade;
                                        officialCourseGrade = (courseDigit > 79.99 && courseDigit < 90.00) ? 'B' : officialCourseGrade;
                                        officialCourseGrade = (courseDigit > 69.99 && courseDigit < 80.00) ? 'C' : officialCourseGrade;
                                        officialCourseGrade = (courseDigit > 59.99 && courseDigit < 70.00) ? 'D' : officialCourseGrade;
                                        officialCourseGrade = (courseDigit < 59.99) ? 'F' : officialCourseGrade;
                                        
                                        // If there's more than 1 courseInformation map, we only need to print the course section title once
                                        if(courseInfoMapCount < 1){
                                            XWPFParagraph courseSectionTitle = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 100);
                                            XWPFRun courseSectionRun = makeNewParagraphRun(courseSectionTitle, "Registered Courses:", true, 13, UnderlinePatterns.SINGLE);
                                        }
                                        
                                        XWPFParagraph courseIDPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun courseIDRun = makeNewParagraphRun(courseIDPara, "Course ID: " + bottomTopicMap.get("CourseID"), false, 12, null);
                                        
                                        XWPFParagraph courseSubjectPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun courseSubjectRun = makeNewParagraphRun(courseSubjectPara, "\tCourse Name: " + bottomTopicMap.get("CourseSubject") + " " 
                                               + bottomTopicMap.get("GradeLevel") + "-" + bottomTopicMap.get("CourseNumber"), false, 12, null);
                                        
                                        XWPFParagraph courseSemesterPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun courseSemesterRun = makeNewParagraphRun(courseSemesterPara, "\tSemester Taken: " + bottomTopicMap.get("Semester"), false, 12, null);
                                        
                                        XWPFParagraph courseGradePara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun courseGradeRun = makeNewParagraphRun(courseGradePara, "\tFinal Course Grade: " + officialCourseGrade + " (" + courseDigit + "%)", true, 12, null);
                                        
                                        courseInfoMapCount++;
                                        break;
                                }
                                // Iterate through each entry
                                for(Map.Entry<String, String> bottomTopicMapEntry : bottomTopicMap.entrySet()){
                                    // Print everything out
                                    System.out.println("\t\t"+bottomTopicMapEntry.getKey() + ": " + bottomTopicMapEntry.getValue());
                                }
                            }
                        }
                    }
                }
                // Save document and write it out
                localFile = new File("C:\\Users\\Public\\Documents\\Reports\\" + studentFullName + ".docx");
                FileOutputStream out = new FileOutputStream(localFile);
                document.write(out);
                out.close();

                System.out.println(localFile.toString() + " written successfully!");
            }
            // Send user a confirmation message
            TA_AlertPage.alert("Report Success", "Your reports have been generated at:\nC:\\Users\\Public\\Documents\\Reports\\", Color.CORAL);
        }catch (Exception e){
            System.out.println(e);
            // Throw new exception to tell the "SaveDocPage" to let the user know they entered an
            // invalid file name or path
            throw new Exception(e);
        }
    
    }
    
    // Method to create and return paragraph
    private static XWPFParagraph makeNewParagraph(XWPFDocument doc, ParagraphAlignment align, boolean pageBreak, int spacingAfterParagraph){
        // Create new Paragraph
        XWPFParagraph tempParagraph = doc.createParagraph();
        tempParagraph.setAlignment(align);
        tempParagraph.setPageBreak(pageBreak);
        tempParagraph.setSpacingAfter(spacingAfterParagraph);
        
        // Return
        return tempParagraph;
    }
    
    // Method to create and return a paragraph run
    private static XWPFRun makeNewParagraphRun(XWPFParagraph paragraph, String text, boolean boldText, int fontSize, UnderlinePatterns option){
        // Create new Paragraph
        XWPFRun tempRun = paragraph.createRun();
        tempRun.setText(text);
        tempRun.setBold(boldText);
        tempRun.setFontSize(fontSize);
        if(option == null){
            // Do nothing
        }
        else{
            tempRun.setUnderline(option);
        }
        
        // Return
        return tempRun;
    }

}
