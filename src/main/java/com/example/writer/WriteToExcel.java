package com.example.writer;

import com.google.common.collect.BiMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
@Service
public class WriteToExcel {
@Autowired
private UploadOutputFileService uploadOutputFileService;

    /**
     * @author Gunjan
     * @param nagarroMap
     * @param proWANDMap
     * @param inverse
     */
    public String writeEmployeeData(Map<String, List<String>> nagarroMap, Map<String, List<String>> proWANDMap, BiMap<String, String> inverse) throws IOException {
        {


            Map<String, List<String>> nagarroMapCopy = new HashMap<>();

            for (Map.Entry<String, List<String>> entry: nagarroMap.entrySet()) {
                nagarroMapCopy.put(entry.getKey(), entry.getValue());
            }
            Map<String, List<String>> proWANDMapCopy = new HashMap<>();

            for (Map.Entry<String, List<String>> entry: proWANDMap.entrySet()) {
                proWANDMapCopy.put(entry.getKey(), entry.getValue());
            }

            //Blank workbook
            XSSFWorkbook workbook = new XSSFWorkbook();

            //Create a blank sheet
            XSSFSheet sheet = workbook.createSheet("Employee Data");

            //This data needs to be written (Object[])
            Map<String, List<String>> data = new TreeMap<>();
            List<String> headersList=new ArrayList<>();
            headersList.add("ID");
            headersList.add("Emp Name");
            headersList.add("Additional Days in SAP");
            headersList.add("Additional days in WAND");
            headersList.add("Total hrs in SAP");
            headersList.add("Total hrs in WAND");
            headersList.add("Difference in hrs");
            int rownum = 0;
            int columnCount = 0;
            Row row = sheet.createRow(rownum++);
            for (String header : headersList)
            {
                Cell cell = row.createCell(columnCount++);
                cell.setCellValue(header);
            }
            for(String empIds : nagarroMap.get("Employee ID"))
            {
                row = sheet.createRow(rownum++);
                Cell cell = row.createCell(0);
                cell.setCellValue(empIds);
            }
            rownum = 1;
            for(String empNames : nagarroMap.get("Full Name"))
            {

                row = sheet.getRow(rownum++);
                Cell cell = row.createCell(1);
                cell.setCellValue(empNames);

            }

            rownum=1;//additional days in sap
            int outer = 0;
            Set<String> nagarroHeaders = nagarroMap.keySet();
            nagarroHeaders.remove("Employee ID");
            nagarroHeaders.remove("Full Name");
            Set<String> proWANDHeaders = proWANDMap.keySet();
            proWANDHeaders.remove("Worker");
            for(String empIds : nagarroMapCopy.get("Employee ID"))
            {

                row = sheet.getRow(rownum++);
                Cell cell = row.createCell(2);
                List<String> addSAP = new ArrayList<>();



                    for (String dates : nagarroHeaders) {
                        int loc = 0;
                        if (inverse.get(empIds) == null) {
                            System.out.println(empIds + "does not exist in mapping file !!");
                            break;
                        }

                        for (String wandNames : proWANDMapCopy.get("Worker")) {

                            if (wandNames != null && wandNames.contains(inverse.get(empIds))) {
                                if (Double.parseDouble(nagarroMapCopy.get(dates).get(outer)) > 0 && Double.parseDouble(proWANDMapCopy.get(dates).get(loc)) < Double.parseDouble(nagarroMapCopy.get(dates).get(outer)))
                                    addSAP.add(dates);
                            } else {
                                loc++;
                            }
                        }
                    }
                    cell.setCellValue(addSAP.toString());

                outer++;

            }


            //additional days in wand

            rownum=1;
            int wandOuter = 0;
            for(String empIds : nagarroMapCopy.get("Employee ID"))
            {

                row = sheet.getRow(rownum++);
                Cell cell = row.createCell(3);
                List<String> addSAP = new ArrayList<>();
                for(String dates : nagarroHeaders)
                {
                    int loc = 0;
                    if (inverse.get(empIds)==null)
                    {
                        System.out.println(empIds +"does not exist in mapping file !!");
                        break;
                    }

                    for (String wandNames : proWANDMapCopy.get("Worker")) {

                        if (wandNames!=null && wandNames.contains(inverse.get(empIds))) {
                            if (Double.parseDouble(proWANDMapCopy.get(dates).get(loc)) > 0 && Double.parseDouble(proWANDMapCopy.get(dates).get(loc)) > Double.parseDouble(nagarroMapCopy.get(dates).get(wandOuter)))
                                addSAP.add(dates);
                        } else
                        {
                            loc++;
                        }
                    }
                }
                wandOuter++;
                cell.setCellValue(addSAP.toString());
            }

            rownum=1;
            int empCount = 0;
            for(String empIds : nagarroMapCopy.get("Employee ID"))
            {

                    row = sheet.getRow(rownum++);
                Cell cell = row.createCell(4);
                double sapTotal =0;
                Cell cellWand = row.createCell(5);
                double wandTotal =0;
                Cell diffCell = row.createCell(6);
                for(String dates : nagarroHeaders)
                {

                    if (inverse.get(empIds)==null)
                    {
                        System.out.println(empIds +"does not exist in mapping file !!");
                        break;
                    }
                    sapTotal = sapTotal+ Double.parseDouble(nagarroMapCopy.get(dates).get(empCount));

                }
                for(String dates : proWANDHeaders)
                {
                    int loc = 0;
                    if (inverse.get(empIds)==null)
                    {
                        System.out.println(empIds +"does not exist in mapping file !!");
                        break;
                    }
                    for (String wandNames : proWANDMapCopy.get("Worker")) {

                        if (wandNames != null && wandNames.contains(inverse.get(empIds))) {
                            wandTotal = wandTotal + Double.parseDouble(proWANDMapCopy.get(dates).get(loc));
                        } else {
                            loc++;
                        }
                    }

                }
                empCount++;
                cell.setCellValue(sapTotal);
                cellWand.setCellValue(wandTotal);
                diffCell.setCellValue(Math.abs(wandTotal-sapTotal));


            }


             /*/////////Second worksheet when required//////////////
             XSSFSheet exceptionSheet = workbook.createSheet("Exception Data");
            List<String> exceptionHeadersList=new ArrayList<>();
            exceptionHeadersList.add("ID");
            exceptionHeadersList.add("Emp Name");
            exceptionHeadersList.add("Missing Data in");
            exceptionHeadersList.add("Days");


            int exceptionColumnCount = 0;
            Row exceptionRow = sheet.createRow(rownum++);
           for (String header : exceptionHeadersList)
            {
                Cell cell = exceptionRow.createCell(exceptionColumnCount++);
                cell.setCellValue(header);
            }*/
            int exceptionRownum = rownum;
            for(String empId : inverse.keySet())
            {
              if(!nagarroMapCopy.get("Employee ID").contains(empId))
                {

                    int loc =0 ;
                    for(String str: proWANDMapCopy.get("Worker"))
                    {
                        if(str.equalsIgnoreCase(inverse.get(empId)))
                        {
                            break;
                        }
                        else
                            loc++;
                    }
                    if(loc<proWANDMapCopy.get("Worker").size()) {

                        List<String> days = new ArrayList<>();
                        for (String dates : proWANDMap.keySet()) {
                            if (Double.parseDouble(proWANDMap.get(dates).get(loc)) > 0) {
                                days.add(dates);
                            }

                        }
                        row = sheet.createRow(exceptionRownum++);
                        Cell cell = row.createCell(0);
                        cell.setCellValue(empId);
                        cell = row.createCell(1);
                        cell.setCellValue(inverse.get(empId));
                        cell = row.createCell(2);
                        cell.setCellValue("No record in SAP");
                        cell = row.createCell(3);
                        cell.setCellValue(days.toString());
                        cell = row.createCell(4);
                        cell.setCellValue(String.valueOf(0));
                        cell= row.createCell(5);
                        cell.setCellValue(days.size()*8);
                        cell = row.createCell(6);
                        cell.setCellValue(days.size()*8);
                    }
                }
                if(!proWANDMapCopy.get("Worker").contains(inverse.get(empId)))
                {

                    int loc=0;
                    for(String empIdsInSAP: nagarroMapCopy.get("Employee ID"))
                    {
                        if(empIdsInSAP.equalsIgnoreCase(empId))
                        {
                            break;
                        }
                        else
                            loc++;
                    }
                    if(loc<nagarroMapCopy.get("Employee ID").size()) {

                        List<String> days = new ArrayList<>();
                        for (String dates : nagarroMap.keySet()) {
                            if (Double.parseDouble(nagarroMap.get(dates).get(loc)) > 0) {
                                days.add(dates);//sort in asc
                            }

                        }
                        row = sheet.createRow(exceptionRownum++);
                        Cell cell = row.createCell(0);
                        cell.setCellValue(empId);
                        cell = row.createCell(1);
                        cell.setCellValue(inverse.get(empId));
                        cell = row.createCell(2);
                        cell.setCellValue(days.toString());
                        cell = row.createCell(3);
                        cell.setCellValue("No record in WAND");
                        cell = row.createCell(4);
                        cell.setCellValue(days.size()*8);
                        cell= row.createCell(5);
                        cell.setCellValue(String.valueOf(0));
                        cell = row.createCell(6);
                        cell.setCellValue(days.size()*8);
                    }
                }
            }

          //code to remove [] entries in case required
          /*  for(int i=0;i<sheet.getLastRowNum();i++){

                XSSFRow r = sheet.getRow(i);
                Cell cell=r.getCell(2);
                String sapList=cell.getStringCellValue();
                cell=r.getCell(3);
                String proWandList=cell.getStringCellValue();
                if(sapList.equalsIgnoreCase("[]") && proWandList.equalsIgnoreCase("[]")){
                    sheet.removeRow(r);
                    sheet.shiftRows(i+1,sheet.getLastRowNum(), -1);

                }}*/
            InputStream azureInputStream = null;
            try
            {
                AreaReference reference = workbook.getCreationHelper().createAreaReference(
                        new CellReference(0, 0), new CellReference( exceptionRownum-1,6));

                XSSFTable table = sheet.createTable(reference);
                CTTable cttable = table.getCTTable();

                cttable.setDisplayName("Table1");
                cttable.setId(1);
                cttable.setName("Test");
                for(int i=0;i<7;i++)
                table.getCTTable().getTableColumns().getTableColumnArray(i).setId(i+1);
                table.getCTTable().addNewAutoFilter().setRef(table.getArea().formatAsString());
                CTTableStyleInfo styleInfo = cttable.addNewTableStyleInfo();
                styleInfo.setName("TableStyleMedium2");
                styleInfo.setShowColumnStripes(true);
                styleInfo.setShowRowStripes(true);
                File file = new File("test.xlsx");
                FileOutputStream out = new FileOutputStream(file);
                workbook.write(out);
                out.close();
                azureInputStream = new FileInputStream(file);

                return uploadOutputFileService.uploadFileToAzure("mcKinsey", file.getName(), azureInputStream, file.length());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                azureInputStream.close();
            }
            return null;
        }
    }
}
