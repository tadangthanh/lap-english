package lap_english.controller;

import lap_english.service.IAzureService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/blob")
@RequiredArgsConstructor
public class AzureBlobController {
    private final IAzureService azureService;

    @GetMapping("/{blobName}")
    public ResponseEntity<InputStreamResource> getAudio(@PathVariable("blobName") String blobName) throws IOException {
        // Trả về stream qua ResponseEntity
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + blobName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Định dạng file âm thanh
                .body(new InputStreamResource(new ByteArrayInputStream(azureService.downloadBlob(blobName).readAllBytes())));
    }


}
