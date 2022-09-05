package com.example.controller;

import com.example.dto.MapUtility;
import com.example.reader.ReadNagarroCSV;
import com.example.reader.ReadWANDexcel;
import com.example.writer.WriteToExcel;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("mcKinsey")
public class AttendanceController {

    @Autowired
    private ReadNagarroCSV readNagarroCSV;
    @Autowired
    private WriteToExcel writeToExcel;
    @Autowired
    private ReadWANDexcel readWANDexcel;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @ApiOperation(value = "This API is used to get list of timesheet defaulters.")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(@RequestParam("files") List<MultipartFile> files) {

        MapUtility maps = new MapUtility();
            try{
            files.stream().forEach(file -> {

                        try {
                            if (file.getOriginalFilename().contains("SAP")) {
                                FileInputStream fis = (FileInputStream) file.getInputStream();
                                maps.setNagarroMap(readNagarroCSV.getNagarroData(fis));
                            }
                            if (file.getOriginalFilename().contains(".txt")) {
                                maps.setProWandEmpMap(loadDataIntoMap(file.getInputStream()));

                            }
                            if (file.getOriginalFilename().contains("WAND")) {
                                File wandfile = new File("WAND.xlsx");
                                FileOutputStream outputStream = new FileOutputStream(wandfile);
                                IOUtils.copy(file.getInputStream(), outputStream);

                                maps.setBloburl(writeToExcel.writeEmployeeData(maps.getNagarroMap(), readWANDexcel.getMcKinseyTimesheetData(wandfile), maps.getProWandEmpMap().inverse()));

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
/*
            try
            {
                String excelPath = Paths.get("test.xlsx")
                        .toAbsolutePath().normalize().toString();
                outputfile = new File(excelPath);
                Path path = Paths.get(outputfile.getAbsolutePath());

            }
            catch (Exception exception) {
                new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
           headers.add("Content-Disposition", "attachment; filename=" + outputfile.getName());
            headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

            InputStreamResource resource = new InputStreamResource(new FileInputStream(outputfile));
            MediaType mediaType = MediaType.parseMediaType("application/xml");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + outputfile.getName())
                    .contentType(mediaType)
                    .contentLength(outputfile.length()) //
                    .body(resource);*/

        } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }


        return new ResponseEntity<String>(maps.getBloburl(), HttpStatus.OK);
    }
    private static  BiMap<String, String> loadDataIntoMap(InputStream is)  throws Exception{

        try {
            BiMap<String, String> namesEmpIdMap = HashBiMap.create();
            String line;
            //BufferedReader reader = new BufferedReader(new FileReader(filePath));
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length >= 2) {
                    String key = parts[1];
                    String value = parts[0];
                    namesEmpIdMap.put(key, value);
                }
            }
            return namesEmpIdMap;
        }
        catch (FileNotFoundException e) {
            System.out.println("config file is missing");
            return null;
        }
        catch (Exception e) {
            System.out.println("Exception occurred while reading config file data"+e.getMessage());
            return null;
        }
    }
}
