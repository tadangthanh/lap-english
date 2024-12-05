package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.GrammarDto;
import lap_english.dto.response.PageResponse;
import lap_english.entity.Grammar;
import lap_english.entity.TypeGrammar;
import lap_english.exception.DuplicateResource;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.GrammarMapper;
import lap_english.repository.GrammarRepo;
import lap_english.repository.TypeGrammarRepo;
import lap_english.repository.specification.EntitySpecificationsBuilder;
import lap_english.service.IGrammarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static lap_english.util.SearchUtil.REGEX_SEARCH;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class GrammarServiceImpl implements IGrammarService {
    private final GrammarRepo grammarRepo;
    private final GrammarMapper grammarMapper;
    private final TypeGrammarRepo typeGrammarRepo;

    @Override
    public void delete(Long id) {
        // Todo
    }

    @Override
    public GrammarDto create(GrammarDto grammarDto) {
        validateGrammar(grammarDto);
        TypeGrammar typeGrammar = findTypeGrammarById(grammarDto.getTypeGrammarId());
        Grammar grammar = grammarMapper.toEntity(grammarDto);
        grammarRepo.save(grammar);
        grammar.setTypeGrammar(typeGrammar);
        return grammarMapper.toDto(grammar);
    }

    @Override
    public GrammarDto update(GrammarDto grammarDto) {
        Grammar grammarExist = findGrammarById(grammarDto.getId());
        grammarMapper.updateFromDto(grammarDto, grammarExist);
        grammarExist = grammarRepo.saveAndFlush(grammarExist);
        if (grammarRepo.findByName(grammarDto.getName()).size() > 1) {
            log.error("Grammar name already exists: {}", grammarDto.getName());
            throw new DuplicateResource("Grammar name already exists");
        }
        return grammarMapper.toDto(grammarRepo.save(grammarExist));
    }

    @Override
    public GrammarDto findById(Long id) {
        return grammarMapper.toDto(findGrammarById(id));
    }

    @Override
    public PageResponse<?> advanceSearchBySpecification(Pageable pageable, String[] grammar) {
        if (grammar != null && grammar.length > 0) {
            EntitySpecificationsBuilder<Grammar> builder = new EntitySpecificationsBuilder<>();
            Pattern pattern = Pattern.compile(REGEX_SEARCH);
            for (String s : grammar) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }
            Specification<Grammar> spec = builder.build();
            Page<Grammar> grammarPage = grammarRepo.findAll(spec, pageable);
            return convertToPageResponse(grammarPage, pageable);
        }
        return convertToPageResponse(grammarRepo.findAll(pageable), pageable);
    }

    private TypeGrammar findTypeGrammarById(Long id) {
        return typeGrammarRepo.findById(id).orElseThrow(() -> {
            log.warn("Type grammar not found with id: {}", id);
            return new ResourceNotFoundException("Type grammar not found with id: " + id);
        });
    }

    private Grammar findGrammarById(Long id) {
        return grammarRepo.findById(id).orElseThrow(() -> {
            log.warn("Grammar not found with id: {}", id);
            return new ResourceNotFoundException("Grammar not found with id: " + id);
        });
    }

    private PageResponse<?> convertToPageResponse(Page<Grammar> grammarPage, Pageable pageable) {
        List<GrammarDto> response = grammarPage.stream().map(this.grammarMapper::toDto).collect(toList());
        return PageResponse.builder().items(response).totalItems(grammarPage.getTotalElements()).totalPage(grammarPage.getTotalPages()).hasNext(grammarPage.hasNext()).pageNo(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }

    private void validateGrammar(GrammarDto grammarDto) {
        if (grammarRepo.existsByName(grammarDto.getName())) {
            log.error("Grammar name already exists: {}", grammarDto.getName());
            throw new DuplicateResource("Grammar name already exists");
        }
    }
}
