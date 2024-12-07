package lap_english.service;

import lap_english.dto.TypeGrammarDto;
import lap_english.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ITypeGrammarService {
    void delete(Long id);

    TypeGrammarDto create(TypeGrammarDto typeGrammarDto);

    TypeGrammarDto update(TypeGrammarDto typeGrammarDto);

    TypeGrammarDto findById(Long id);
    PageResponse<?> advanceSearchBySpecification(Pageable pageable, String[] typeGrammars);
}
