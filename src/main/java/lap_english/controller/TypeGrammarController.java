package lap_english.controller;

import lap_english.dto.TypeGrammarDto;
import lap_english.dto.response.ResponseData;
import lap_english.service.ITypeGrammarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/type-grammar")
@Validated
public class TypeGrammarController {
    private final ITypeGrammarService typeGrammarService;

    @PostMapping
    public ResponseData<?> createTypeGrammar(@Validated @RequestBody TypeGrammarDto typeGrammarDto) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", typeGrammarService.create(typeGrammarDto));
    }

    @PutMapping("/{id}")
    public ResponseData<?> updateTypeGrammar(@PathVariable Long id, @Validated @RequestBody TypeGrammarDto typeGrammarDto) {
        typeGrammarDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", typeGrammarService.update(typeGrammarDto));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> deleteTypeGrammar(@PathVariable Long id) {
        typeGrammarService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "success", null);
    }

    @GetMapping("/{id}")
    public ResponseData<?> getTypeGrammar(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", typeGrammarService.findById(id));
    }

    @GetMapping
    public ResponseData<?> getAllBySearch(Pageable pageable, @RequestParam(required = false, value = "typeGrammars") String[] typeGrammars) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", typeGrammarService.advanceSearchBySpecification(pageable, typeGrammars));
    }
}
