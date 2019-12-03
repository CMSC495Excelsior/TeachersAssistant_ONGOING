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
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
        TreeMap<Integer, TreeMap<Float, ArrayList<Integer>>> rankingsMap = TA_SQLFunctions.calculateStudentRankings();
        
        // Float formatter
        //NumberFormat floatFormatter = new DecimalFormat("##.##");

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
                    
                    // Give the studentID to the method to return the student's rank in grade level
                    int tempStudentRank = getStudentRank(studentIDMap.getKey(), rankingsMap);
                    if(tempStudentRank == -1){
                        // Prompt user
                        TA_AlertPage.alert("Student Rank Error", "No rank was found for student ID: " + studentIDMap.getKey(), Color.CORAL);
                    }
                    
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
                                        // Calculate the average GPA for the student's grade level
                                        float averageGPA = getGradeAverage(rankingsMap, Integer.parseInt(bottomTopicMap.get("GradeLevel")));
                                        
                                        studentFullName = bottomTopicMap.get("LastName") + "_" + bottomTopicMap.get("FirstName") + studentIDMap.getKey(); 
                                        XWPFParagraph namePara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun nameRun = makeNewParagraphRun(namePara, "\tFull Name: " + bottomTopicMap.get("FirstName") + " " +
                                                bottomTopicMap.get("MiddleName") + " " + bottomTopicMap.get("LastName"), false, 12, null);
                                        
                                        XWPFParagraph gradeLevelPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun gradeLevelRun = makeNewParagraphRun(gradeLevelPara, "\tGrade Level: " + bottomTopicMap.get("GradeLevel"), false, 12, null);
                                        
                                        XWPFParagraph majorPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun majorRun = makeNewParagraphRun(majorPara, "\tMajor: " + bottomTopicMap.get("Major"), false, 12, null);
                                        
                                        XWPFParagraph rankPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun rankRun = makeNewParagraphRun(rankPara, "\tRank (out of grade level): " + tempStudentRank, false, 12, null);
                                        
                                        XWPFParagraph averageGPAPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun averageGPARun = makeNewParagraphRun(averageGPAPara, "\tGrade " + bottomTopicMap.get("GradeLevel") + " Average GPA: " + String.format("%.2f", averageGPA), false, 12, null);
                                        
                                        XWPFParagraph studentGPAPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 500);
                                        XWPFRun studentGPARun = makeNewParagraphRun(studentGPAPara, "\tOverall GPA: " + bottomTopicMap.get("StudentGPA"), true, 12, null);
                                        break;
                                        
                                    case "CourseInformation":
                                        System.out.println("\n\nInside CourseInformation\n\n");
                                        
                                        // Save the course average GPA
                                        float tempAverageCourseGrade = TA_SQLFunctions.calculateAverageCourseGrade(Integer.parseInt(bottomTopicMap.get("CourseID")));
                                        tempAverageCourseGrade = tempAverageCourseGrade * 100;
                                        
                                        // Create Char for the courseGrade
                                        String officialCourseGrade = bottomTopicMap.get("FinalCourseGradeChar");
                                        String courseGradeString = bottomTopicMap.get("FinalCourseGrade");
                                        float courseDigit = Float.parseFloat(courseGradeString);
                                        System.out.println("The FINAL course grade is: " + courseDigit + "\n\tCourse Letter Grade: " + officialCourseGrade);
                                        
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
                                        
                                        XWPFParagraph averageCourseGradePara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun averageCourseGradeRun = makeNewParagraphRun(averageCourseGradePara, "\tAverage Course Grade: " + String.format("%.2f",tempAverageCourseGrade) + "%", false, 12, null);
                                        
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
    
    private static int getStudentRank(int studentID_ARG, TreeMap<Integer, TreeMap<Float, ArrayList<Integer>>> rankMap){
        // Use the rankings map to locate the supplied ID and return the rank
        for(Map.Entry<Integer, TreeMap<Float, ArrayList<Integer>>> gradeLevelMap : rankMap.entrySet()){
            // Save the tempGrade Level & counter for the rank
            int tempGradeLevel = gradeLevelMap.getKey();
            
            // Save temp rank
            int studentRank = 1;
            
            // Iterate through the sub-map
            for(Map.Entry<Float, ArrayList<Integer>> studentRankMap : gradeLevelMap.getValue().entrySet()){
                // Iterate through the List
                for(Integer entry : studentRankMap.getValue()){
                    // Try finding the student ID
                    if(entry == studentID_ARG){
                        // Return
                        System.out.println("Student ID found at GradeLevel: " + tempGradeLevel + " and GPA: " + studentRankMap.getKey() + " ...Rank is: " + studentRank);
                        return studentRank;
                    }
                }
                // Decrease the rank (higher number is worse)
                studentRank++;
            }
        }
        
        // If no match is found return -1
        return -1;
    }
    
    
    private static float getGradeAverage(TreeMap<Integer, TreeMap<Float, ArrayList<Integer>>> rankMap, int gradeLevel_ARG){
        // Default average grade for grade level
        float tempGPAAverage = 0.0f;
        int numOfGPAs = 0;
        
        // Iterate through the map
        for(Map.Entry<Integer, TreeMap<Float, ArrayList<Integer>>> gradeLevelMap : rankMap.entrySet()){
            // Save gradeLevel
            int tempGradeLevel = gradeLevelMap.getKey();
            
            // Confirm we're iterating through proper map
            if(tempGradeLevel == gradeLevel_ARG){
                // Update the size of numOfGPAs
                numOfGPAs = gradeLevelMap.getValue().size();
                // Iterate through GPAMap
                for(Map.Entry<Float, ArrayList<Integer>> gpaMap : gradeLevelMap.getValue().entrySet()){
                    // Calculate the average
                    tempGPAAverage = tempGPAAverage + gpaMap.getKey();
                }
                
                // Finalize the average
                tempGPAAverage = tempGPAAverage / Float.parseFloat(String.valueOf(numOfGPAs));
                
                // Return the average
                return tempGPAAverage;
            }
            else{
                // Do nothing
            } 
        }
        
        // Otherwise return -1
        return -1.0f;
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
