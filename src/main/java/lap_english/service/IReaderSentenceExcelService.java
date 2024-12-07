package lap_english.service;

import lap_english.dto.SentenceDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IReaderSentenceExcelService {
    List<SentenceDto> importSentenceFromExcel(MultipartFile file);

}
