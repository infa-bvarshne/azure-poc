package com.example.tokenpoc;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.sas.AccountSasPermission;
import com.azure.storage.common.sas.AccountSasResourceType;
import com.azure.storage.common.sas.AccountSasService;
import com.azure.storage.common.sas.AccountSasSignatureValues;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.SharedAccessAccountPolicy;
import com.microsoft.azure.storage.StorageException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
//import com.azure.identity.DefaultAzureCredentialBuilder;


import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.time.OffsetDateTime;
import java.util.Date;

@RestController
public class SASController {
    private String accountName = "saspossa1";
    private String endpointSuffix = "core.windows.net";
    //private final String CONTAINER_NAME = ""

//    private String generateUserDelegationKey() {
//        /* Reference
//        https://stackoverflow.com/questions/64242397/azure-sdk-for-java-how-to-setup-user-delegation-key-and-shared-authentication-si
//
//         */
//        BlobServiceClient blobStorageClient = new BlobServiceClientBuilder()
//                .endpoint("<your-storage-account-url>")
//                .credential(new DefaultAzureCredentialBuilder().build())
//                .buildClient();
//    }

    private String generateStorageAccountKey() throws URISyntaxException, InvalidKeyException, StorageException {

       return "";
    }

    private String getAccountName(){
        return accountName;
    }

    private String getEndpointSuffix(){
        return endpointSuffix;
    }

    private String getAccountKey(){
        return "e6NYXGu69nW0iQ3JtWpkdOwWPzwyoacXOH+3wHZyTT2c0E7p9suw16mT7m5/pKI3Mum7llS+ueJX+AStTOxnlQ==";
    }

    /*
    Below method generates account level sas
    You can use "az storage container generate-sas" to create service SAS and "az storage account generate-sas" to create account SAS, Account SAS looks like "se=2020-06-01&sp=r&sv=2018-03-28&ss=b", service SAS doesn't have "ss=b"
    Refer - https://stackoverflow.com/questions/41697925/what-is-difference-between-account-vs-service-sas-in-azure
     */
    public String getSasTokenUsingSharedAccessPolicy() throws InvalidKeyException, StorageException, URISyntaxException {
         /*
        Reference:
        1. https://stackoverflow.com/questions/53653473/generating-an-sas-token-in-java-to-download-a-file-in-an-azure-data-storage-cont
         */
        String accountName = getAccountName();
        String accountKey = getAccountKey();
        String endpointSuffix = getEndpointSuffix();
        String storageConnectionString = String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=%s",
                accountName, accountKey, endpointSuffix);
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

        SharedAccessAccountPolicy sharedAccessAccountPolicy = new SharedAccessAccountPolicy();
        sharedAccessAccountPolicy.setPermissionsFromString("racwdlup");
        long date = new Date().getTime();
        long expiryDate = new Date(date + 8640000).getTime();
        sharedAccessAccountPolicy.setSharedAccessStartTime(new Date(date));
        sharedAccessAccountPolicy.setSharedAccessExpiryTime(new Date(expiryDate));
        sharedAccessAccountPolicy.setResourceTypeFromString("sco");
        sharedAccessAccountPolicy.setServiceFromString("bfqt");
        String sasToken = "?" + storageAccount.generateSharedAccessSignature(sharedAccessAccountPolicy);
        return sasToken;
    }

    @GetMapping("/generateSASToken")
    public String getSasToken(){
        try{
            return getSasTokenUsingSharedAccessPolicy();
        }catch (Exception e) {
            System.out.println(e);
        }
        return "";
    }
}
