package com.example.tokenpoc;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.storage.StorageException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@RestController
public class Usage {
    //private final String SASTOKEN="?ss=bfqt&sig=Rl7a%2BE0EIcXt%2BCD9gkmLsb5YTpki2Jv1vhnw7EGUNHo%3D&st=2022-10-06T11%3A37%3A41Z&se=2022-10-06T14%3A01%3A41Z&sv=2019-02-02&srt=sco&sp=racwdlup&sr=sco%";
    private final String STORAGE_ACCOUNT_URL = "https://saspossa1.blob.core.windows.net";
    BlobClient blobClient;
    BlobContainerClient blobContainerClient;
    BlobServiceClient blobServiceClient;
    SASController controller = new SASController();

    @GetMapping("/Usage/uploadBlob")
    public void uploadBlob() throws URISyntaxException, InvalidKeyException, StorageException {
        String token = controller.getSasTokenUsingSharedAccessPolicy();
        blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(STORAGE_ACCOUNT_URL)
                .sasToken(token)
                .buildClient();
        blobContainerClient = blobServiceClient.getBlobContainerClient("container-example");
        blobClient = blobContainerClient.getBlobClient("blob2");
        blobClient = blobContainerClient.getBlobClient("blob2");
        blobClient.uploadFromFile("/Users/bvarshne/Desktop/blob2.png");
    }

}
