package lap_english.service.impl;

import jakarta.validation.ValidationException;
import lap_english.dto.WordDto;
import lap_english.service.IReaderWordExcelService;
import lap_english.util.ExcelReaderUtil;
import lap_english.util.ObjectsValidator;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReaderWordExcelServiceImpl implements IReaderWordExcelService {
    private final ObjectsValidator<WordDto> objectsValidator;
    private final ExcelReaderUtil<WordDto> excelReaderUtil;

    @Override
    public List<WordDto> importWordExcel(MultipartFile file) {
        // neu file k hop le se throw ra exception
        excelReaderUtil.validateFile(file);
        List<WordDto> wordList = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0); // Đọc sheet đầu tiên
            Iterator<Row> rows = sheet.iterator();
            int rowNumber = 0;
            // map chứa tên cột và index của cột
            Map<String, Integer> header = new HashMap<>();
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // Bỏ qua tiêu đề (header)
                if (rowNumber == 0) {
                    header = excelReaderUtil.getHeaderColumnName(currentRow);
                    rowNumber++;
                    continue;
                }
                WordDto wordDto = excelReaderUtil.convertToEntity(header, currentRow, WordDto.class);
                //validate word
                validateWord(wordDto, currentRow);
                wordList.add(wordDto);
            }
        } catch (IOException e) {
            log.error("Fail to import word: {}", e.getMessage());
        }
        return wordList;
    }

    private void validateWord(WordDto wordDto, Row row) {
        try {
            objectsValidator.validate(wordDto, Update.class);
        } catch (ValidationException e) {
            log.error("Row {} is invalid: {}", row.getRowNum(), e.getMessage());
            throw new ValidationException("Row " + row.getRowNum() + " is invalid: " + e.getMessage());
        }
    }

}
