package com.example.tokenpoc;

import com.microsoft.azure.storage.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@RestController
public class PocController {
    @Value("${account.name}")
    private String accountName;
    @Value("${tenant.id}")
    String tenantId;
    @Value("${secret.key}")
    String clientSecret;
    @Value("${client.id}")
    String clientId;
    @Value("${container.name}")
    String containerName;
    @Value("${account.key}")
    String accountKey;
    //private final String CONTAINER_NAME = ""
    @Autowired
    SasService sasService;

    @Autowired
    UsageService usageService;

    private String endpointSuffix = "core.windows.net";

    @GetMapping("/getUserDelegationTokenFromFile")
    public String getUserDelegationFromFile() throws IOException {
        return sasService.getUserDelegationSas2(tenantId, clientId, clientSecret, accountName, containerName);
    }

    @GetMapping("/getUserDelegationTokenFromEnv")
    private String getUserDelegationFromEnv(){
        return sasService.getUserDelegationSas(accountName, containerName);
    }

    @GetMapping("/getAccountToken")
    public String getSasToken() throws URISyntaxException, InvalidKeyException, StorageException {
        return sasService.getSasTokenUsingSharedAccessPolicy(accountName, accountKey, endpointSuffix);
    }

    @GetMapping("/Usage/storageDelegation/uploadBlob")
    public void uploadBlobUsingAccountToken() throws URISyntaxException, InvalidKeyException, StorageException{
        usageService.uploadBlobUsingAccountToken(containerName, accountName, accountKey, endpointSuffix);
    }

    @GetMapping("/Usage/userDelegation/uploadBlob")
    public void uploadBlobUsingDelegationToken() throws IOException {
        usageService.uploadBlobUsingDelegationToken(tenantId, clientId, clientSecret, accountName, containerName);
    }

    @GetMapping("/Usage/userDelegation/getAllBlobs")
    public void getAllBlobsFromDelegation() throws IOException {
        usageService.getAllBlobsFromDelegation(tenantId, clientId, clientSecret, accountName, containerName);
    }

    @GetMapping("/Usage/account/getAllBlobs")
    public void getAllBlobsFromAccount() throws URISyntaxException, InvalidKeyException, StorageException {
        usageService.getAllBlobsFromAccount(accountName, accountKey,endpointSuffix,containerName);
    }

    @GetMapping("/Usage/userDelegation/downloadBlobContent")
    public void downloadBlobContentFromDelegation() throws IOException {
        usageService.downloadBlobContentFromDelegation(tenantId, clientId, clientSecret, accountName, containerName);
    }

    @GetMapping("/Usage/account/downloadBlobContent")
    public void downloadBlobContentFromAccount() throws URISyntaxException, InvalidKeyException, StorageException {
        usageService.downloadBlobContentFromAccount(containerName, accountName, accountKey, endpointSuffix);
    }
}
