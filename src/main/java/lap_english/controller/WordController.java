package lap_english.controller;

import jakarta.validation.constraints.Min;
import lap_english.dto.WordDto;
import lap_english.dto.response.ResponseData;
import lap_english.service.IWordService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/word")
@Validated
public class WordController {
    private final IWordService wordService;

    @PostMapping
    public ResponseData<?> create(@Validated(Create.class) @RequestPart("data") WordDto wordDto, @RequestPart(value = "file", required = false) MultipartFile file) {
      try{
          wordDto.setFile(file);
          return new ResponseData<>(HttpStatus.CREATED.value(), "success", wordService.create(wordDto));
      }catch (Exception e){
          return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
      }
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> deleteWord(@PathVariable Long id) {
      try{
          wordService.delete(id);
          return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "success", null);
      }catch (Exception e){
          return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
      }
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable Long id, @Validated(Update.class) @RequestBody WordDto wordDto) {
       try{
           wordDto.setId(id);
           return new ResponseData<>(HttpStatus.OK.value(), "success", wordService.update(wordDto));
       }catch (Exception e){
           return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
       }
    }

    @GetMapping
    public ResponseData<?> getBySubTopic(Pageable pageable,
                                          @RequestParam(required = false, value = "word") String[] word) {
      try{
          return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", wordService.advanceSearchBySpecification(pageable,word));
      }catch (Exception e){
          return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
      }
    }
    @PostMapping("/import/{subTopicId}")
    public ResponseData<?> importWordExcel(@PathVariable @Min(1) Long subTopicId, @RequestPart("file") MultipartFile file) {
        try{
            return new ResponseData<>(HttpStatus.OK.value(), "Processing file... Please wait.", wordService.importFromExcel(subTopicId, file));
        }catch (Exception e){
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

}
