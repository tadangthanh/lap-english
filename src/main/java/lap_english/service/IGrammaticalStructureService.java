package lap_english.service;

import lap_english.dto.GrammaticalStructureDto;
import lap_english.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGrammaticalStructureService {
    void delete(Long id);

    GrammaticalStructureDto create(GrammaticalStructureDto grammaticalStructureDto);

    GrammaticalStructureDto update(GrammaticalStructureDto grammaticalStructureDto);

    GrammaticalStructureDto findById(Long id);

    PageResponse<List<GrammaticalStructureDto>> advanceSearchBySpecification(Pageable pageable, String[] grammaticalStructure);
    void deleteByGrammarId(Long grammarId);
}
