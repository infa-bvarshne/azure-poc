package com.example.tokenpoc;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.sas.AccountSasPermission;
import com.azure.storage.common.sas.AccountSasResourceType;
import com.azure.storage.common.sas.AccountSasService;
import com.azure.storage.common.sas.AccountSasSignatureValues;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Date;

@RestController
public class SASController {
    private final String SASTOKEN="?sv=2021-06-08&ss=bfqt&srt=sc&sp=rwdlacupiytfx&se=2022-09-30T19:15:09Z&st=2022-09-30T11:15:09Z&spr=https&sig=C67OXNjL2gquwUh3q7h%2Barn1hDUwbHp9BX63s3E%2Bdb4%3D";
    private final String STORAGE_ACCOUNT_URL = "https://saspossa1.blob.core.windows.net";
    //private final String CONTAINER_NAME = ""
    @GetMapping("/generateSASToken")
    public String generateSASToken(){
        // Reference -https://stackoverflow.com/questions/53653473/generating-an-sas-token-in-java-to-download-a-file-in-an-azure-data-storage-cont
        SharedAccessAccountPolicy sharedAccessAccountPolicy = new SharedAccessAccountPolicy();
        sharedAccessAccountPolicy.setPermissionsFromString("racwdlup");
        long date = new Date().getTime();
        long expiryDate = new Date(date + 8640000).getTime();
        sharedAccessAccountPolicy.setSharedAccessStartTime(new Date(date));
        sharedAccessAccountPolicy.setSharedAccessExpiryTime(new Date(expiryDate));
        sharedAccessAccountPolicy.setResourceTypeFromString("sco");
        sharedAccessAccountPolicy.setServiceFromString("bfqt");
        String sasToken = "?" + storageAccount.generateSharedAccessSignature(sharedAccessAccountPolicy);
    }

    @GetMapping("/")
    public String index() {
        /*
         * Generate an account sas. Other samples in this file will demonstrate how to create a client with the sas
         * token.
         */
        // Object creation
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(STORAGE_ACCOUNT_URL)
                .sasToken(SASTOKEN)
                .buildClient();
        //BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("mycontainer");


// Configure the sas parameters. This is the minimal set.
        OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(1);
        AccountSasPermission accountSasPermission = new AccountSasPermission().setReadPermission(true);
        AccountSasService services = new AccountSasService().setBlobAccess(true);
        AccountSasResourceType resourceTypes = new AccountSasResourceType().setObject(true);

// Generate the account sas.
        AccountSasSignatureValues accountSasValues =
                new AccountSasSignatureValues(expiryTime, accountSasPermission, services, resourceTypes);
        System.out.println(accountSasValues);
        String sasToken = blobServiceClient.generateAccountSas(accountSasValues);
        return sasToken;

//// Generate a sas using a container client
//        BlobContainerSasPermission containerSasPermission = new BlobContainerSasPermission().setCreatePermission(true);
//        BlobServiceSasSignatureValues serviceSasValues =
//                new BlobServiceSasSignatureValues(expiryTime, containerSasPermission);
//        blobContainerClient.generateSas(serviceSasValues);
//
//// Generate a sas using a blob client
//        BlobSasPermission blobSasPermission = new BlobSasPermission().setReadPermission(true);
//        serviceSasValues = new BlobServiceSasSignatureValues(expiryTime, blobSasPermission);
//        blobClient.generateSas(serviceSasValues);
    }

//    @GetMapping("/getAzureToken")
//    public void getToken(){
//        String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", "accountName");
//
//// Create a BlobServiceClient object which will be used to create a container client
//        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().endpoint(endpoint)
//                .credential(new DefaultAzureCredentialBuilder().build()).buildClient();
//
//// Get a user delegation key for the Blob service that's valid for seven days.
//// You can use the key to generate any number of shared access signatures over the lifetime of the key.
//        OffsetDateTime keyStart = OffsetDateTime.now();
//        OffsetDateTime keyExpiry = OffsetDateTime.now().plusDays(7);
//        UserDelegationKey userDelegationKey = blobServiceClient.getUserDelegationKey(keyStart, keyExpiry);
//
//        BlobContainerSasPermission blobContainerSas = new BlobContainerSasPermission();
//        blobContainerSas.setReadPermission(true);
//        BlobServiceSasSignatureValues blobServiceSasSignatureValues = new BlobServiceSasSignatureValues(keyExpiry,
//                blobContainerSas);
//        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("containerName");
//        if (!blobContainerClient.exists())
//            blobContainerClient.create();
//
//        String sas = blobContainerClient
//                .generateUserDelegationSas(blobServiceSasSignatureValues, userDelegationKey);
//    }
//    }
}
