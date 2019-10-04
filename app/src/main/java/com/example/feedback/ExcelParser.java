package com.example.feedback;

import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class ExcelParser {

    public ArrayList<Criteria> readXlsCriteria(String path) {
        ArrayList<Criteria> customisedCriteriaList = new ArrayList<>();
        try {
            // Creating Input Stream
            InputStream myInput;

            File file = new File(path);

            //  Don't forget to Change to your assets folder excel sheet
            myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells. **/
            Iterator<Row> rowIter = mySheet.rowIterator();

            ArrayList<CustomisedCriteria> criteriaList = new ArrayList<>();

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();

                CustomisedCriteria newCriteria = new CustomisedCriteria();

                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    if (myCell.getColumnIndex() == 0) {
                        newCriteria.setCriteria(myCell.toString());
                    }

                    if (myCell.getColumnIndex() == 1) {
                        newCriteria.setSubSection(myCell.toString());
                    }

                    if (myCell.getColumnIndex() == 2) {
                        newCriteria.setShortText(myCell.toString());
                    }

                    if (myCell.getColumnIndex() == 3) {
                        newCriteria.setLongText(myCell.toString());
                    }
                }
                Log.d("EEEE", "Cell Value: " + newCriteria.toString() + " Index :" + myRow.getRowNum());
                criteriaList.add(newCriteria);
            }
            Log.d("EEEE", criteriaList.toString());
            customisedCriteriaList = generateCriteriaList(criteriaList);
            Log.d("EEEE", customisedCriteriaList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customisedCriteriaList;
    }

    public ArrayList<Criteria> generateCriteriaList(ArrayList<CustomisedCriteria> criteriaList) {
        Log.d("EEEE", "generate criteria list.");
        ArrayList<Criteria> customisedCriteriaList = new ArrayList<>();
        for(int i = 0; i < criteriaList.size(); i++) {
            Log.d("EEEE", "criteria size: " + criteriaList.size());
            Log.d("EEEE", "criteria index: " + i);
            String newCriteriaName = criteriaList.get(i).getCriteria().trim();
            String newSubsectionName = criteriaList.get(i).getSubSection().trim();
            String newShortText = criteriaList.get(i).getShortText().trim();
            String newLongText = criteriaList.get(i).getLongText().trim();
            if(!(newCriteriaName.equals("") || newSubsectionName.equals("") || newShortText.equals("") || newLongText.equals(""))) {
//                Log.d("EEEE", newCriteriaName);
                int criteriaIndex = checkCriteriaName(newCriteriaName, customisedCriteriaList);
                if(criteriaIndex == -1) {
                    Criteria customisedCriteria = new Criteria();
                    customisedCriteria.setName(newCriteriaName);

                    ArrayList<SubSection> customisedSubSectionList = new ArrayList<>();
                    SubSection customisedSubsection = new SubSection();
                    customisedSubsection.setName(newSubsectionName);

                    ArrayList<ShortText> customisedShortTextList = new ArrayList<>();
                    ShortText customisedShortText = new ShortText();
                    customisedShortText.setName(newShortText);

                    ArrayList<String> customisedLongTextList = new ArrayList<>();
                    customisedLongTextList.add(newLongText);

                    customisedShortText.setLongtext(customisedLongTextList);
                    customisedShortTextList.add(customisedShortText);

                    customisedSubsection.setShortTextList(customisedShortTextList);
                    customisedSubSectionList.add(customisedSubsection);

                    customisedCriteria.setSubsectionList(customisedSubSectionList);
                    customisedCriteriaList.add(customisedCriteria);
                } else {
//                    Log.d("EEEE", newSubsectionName);
                    ArrayList<SubSection> customisedSubSectionList = customisedCriteriaList.get(criteriaIndex).getSubsectionList();
                    int subsectionIndex = checkSubSectionName(newSubsectionName, customisedSubSectionList);
                    if (subsectionIndex == -1) {
                        SubSection customisedSubsection = new SubSection();
                        customisedSubsection.setName(newSubsectionName);

                        ArrayList<ShortText> customisedShortTextList = new ArrayList<>();
                        ShortText customisedShortText = new ShortText();
                        customisedShortText.setName(newShortText);

                        ArrayList<String> customisedLongTextList = new ArrayList<>();
                        customisedLongTextList.add(newLongText);

                        customisedShortText.setLongtext(customisedLongTextList);
                        customisedShortTextList.add(customisedShortText);

                        customisedSubsection.setShortTextList(customisedShortTextList);
                        customisedSubSectionList.add(customisedSubsection);
                    } else {
//                        Log.d("EEEE", newShortText);
                        ArrayList<ShortText> customisedShortTextList = customisedSubSectionList.get(subsectionIndex).getShortTextList();
                        int shortTextIndex = checkShortText(newShortText, customisedShortTextList);
                        if(shortTextIndex == -1) {
                            ShortText customisedShortText = new ShortText();
                            customisedShortText.setName(newShortText);

                            ArrayList<String> customisedLongTextList = new ArrayList<>();
                            customisedLongTextList.add(newLongText);

                            customisedShortText.setLongtext(customisedLongTextList);
                            customisedShortTextList.add(customisedShortText);
                        } else {
//                            Log.d("EEEE", newLongText);
                            ArrayList<String> customisedLongTextList = customisedShortTextList.get(shortTextIndex).getLongtext();
                            int longTextIndex = checkLongText(newLongText, customisedLongTextList);
                            if(longTextIndex == -1) {
                                customisedLongTextList.add(newLongText);
                            }
                        }
                    }
                }
            }
        }
        display(customisedCriteriaList);
        return customisedCriteriaList;
    }

    public void display(ArrayList<Criteria> customisedCriteriaList) {
        for(int i = 0; i < customisedCriteriaList.size(); i++) {
            Log.d("EEEE", "criteria: " + customisedCriteriaList.get(i).getName());
            for(int a = 0; a < customisedCriteriaList.get(i).getSubsectionList().size(); a++) {
                Log.d("EEEE", "subsection: " + customisedCriteriaList.get(i).getSubsectionList().get(a).getName());
                for(int b = 0; b < customisedCriteriaList.get(i).getSubsectionList().get(a).getShortTextList().size(); b++) {
                    Log.d("EEEE", "shorttext: " + customisedCriteriaList.get(i).getSubsectionList().get(a).getShortTextList().get(b).getName());
                    for(int c = 0; c < customisedCriteriaList.get(i).getSubsectionList().get(a).getShortTextList().get(b).getLongtext().size(); c++) {
                        Log.d("EEEE", "longtext: " + customisedCriteriaList.get(i).getSubsectionList().get(a).getShortTextList().get(b).getLongtext().get(c));
                    }
                }
            }
        }
    }

    public int checkCriteriaName(String newCriteriaName, ArrayList<Criteria> customisedCriteriaList) {
        for(int i = 0; i < customisedCriteriaList.size(); i++) {
//            Log.d("EEEE", "size:  " + customisedCriteriaList.size());
            String oldCriteriaName = customisedCriteriaList.get(i).getName();
//            Log.d("EEEE", "old  " + oldCriteriaName);
//            Log.d("EEEE", "new  " + newCriteriaName);
            if(oldCriteriaName.equals(newCriteriaName)) {
                return i;
            }
        }
        return -1;
    }

    public int checkSubSectionName(String newSubsectionName, ArrayList<SubSection> customisedSubSectionList) {
        for(int i = 0; i < customisedSubSectionList.size(); i++) {
            String oldSubsectionName = customisedSubSectionList.get(i).getName();
            if(oldSubsectionName.equals(newSubsectionName)) {
                return i;
            }
        }
        return -1;
    }

    public int checkShortText(String newShortText, ArrayList<ShortText> customisedShortTextList) {
        for(int i = 0; i < customisedShortTextList.size(); i++) {
            String oldShortText = customisedShortTextList.get(i).getName();
            if(oldShortText.equals(newShortText)) {
                return i;
            }
        }
        return -1;
    }

    public int checkLongText(String newLongText, ArrayList<String> customisedLongTextList) {
        for(int i = 0; i < customisedLongTextList.size(); i++) {
            String oldLongText = customisedLongTextList.get(i);
            if(oldLongText.equals(newLongText)) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Criteria> readXlsxCriteria(String path) {
        ArrayList<Criteria> customisedCriteriaList = new ArrayList<>();
        try {
            // Creating Input Stream
            InputStream myInput;

            File file = new File(path);

            //  Don't forget to Change to your assets folder excel sheet
            myInput = new FileInputStream(file);

            XSSFWorkbook workbook = new XSSFWorkbook(myInput);
            XSSFSheet mySheet = workbook.getSheetAt(0);


            /** We now need something to iterate through the cells. **/
            Iterator<Row> rowIter = mySheet.rowIterator();

            ArrayList<CustomisedCriteria> criteriaList = new ArrayList<>();

            while (rowIter.hasNext()) {
                Row myRow = (Row) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();

                CustomisedCriteria newCriteria = new CustomisedCriteria();

                while (cellIter.hasNext()) {
                    XSSFCell myCell = (XSSFCell) cellIter.next();
                    if (myCell.getColumnIndex() == 0) {
                        newCriteria.setCriteria(myCell.toString());
                    }

                    if (myCell.getColumnIndex() == 1) {
                        newCriteria.setSubSection(myCell.toString());
                    }

                    if (myCell.getColumnIndex() == 2) {
                        newCriteria.setShortText(myCell.toString());
                    }

                    if (myCell.getColumnIndex() == 3) {
                        newCriteria.setLongText(myCell.toString());
                    }

                    Log.d("EEEE", "Cell Value: " + newCriteria.toString() + " Index :" + myCell.getColumnIndex());

                }
                criteriaList.add(newCriteria);
            }
            Log.d("EEEE", criteriaList.toString());
            customisedCriteriaList = generateCriteriaList(criteriaList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customisedCriteriaList;
    }
}
