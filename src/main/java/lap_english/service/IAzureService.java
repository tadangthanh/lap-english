package lap_english.service;

import lap_english.dto.response.BlobResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IAzureService {
    BlobResponse upload(MultipartFile file);

    String getBlobUrl(String blobName);

    boolean deleteBlob(String blobName);
}
