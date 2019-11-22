/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teachersassistant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
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

/**
 *
 * @author SoftwareEng
 */
public class TA_WordFunctions {
    
    
    private static String lineBreak;

    private static XWPFDocument document;
    private static FileReader fileReader;
    private static FileWriter fileWriter;
    private static BufferedWriter bufferedWriter;
    private static PrintWriter printWriter;

    private static File localFile;
    private static XWPFParagraph[] paragraphs;


    public static void makeStudentReport(ArrayList<TreeMap<Integer, ArrayList<TreeMap<String, ArrayList<TreeMap<String, String>>>>>> masterStudentList)throws Exception{
        // Capture the current date
        LocalDate currentDate = LocalDate.now();


        // Configure logging
        BasicConfigurator.configure();
        
        lineBreak = "________________________________________________________________________________________";

        try{
            // Create Document
            //localFile = new File(filePath + "\\" + fileName+ ".docx");
            //Write the Document in file system
            //FileOutputStream out = new FileOutputStream(localFile);
            
            
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
                    // Create a paragraph for the diving section/title
                    XWPFParagraph titleParagraph = makeNewParagraph(document,ParagraphAlignment.CENTER, true, 100);
                    XWPFRun titleRun = makeNewParagraphRun(titleParagraph, "Report Card", true, UnderlinePatterns.SINGLE);
                    
                    XWPFParagraph studentIDParagraph = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                    XWPFRun studentIDRun = makeNewParagraphRun(studentIDParagraph, "Student ID: " + studentIDMap.getKey(), false, null);
                    
                    // Iterate through studentIDMap entries
                    for(TreeMap<String, ArrayList<TreeMap<String, String>>> studentMapList : studentIDMap.getValue()){
                        // Iterate through Map entries
                        for(Map.Entry<String, ArrayList<TreeMap<String, String>>> studentTopicMap : studentMapList.entrySet()){
                            // Print Map name
                            System.out.println("\tMap Name: " + studentTopicMap.getKey());
                            // Iterate through each studentTopicMap
                            for(TreeMap<String, String> bottomTopicMap : studentTopicMap.getValue()){
                                // Enter Switch
                                switch(studentTopicMap.getKey()){
                                    case "StudentInformation":
                                        studentFullName = bottomTopicMap.get("LastName") + "_" + bottomTopicMap.get("FirstName") + studentIDMap.getKey(); 
                                        XWPFParagraph namePara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun nameRun = makeNewParagraphRun(namePara, "Full Name: " + bottomTopicMap.get("FirstName") + " " +
                                                bottomTopicMap.get("MiddleName") + " " + bottomTopicMap.get("LastName"), false, null);
                                        
                                        XWPFParagraph gradeLevelPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun gradeLevelRun = makeNewParagraphRun(gradeLevelPara, "Grade Level: " + bottomTopicMap.get("GradeLevel"), false, null);
                                        
                                        XWPFParagraph majorPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun majorRun = makeNewParagraphRun(majorPara, "Major: " + bottomTopicMap.get("Major"), false, null);
                                        break;
                                        
                                    case "CourseInformation":
                                        System.out.println("\n\nInside CourseInformation\n\n");
                                        
                                        XWPFParagraph courseSectionTitle = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 100);
                                        XWPFRun courseSectionRun = makeNewParagraphRun(courseSectionTitle, "Registered Courses", true, UnderlinePatterns.SINGLE);
                                        
                                        System.out.println("CourseID: " + bottomTopicMap.get("CourseID"));
                                        XWPFParagraph courseIDPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun courseIDRun = makeNewParagraphRun(courseIDPara, "Course ID: " + bottomTopicMap.get("CourseID"), false, null);
                                        
                                        XWPFParagraph courseSubjectPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun courseSubjectRun = makeNewParagraphRun(courseSubjectPara, "Course Subject: " + bottomTopicMap.get("CourseSubject"), false, null);
                                        
                                        XWPFParagraph courseSemesterPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun courseSemesterRun = makeNewParagraphRun(courseSemesterPara, "Semester: " + bottomTopicMap.get("CourseSemester") +
                                                " , Course Number: " + bottomTopicMap.get("CourseNumber"), false, null);
                                        
                                        XWPFParagraph courseGradePara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                        XWPFRun courseGradeRun = makeNewParagraphRun(courseGradePara, "Course Grade: " + bottomTopicMap.get("FinalCourseGrade"), true, null);
                                        System.out.println("I passed!");
                                        break;
                                }
                                // Iterate through each entry
                                for(Map.Entry<String, String> bottomTopicMapEntry : bottomTopicMap.entrySet()){
                                    // Print everything out
                                    System.out.println("\t\t"+bottomTopicMapEntry.getKey() + ": " + bottomTopicMapEntry.getValue());
                                    //XWPFParagraph tempPara = makeNewParagraph(document,ParagraphAlignment.LEFT, false, 10);
                                    //XWPFRun tempRun = makeNewParagraphRun(tempPara, bottomTopicMapEntry.getKey() + ": " + bottomTopicMapEntry.getValue(), false, null);
                                }
                            }
                        }
                    }
                }
                // Save document and write it out
                localFile = new File("C:\\Users\\SoftwareEng\\Documents\\Reports\\" + studentFullName + ".docx");
                FileOutputStream out = new FileOutputStream(localFile);
                //paragraphs = document.getParagraphs();
                document.write(out);
                out.close();

                System.out.println(localFile.toString() + " written successfully!");
            }
            // Send user a confirmation message
            TA_AlertPage.alert("Report Success", "Your reports have been generated at:\nC:\\Users\\SoftwareEng\\Documents\\Reports\\", Color.CORAL);
        }catch (Exception e){
            System.out.println(e);
            // Throw new exception to tell the "SaveDocPage" to let the user know they entered an
            // invalid file name or path
            throw new Exception(e);
        }
    
    }
    
    
    private static XWPFParagraph makeNewParagraph(XWPFDocument doc, ParagraphAlignment align, boolean pageBreak, int spacingAfterParagraph){
        // Create new Paragraph
        XWPFParagraph tempParagraph = doc.createParagraph();
        tempParagraph.setAlignment(align);
        tempParagraph.setPageBreak(pageBreak);
        tempParagraph.setSpacingAfter(spacingAfterParagraph);
        
        // Return
        return tempParagraph;
    }
    
    
    private static XWPFRun makeNewParagraphRun(XWPFParagraph paragraph, String text, boolean boldText, UnderlinePatterns option){
        // Create new Paragraph
        XWPFRun tempRun = paragraph.createRun();
        tempRun.setText(text);
        tempRun.setBold(boldText);
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
