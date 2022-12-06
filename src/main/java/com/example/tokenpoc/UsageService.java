package com.example.tokenpoc;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.microsoft.azure.storage.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@Service
public class UsageService {
    private final String STORAGE_ACCOUNT_URL = "https://saspossa1.blob.core.windows.net";
    BlobClient blobClient;
    BlobContainerClient blobContainerClient;
    BlobServiceClient blobServiceClient;
    @Autowired
    SasService controller;

    public void uploadBlobUsingAccountToken(String containerName, String accountName, String accountKey, String endpointSuffix) throws URISyntaxException, InvalidKeyException, StorageException {
        String token = controller.getSasTokenUsingSharedAccessPolicy(accountName, accountKey, endpointSuffix);
        blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(STORAGE_ACCOUNT_URL)
                .sasToken(token)
                .buildClient();
        blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        blobClient = blobContainerClient.getBlobClient("blob3");
        blobClient = blobContainerClient.getBlobClient("blob3");
        blobClient.uploadFromFile("/Users/bvarshne/Desktop/blob3.png");
    }

    public void uploadBlobUsingDelegationToken(String tenantId, String clientId, String clientSecret, String accountName, String containerName) throws IOException {
        String token = controller.getUserDelegationSas3(tenantId, clientId, clientSecret, accountName, containerName);
        blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(STORAGE_ACCOUNT_URL)
                .sasToken(token)
                .buildClient();
        blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        blobClient = blobContainerClient.getBlobClient("blob4");
        blobClient.uploadFromFile("/Users/bvarshne/Desktop/blob3.png");
    }

    public void getAllBlobsFromDelegation(String tenantId, String clientId, String clientSecret, String accountName, String containerName) throws IOException {
        String token = controller.getUserDelegationSas2(tenantId, clientId, clientSecret, accountName, containerName);
        blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(STORAGE_ACCOUNT_URL)
                .sasToken(token)
                .buildClient();
        blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            System.out.println("This is the blob name: " + blobItem.getName());
        }
    }

    public void getAllBlobsFromAccount(String accountName,String accountKey,String endpointSuffix, String containerName) throws URISyntaxException, InvalidKeyException, StorageException {
        String token = controller.getSasTokenUsingSharedAccessPolicy(accountName, accountKey, endpointSuffix);
        blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(STORAGE_ACCOUNT_URL)
                .sasToken(token)
                .buildClient();
        blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            System.out.println("This is the blob name: " + blobItem.getName());
        }
    }

    public void downloadBlobContentFromDelegation(String tenantId, String clientId, String clientSecret, String accountName, String containerName) throws IOException {
        String token = controller.getUserDelegationSas3(tenantId, clientId, clientSecret, accountName, containerName);
        blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(STORAGE_ACCOUNT_URL)
                .sasToken(token)
                .buildClient();
        blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = blobContainerClient.getBlobClient("blob3");
        BinaryData content = blobClient.downloadContent();
        System.out.println(content);
    }

    public void downloadBlobContentFromAccount(String containerName, String accountName, String accountKey, String endpointSuffix) throws URISyntaxException, InvalidKeyException, StorageException {
        String token = controller.getSasTokenUsingSharedAccessPolicy(accountName, accountKey, endpointSuffix);
        blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(STORAGE_ACCOUNT_URL)
                .sasToken(token)
                .buildClient();
        blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = blobContainerClient.getBlobClient("blob3");
        BinaryData content = blobClient.downloadContent();
        System.out.println(content);
    }
}
