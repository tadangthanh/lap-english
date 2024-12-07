package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.TypeGrammarDto;
import lap_english.dto.response.PageResponse;
import lap_english.entity.TypeGrammar;
import lap_english.exception.DuplicateResource;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.TypeGrammarMapper;
import lap_english.repository.TypeGrammarRepo;
import lap_english.repository.specification.EntitySpecificationsBuilder;
import lap_english.service.IGrammarService;
import lap_english.service.ITypeGrammarService;
import lap_english.util.SearchUtil;
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
        grammarService.deleteByTypeGrammarId(id);
        typeGrammarRepo.deleteById(id);
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

    @Override
    public PageResponse<?> advanceSearchBySpecification(Pageable pageable, String[] typeGrammars) {
        if (typeGrammars != null && typeGrammars.length > 0) {
            EntitySpecificationsBuilder<TypeGrammar> builder = new EntitySpecificationsBuilder<>();
            Pattern pattern = Pattern.compile(SearchUtil.REGEX_SEARCH);
            for (String s : typeGrammars) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }
            Specification<TypeGrammar> spec = builder.build();
            Page<TypeGrammar> typeGrammarPage = typeGrammarRepo.findAll(spec, pageable);

            return convertToPageResponse(typeGrammarPage, pageable);
        }
        return convertToPageResponse(typeGrammarRepo.findAll(pageable), pageable);
    }

    private PageResponse<?> convertToPageResponse(Page<TypeGrammar> typeGrammarPage, Pageable pageable) {
        List<TypeGrammarDto> response = typeGrammarPage.stream().map(this.typeGrammarMapper::toDto).collect(toList());
        return PageResponse.builder().items(response).totalItems(typeGrammarPage.getTotalElements()).totalPage(typeGrammarPage.getTotalPages()).hasNext(typeGrammarPage.hasNext()).pageNo(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
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
