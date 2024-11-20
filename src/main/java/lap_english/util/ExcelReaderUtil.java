package lap_english.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lap_english.exception.InvalidFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExcelReaderUtil<T> { // T là kiểu dữ liệu của object cần convert
    private final ObjectMapper objectMapper;

    public void validateFile(MultipartFile file) {
        if (file == null) {
            throw new InvalidFileException("No file was provided. Please upload a file.");
        }
        if (file.isEmpty()) {
            throw new InvalidFileException("The uploaded file is empty. Please upload a valid file.");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new InvalidFileException("Invalid file type. Only Excel files (.xlsx, .xls) are allowed.");
        }
    }

    // doc du lieu tu cell
    public String getCellValue(Cell cell) {
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

    // key chứa tên cột, và value chứa index của cột đó
    public Map<String, Integer> getHeaderColumnName(Row row) {
        Map<String, Integer> data = new HashMap<>();
        Iterator<Cell> cellIterator = row.cellIterator();
        int columnIndex = 0;
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            // chuyển hết về chữ thường, mỗi key là 1 field của object
            data.put(getCellValue(cell).toLowerCase(), columnIndex++);
        }
        return data;
    }

    /**
     *
     * @param header : key chứa tên cột, và value chứa index của cột đó trong excel,
     * @param row : dòng dữ liệu được đọc trong excel,
     * @param clazz : kiểu dữ liệu của object cần convert
     * @return : trả về object sau khi convert
     */
    public T convertToEntity( Map<String, Integer> header, Row row, Class<T> clazz) {
        Map<String, String> data = new HashMap<>();
        // key chứa tên cột, và value chứa index của cột đó
        for (Map.Entry<String, Integer> entry : header.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            data.put(key, getCellValue(row.getCell(value)));
        }
        return objectMapper.convertValue(data, clazz);
    }

}
