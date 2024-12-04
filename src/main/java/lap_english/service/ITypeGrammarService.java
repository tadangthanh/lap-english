package lap_english.service;

import lap_english.dto.TypeGrammarDto;

public interface ITypeGrammarService {
    void delete(Long id);

    TypeGrammarDto create(TypeGrammarDto typeGrammarDto);

    TypeGrammarDto update(TypeGrammarDto typeGrammarDto);

    TypeGrammarDto findById(Long id);
}
