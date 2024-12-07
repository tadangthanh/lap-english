package lap_english.controller;

import lap_english.dto.GrammarDto;
import lap_english.dto.response.ResponseData;
import lap_english.service.IGrammarService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/grammar")
@RestController
@Validated
public class GrammarController {
    private final IGrammarService grammarService;

    @PostMapping
    public ResponseData<?> create(@Validated(Create.class) @RequestBody GrammarDto dto) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", grammarService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable Long id, @Validated(Update.class) @RequestBody GrammarDto dto) {
        dto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", grammarService.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable Long id) {
        grammarService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", null);
    }

    @GetMapping("/{id}")
    public ResponseData<?> findById(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", grammarService.findById(id));
    }

    @GetMapping
    public ResponseData<?> advanceSearchBySpecification(Pageable pageable, @RequestParam(required = false, value = "grammar") String[] grammar) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", grammarService.advanceSearchBySpecification(pageable, grammar));
    }
}
