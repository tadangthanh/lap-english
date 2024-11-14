package lap_english.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import lap_english.dto.response.BlobResponse;
import lap_english.exception.UploadFailureException;
import lap_english.service.IAzureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AzureBlobServiceImpl implements IAzureService {
    @Value("${spring.cloud.azure.storage.blob.container-name}")
    private String containerName;
    private final BlobServiceClient blobServiceClient;
    @Value("${azure.blob-storage.connection-string}")
    private String connectionString;


    @Override
    public BlobResponse upload(MultipartFile file) {
        try {
            long var10000 = System.currentTimeMillis();
            String blobFileName = "" + var10000 + "_" + file.getOriginalFilename();
            BlobClient blobClient = this.blobServiceClient.getBlobContainerClient(this.containerName).getBlobClient(blobFileName);
            blobClient.upload(file.getInputStream(), file.getSize(), true);
            return BlobResponse.builder().blobName(blobFileName).url(this.getBlobUrl(blobFileName)).build();
        } catch (IOException var4) {
            throw new UploadFailureException("Lỗi khi upload file");
        }
    }

    @Override
    public String getBlobUrl(String blobName) {
        BlobServiceClient blobServiceClient = (new BlobServiceClientBuilder()).connectionString(this.connectionString).buildClient();
        BlobClient blobClient = blobServiceClient.getBlobContainerClient(this.containerName).getBlobClient(blobName);
        OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(10L);
        BlobSasPermission permission = (new BlobSasPermission()).setReadPermission(true);
        BlobServiceSasSignatureValues sasValues = (new BlobServiceSasSignatureValues(expiryTime, permission)).setStartTime(OffsetDateTime.now());
        String sasToken = blobClient.generateSas(sasValues);
        String var10000 = blobClient.getBlobUrl();
        return var10000 + "?" + sasToken;
    }

    @Override
    public boolean deleteBlob(String blobName) {
        BlobClient blobClient = this.blobServiceClient.getBlobContainerClient(this.containerName).getBlobClient(blobName);
        return blobClient.deleteIfExists();
    }
}
