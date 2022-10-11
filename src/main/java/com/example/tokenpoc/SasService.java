package com.example.tokenpoc;

import com.azure.identity.AzureAuthorityHosts;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.UserDelegationKey;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.SharedAccessAccountPolicy;
import com.microsoft.azure.storage.StorageException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Locale;

@Service
public class SasService {
    public String getUserDelegationSas2(String tenantId, String clientId, String clientSecret, String accountName, String containerName) throws IOException {
        String authorityUrl = AzureAuthorityHosts.AZURE_PUBLIC_CLOUD +  tenantId;

        ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                .authorityHost(authorityUrl)
                .tenantId(tenantId)
                .clientSecret(clientSecret)
                .clientId(clientId)
                .build();


        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .credential(credential)
                .endpoint("https://"+accountName+".blob.core.windows.net")
                .buildClient();

        // Upload a file
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = blobContainerClient.getBlobClient(null);
        //BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(new File("/Users/bvarshne/Desktop/blob3.png")));
        //blobClient.upload(bufferedInputStream, bufferedInputStream.available(),true);


        OffsetDateTime keyStart = OffsetDateTime.now();
        OffsetDateTime keyExpiry = OffsetDateTime.now().plusDays(7);


        UserDelegationKey userDelegationKey = blobServiceClient.getUserDelegationKey(keyStart, keyExpiry);

        BlobContainerSasPermission blobContainerSas = new BlobContainerSasPermission();
        blobContainerSas.setReadPermission(true);
        blobContainerSas.setCreatePermission(true);
        blobContainerSas.setAddPermission(true);
        blobContainerSas.setWritePermission(true);
        blobContainerSas.setExecutePermission(true);
        blobContainerSas.setListPermission(true);
        blobContainerSas.setDeletePermission(true);
        blobContainerSas.setImmutabilityPolicyPermission(true);
        blobContainerSas.setTagsPermission(true);
        blobContainerSas.setMovePermission(true);
        BlobServiceSasSignatureValues blobServiceSasSignatureValues = new BlobServiceSasSignatureValues(keyExpiry,
                blobContainerSas);

        String sas = "?"+blobClient.generateUserDelegationSas(blobServiceSasSignatureValues, userDelegationKey);
        return sas;
    }

    public String getUserDelegationSas(String accountName, String containerName) {
        /*
        Reference: https://stackoverflow.com/questions/64242397/azure-sdk-for-java-how-to-setup-user-delegation-key-and-shared-authentication-si
         */
        String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", accountName);

// Create a BlobServiceClient object which will be used to create a container client
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().endpoint(endpoint)
                .credential(new DefaultAzureCredentialBuilder().build()).buildClient();

// Get a user delegation key for the Blob service that's valid for seven days.
// You can use the key to generate any number of shared access signatures over the lifetime of the key.
        OffsetDateTime keyStart = OffsetDateTime.now();
        OffsetDateTime keyExpiry = OffsetDateTime.now().plusDays(7);
        UserDelegationKey userDelegationKey = blobServiceClient.getUserDelegationKey(keyStart, keyExpiry);

        BlobContainerSasPermission blobContainerSas = new BlobContainerSasPermission();
        blobContainerSas.setReadPermission(true);
        BlobServiceSasSignatureValues blobServiceSasSignatureValues = new BlobServiceSasSignatureValues(keyExpiry,
                blobContainerSas);
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!blobContainerClient.exists())
            blobContainerClient.create();

        String sas = blobContainerClient
                .generateUserDelegationSas(blobServiceSasSignatureValues, userDelegationKey);
        return sas;
    }

    /*
    Below method generates account level sas
    You can use "az storage container generate-sas" to create service SAS and "az storage account generate-sas" to create account SAS, Account SAS looks like "se=2020-06-01&sp=r&sv=2018-03-28&ss=b", service SAS doesn't have "ss=b"
    Refer - https://stackoverflow.com/questions/41697925/what-is-difference-between-account-vs-service-sas-in-azure
     */
    public String getSasTokenUsingSharedAccessPolicy(String accountName, String accountKey, String endpointSuffix) throws InvalidKeyException, StorageException, URISyntaxException {
         /*
        Reference:
        1. https://stackoverflow.com/questions/53653473/generating-an-sas-token-in-java-to-download-a-file-in-an-azure-data-storage-cont
         */
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

}
