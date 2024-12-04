package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.TypeGrammarDto;
import lap_english.entity.TypeGrammar;
import lap_english.exception.DuplicateResource;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.TypeGrammarMapper;
import lap_english.repository.TypeGrammarRepo;
import lap_english.service.IGrammarService;
import lap_english.service.ITypeGrammarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TypeGrammarServiceImpl implements ITypeGrammarService {
    private final TypeGrammarRepo typeGrammarRepo;
    private final TypeGrammarMapper typeGrammarMapper;
    private final IGrammarService grammarService;


    @Override
    public void delete(Long id) {
        // Todo implement later
    }

    @Override
    public TypeGrammarDto create(TypeGrammarDto typeGrammarDto) {
        validateTypeGrammar(typeGrammarDto);
        TypeGrammar typeGrammar = typeGrammarMapper.toEntity(typeGrammarDto);
        return typeGrammarMapper.toDto(typeGrammarRepo.save(typeGrammar));
    }

    @Override
    public TypeGrammarDto update(TypeGrammarDto typeGrammarDto) {
        validateTypeGrammar(typeGrammarDto);
        TypeGrammar typeGrammarExist = findTypeGrammarById(typeGrammarDto.getId());
        typeGrammarMapper.updateEntityFromDto(typeGrammarDto, typeGrammarExist);
        return typeGrammarMapper.toDto(typeGrammarRepo.save(typeGrammarExist));
    }

    @Override
    public TypeGrammarDto findById(Long id) {
        TypeGrammar typeGrammarExist = findTypeGrammarById(id);
        return typeGrammarMapper.toDto(typeGrammarExist);
    }

    private TypeGrammar findTypeGrammarById(Long id) {
        return typeGrammarRepo.findById(id).orElseThrow(() -> {
            log.warn("TypeGrammar not found with id: {}", id);
            return new ResourceNotFoundException("TypeGrammar not found");
        });
    }

    private void validateTypeGrammar(TypeGrammarDto typeGrammarDto) {
        if (typeGrammarRepo.existsByName(typeGrammarDto.getName())) {
            throw new DuplicateResource("TypeGrammar name already exists");
        }
    }
}
