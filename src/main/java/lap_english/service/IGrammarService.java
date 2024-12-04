package lap_english.service;

import lap_english.dto.GrammarDto;
import lap_english.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface IGrammarService {
    void delete(Long id);

    GrammarDto create(GrammarDto grammarDto);

    GrammarDto update(GrammarDto grammarDto);

    GrammarDto findById(Long id);

    PageResponse<?> advanceSearchBySpecification(Pageable pageable, String[] grammar);
}
