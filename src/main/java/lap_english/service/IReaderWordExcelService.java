package lap_english.service;

import lap_english.dto.WordDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IReaderWordExcelService {
    List<WordDto> importWordExcel(MultipartFile file);
}
