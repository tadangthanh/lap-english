package lap_english.service.impl;

import jakarta.validation.ValidationException;
import lap_english.dto.WordDto;
import lap_english.entity.WordLevel;
import lap_english.entity.WordType;
import lap_english.service.IImportWordExcelService;
import lap_english.util.ObjectsValidator;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImportWordExcelServiceImpl implements IImportWordExcelService {
    private final ObjectsValidator<WordDto> objectsValidator;

    @Override
    public List<WordDto> importWordExcel(MultipartFile file) {
        List<WordDto> wordList = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0); // Đọc sheet đầu tiên
            Iterator<Row> rows = sheet.iterator();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // Bỏ qua tiêu đề (header)
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                WordDto word = new WordDto();
                word.setWord(getCellValue(currentRow.getCell(0))); // column 1 : word
                word.setMeaning(getCellValue(currentRow.getCell(1)));// column 2 : meaning
                word.setPronounceUK(getCellValue(currentRow.getCell(2)));// column 3 : pronounceUK
                word.setPronounceUS(getCellValue(currentRow.getCell(3)));// column 4 : pronounceUS
                word.setType(WordType.valueOf(getCellValue(currentRow.getCell(4))));// column 5 : type
                word.setLevel(WordLevel.valueOf(getCellValue(currentRow.getCell(5))));// column 6 : level
                word.setExample(getCellValue(currentRow.getCell(6)));// column 7 : example
                //validate word
                validateWord(word, currentRow);
                wordList.add(word);
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

    private static String getColumnLetter(int columnIndex) {
        return String.valueOf((char) ('A' + columnIndex));
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            double numericValue = cell.getNumericCellValue();
            if (numericValue == Math.floor(numericValue)) {
                return String.valueOf((long) numericValue);
            } else {
                return String.valueOf(numericValue);
            }
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }
        return "";
    }
}
