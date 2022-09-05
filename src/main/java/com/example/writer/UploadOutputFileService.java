package com.example.writer;

import com.azure.storage.blob.BlobClient;
import com.example.config.AzureFileUploadConfig;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;

@Service
public class UploadOutputFileService {

    private final AzureFileUploadConfig azureFileUploadConfig;

    public UploadOutputFileService(AzureFileUploadConfig azureFileUploadConfig) {
        this.azureFileUploadConfig = azureFileUploadConfig;

    }

    public String uploadFileToAzure(String dirName, String fileName, InputStream azureFileInputStream,
                                    long fileSize) {
        BlobClient blobClient = null;
        blobClient = azureFileUploadConfig.blobContainerClient().getBlobClient(dirName + "/" + fileName);

        if (blobClient.exists())
        {
            blobClient.delete();
            blobClient = azureFileUploadConfig.blobContainerClient().getBlobClient(dirName + "/" + fileName);
        }
          //  blobClient = azureFileUploadConfig.blobContainerClient().getBlobClient(dirName + "/"+ Instant.now().toEpochMilli() + fileName );
//upload file
            blobClient.upload(azureFileInputStream, fileSize);
            return blobClient.getBlobUrl();

    }


}
