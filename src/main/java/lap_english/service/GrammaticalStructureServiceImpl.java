package lap_english.service;

import jakarta.transaction.Transactional;
import lap_english.dto.GrammaticalStructureDto;
import lap_english.dto.response.PageResponse;
import lap_english.entity.Grammar;
import lap_english.entity.GrammaticalStructure;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.GrammaticalStructureMapper;
import lap_english.repository.GrammarRepo;
import lap_english.repository.GrammaticalStructureRepo;
import lap_english.repository.specification.EntitySpecificationsBuilder;
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

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GrammaticalStructureServiceImpl implements IGrammaticalStructureService {
    private final GrammaticalStructureRepo grammaticalStructureRepo;
    private final GrammaticalStructureMapper grammaticalStructureMapper;
    private final GrammarRepo grammarRepo;

    @Override
    public void delete(Long id) {

    }

    @Override
    public GrammaticalStructureDto create(GrammaticalStructureDto grammaticalStructureDto) {
        GrammaticalStructure grammaticalStructure = grammaticalStructureMapper.toEntity(grammaticalStructureDto);
        Grammar grammar = findGrammarById(grammaticalStructureDto.getGrammarId());
        grammaticalStructure.setGrammar(grammar);
        grammaticalStructureRepo.save(grammaticalStructure);
        return grammaticalStructureMapper.toDto(grammaticalStructure);
    }

    @Override
    public GrammaticalStructureDto update(GrammaticalStructureDto grammaticalStructureDto) {
        GrammaticalStructure grammaticalStructureExist = findByGrammaticalStructureId(grammaticalStructureDto.getId());
        grammaticalStructureMapper.updateFromDto(grammaticalStructureDto, grammaticalStructureExist);
        return grammaticalStructureMapper.toDto(grammaticalStructureRepo.save(grammaticalStructureExist));
    }

    @Override
    public GrammaticalStructureDto findById(Long id) {
        GrammaticalStructure grammaticalStructure = grammaticalStructureRepo.findById(id).orElseThrow(() -> {
            log.error("Grammatical structure not found");
            return new ResourceNotFoundException("Grammatical structure not found with id: " + id);
        });
        return grammaticalStructureMapper.toDto(grammaticalStructure);
    }

    @Override
    public PageResponse<?> advanceSearchBySpecification(Pageable pageable, String[] grammaticalStructure) {
        if (grammaticalStructure != null && grammaticalStructure.length > 0) {
            EntitySpecificationsBuilder<GrammaticalStructure> builder = new EntitySpecificationsBuilder<>();
            Pattern pattern = Pattern.compile(REGEX_SEARCH);
            for (String s : grammaticalStructure) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }
            Specification<GrammaticalStructure> spec = builder.build();
            Page<GrammaticalStructure> grammaticalStructurePage = grammaticalStructureRepo.findAll(spec, pageable);
            return convertToPageResponse(grammaticalStructurePage, pageable);
        }
        return convertToPageResponse(grammaticalStructureRepo.findAll(pageable), pageable);
    }

    private PageResponse<?> convertToPageResponse(Page<GrammaticalStructure> grammaticalStructurePage, Pageable pageable) {
        List<GrammaticalStructureDto> response = grammaticalStructurePage.stream().map(this.grammaticalStructureMapper::toDto).collect(toList());
        return PageResponse.builder().items(response).totalItems(grammaticalStructurePage.getTotalElements()).totalPage(grammaticalStructurePage.getTotalPages()).hasNext(grammaticalStructurePage.hasNext()).pageNo(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }

    private GrammaticalStructure findByGrammaticalStructureId(Long id) {
        return grammaticalStructureRepo.findById(id).orElseThrow(() -> {
            log.error("Grammatical structure not found");
            return new ResourceNotFoundException("Grammatical structure not found with id: " + id);
        });
    }

    private Grammar findGrammarById(Long id) {
        return grammarRepo.findById(id).orElseThrow(() -> {
            log.error("Grammar not found");
            return new ResourceNotFoundException("Grammar not found with id: " + id);
        });
    }
}
