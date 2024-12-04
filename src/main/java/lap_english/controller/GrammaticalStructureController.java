package lap_english.controller;

import lap_english.dto.GrammaticalStructureDto;
import lap_english.dto.response.ResponseData;
import lap_english.service.IGrammaticalStructureService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/grammatical-structure")
public class GrammaticalStructureController {
    private final IGrammaticalStructureService grammaticalStructureService;

    @PostMapping
    public ResponseData<?> create(@Validated(Create.class) @RequestBody GrammaticalStructureDto grammaticalStructureDto) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", grammaticalStructureService.create(grammaticalStructureDto));
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable("id") Long id, @Validated(Update.class) @RequestBody GrammaticalStructureDto grammaticalStructureDto) {
        grammaticalStructureDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", grammaticalStructureService.update(grammaticalStructureDto));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable("id") Long id) {
        grammaticalStructureService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", null);
    }

    @GetMapping
    public ResponseData<?> advanceSearchBySpecification(Pageable pageable, @RequestParam(required = false, value = "grammaticalStructure") String[] grammaticalStructure) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", grammaticalStructureService.advanceSearchBySpecification(pageable, grammaticalStructure));
    }
}
