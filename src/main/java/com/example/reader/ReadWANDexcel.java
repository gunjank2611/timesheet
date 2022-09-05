package com.example.reader;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
@Service
public class ReadWANDexcel {


    public Map<String, List<String>> getMcKinseyTimesheetData(File file) {

        Map<String, List<String>> columnWiseData = new HashMap<>();

        List<String> dataList = new ArrayList<>();
        try {
          //  File file = new File("D:\\workspace\\attendance-management\\WAND_dump.xls");   //creating a new file instance
          //  FileInputStream fis = new FileInputStream(file);

            //creating Workbook instance that refers to .xlsx file
            Workbook myWorkBook = null;
            POIFSFileSystem myFileSystem = new POIFSFileSystem(file);
            myWorkBook = new HSSFWorkbook(myFileSystem);
          /*  if (file.getName().endsWith("xls")) {
                POIFSFileSystem myFileSystem = new POIFSFileSystem(file);
                myWorkBook = new HSSFWorkbook(myFileSystem);
            } else if (file.getName().endsWith("xlsx")) {
                myWorkBook = new XSSFWorkbook(file);
            }*/

            Sheet sheet = myWorkBook.getSheetAt(0);  //creating a Sheet object to retrieve object
            int empIdColIndex = 0;
            int empIdRowIndex = 0;
            //iterating over excel file

            short maxColIx = 0;
            outerloop:
            for (Row row : sheet) {
                short minColIx = row.getFirstCellNum(); //get the first column index for a row
                maxColIx = row.getLastCellNum(); //get the last column index for a row
                for (short colIx = minColIx; colIx < maxColIx; colIx++) { //loop from first to last index
                    Cell cell = row.getCell(colIx); //get the cell
                    if (cell.getStringCellValue().contains("Worker")) {
                        empIdColIndex = cell.getColumnIndex();
                        empIdRowIndex = cell.getRowIndex();
                        break outerloop;
                    }
                }

            }

            Iterator<Row> rowIterator = sheet.iterator();
            String cellData = null;
            Row row = null;
            row = rowIterator.next();
            row = rowIterator.next();
            List<String> values = new ArrayList<>();
            String header = null;
            while (rowIterator.hasNext()) {

                row = rowIterator.next();
                Cell myCell = null;
                Cell cell = row.getCell(0); //get the cell
                if (cell.getStringCellValue().contains("blank")) {
                    break;
                }
                for (int i = 0; i < maxColIx; i++) {
                    // values = new ArrayList<>();
                    cellData = new String();
                    myCell = row.getCell(i);
                    if(myCell!=null) {
                        if (myCell.getCellType() == CellType.NUMERIC)
                            cellData = (String.valueOf(myCell.getNumericCellValue()));
                        if (myCell.getCellType() == CellType.STRING)
                        {

                            if(StringUtils.isEmpty(myCell.getRichStringCellValue().toString()))
                            {
                                cellData = "0.0";
                            }
                            else
                            {
                                cellData =myCell.getRichStringCellValue().toString();
                            }
                        }
                        if( myCell.getCellType()==CellType.BLANK)
                            cellData = "0.0";

                        if (empIdRowIndex != myCell.getRowIndex()) {
                            //values.add(cellData);
                            List<String> existingValues = columnWiseData.get(dataList.get(i));
                            if (empIdRowIndex == myCell.getRowIndex()) {
                                columnWiseData.put(dataList.get(i), null);
                            } else if (existingValues != null) {
                                existingValues.add(cellData);
                                columnWiseData.put(dataList.get(i), existingValues);
                            } else if (existingValues == null) {
                                List<String> temp = new ArrayList<>();
                                temp.add(cellData);
                                columnWiseData.put(dataList.get(i), temp);
                            }
                        }
                        dataList.add(cellData);
                    }
                    else
                    {
                        List<String> existingValues = columnWiseData.get(dataList.get(i));
                        if (existingValues != null) {
                            existingValues.add("0.0");
                            columnWiseData.put(dataList.get(i), existingValues);
                        } else if (existingValues == null) {
                            List<String> temp = new ArrayList<>();
                            temp.add("0.0");
                            columnWiseData.put(dataList.get(i), temp);
                        }
                    }
                }
            }
        }
            catch (FileNotFoundException e) {
                System.out.println("WAND dump file is missing");
                return null;
            }
        catch (Exception e) {
                System.out.println("Exception occurred while reading SAP data"+e.getMessage());
                return null;
            }

        Map<String, List<String>> clientDataMap = new HashMap<>();
        for(String key:columnWiseData.keySet())
        {
            if(key.contains("-"))
            {
                clientDataMap.put(key.split("-")[0],columnWiseData.get(key));
            }
            else
            {
                clientDataMap.put(key,columnWiseData.get(key));
            }
        }
        return clientDataMap;
    }
}
