package lap_english.service;

import lap_english.dto.TypeGrammarDto;
import lap_english.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITypeGrammarService {
    void delete(Long id);

    TypeGrammarDto create(TypeGrammarDto typeGrammarDto);

    TypeGrammarDto update(TypeGrammarDto typeGrammarDto);

    TypeGrammarDto findById(Long id);
    PageResponse<List<TypeGrammarDto>> advanceSearchBySpecification(Pageable pageable, String[] typeGrammars);
}
