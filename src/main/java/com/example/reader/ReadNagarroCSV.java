package com.example.reader;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

@Service
public class ReadNagarroCSV {
    //private static BiMap<String, String> myBiMap = HashBiMap.create();


    public Map<String,List<String>>  getNagarroData(FileInputStream fis) {

        List<String> dataList = new ArrayList<>();
        try {
            //File file = new File("D:\\workspace\\attendance-management\\SAP_dump.xlsx");   //creating a new file instance
            //FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file
            //creating Workbook instance that refers to .xlsx file
            Workbook wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object
            int empIdRowIndex = 0;
            //iterating over excel file

            short maxColIx = 0;
            outerloop:
            for (Row row : sheet) {
                short minColIx = row.getFirstCellNum(); //get the first column index for a row
                maxColIx = row.getLastCellNum(); //get the last column index for a row
                for (short colIx = minColIx; colIx < maxColIx; colIx++) { //loop from first to last index
                    Cell cell = row.getCell(colIx); //get the cell
                    if (cell.getStringCellValue().contains("Employee ID")) {
                        empIdRowIndex = cell.getRowIndex();
                        break outerloop;
                    }
                }

            }
            Map<String,List<String>> columnWiseData = new HashMap<>();
            Iterator<Row> rowIterator = sheet.iterator();
            String cellData = null;
            Row row = null;
            rowIterator.next();
            rowIterator.next();
            List<String> values = new ArrayList<>();
            String header = null;
            while (rowIterator.hasNext()) {

                row = rowIterator.next();
                Cell myCell = null;

                for (int i = 1; i < maxColIx; i++) {


                    myCell = row.getCell(i);
                    if(myCell.getCellType() == CellType.NUMERIC)
                    {
                        cellData = (String.valueOf(myCell.getNumericCellValue()));
                        if(cellData!=null && cellData.endsWith(".0"))
                            cellData = cellData.replaceAll("\\.0","");
                    }

                    if(myCell.getCellType() == CellType.STRING && myCell.getRichStringCellValue() != null)
                        cellData =  (myCell.getRichStringCellValue().toString());


                    if(empIdRowIndex != myCell.getRowIndex())
                    {

                           List<String> existingValues =  columnWiseData.get(dataList.get(i-1));
                            if(empIdRowIndex+1 == myCell.getRowIndex())
                            {
                                columnWiseData.put(dataList.get(i-1),null);
                            }

                            if(existingValues!=null)
                            {
                                existingValues.add(cellData);
                                columnWiseData.put(dataList.get(i-1), existingValues);
                            }
                            if(columnWiseData.get(dataList.get(i-1))==null)
                            {
                                List<String> temp = new ArrayList<>();
                                temp.add(cellData);
                                        columnWiseData.put(dataList.get(i-1), temp);
                            }
                    }
                    dataList.add(cellData);
                }
            }


           // if (!dataList.isEmpty()) {

               // List<Object> datesList = (List<Object>) dataList.get(0);
                //dataList.remove(datesList);

               // List<String> datesStrList = datesList.stream()
                   //     .map(object -> Objects.toString(object, null))
                    //    .collect(Collectors.toList());

                //AtomicInteger c = new AtomicInteger(2);
             //   datesStrList.forEach(date -> {
                 /*   Map<String, Double> dayMap = dataList.stream()
                            .collect(Collectors.toMap(p -> p.get(0).toString(), p -> (Double) p.get(c.get())));*/
                    //c.getAndIncrement();
                    //nagarroDataMap.put(date.split("\\.")[0], dayMap);
              //  });
           // }
            Map<String, List<String>> nagarroDataMap = new HashMap<>();
            for(String key:columnWiseData.keySet())
            {
                if(key.contains("."))
                {
                    nagarroDataMap.put(key.split("\\.")[0],columnWiseData.get(key));
                }
                else
                {
                    nagarroDataMap.put(key,columnWiseData.get(key));
                }
            }
            return nagarroDataMap;


        } catch (FileNotFoundException e) {
          System.out.println("Nagarro dump file is missing");
            return null;
        }
        catch (Exception e) {
            System.out.println("Exception occurred while reading SAP data"+e.getMessage());
            return null;
        }

    }

   /* public static BiMap<String, String> getMyBiMap() {
        return myBiMap;
    }*/

}



