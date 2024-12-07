package lap_english.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BlobResponse {
    private String blobName;
    private String url;
}
