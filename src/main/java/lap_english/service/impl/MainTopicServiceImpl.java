package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.LockStatusManager;
import lap_english.dto.MainTopicDto;
import lap_english.dto.response.PageResponse;
import lap_english.entity.MainTopic;
import lap_english.entity.User;
import lap_english.entity.UserMainTopic;
import lap_english.exception.DuplicateResource;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.MainTopicMapper;
import lap_english.repository.MainTopicRepo;
import lap_english.repository.UserMainTopicRepo;
import lap_english.repository.UserRepo;
import lap_english.repository.specification.EntitySpecificationsBuilder;
import lap_english.service.IMainTopicService;
import lap_english.service.ISubTopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MainTopicServiceImpl implements IMainTopicService {
    private final MainTopicMapper mainTopicMapper;
    private final MainTopicRepo mainTopicRepo;
    private final ISubTopicService subTopicService;
    private final UserMainTopicRepo userMainTopicRepo;
    private final UserRepo userRepo;

    @Override
    public MainTopicDto create(MainTopicDto mainTopicDto) {
        checkExist(mainTopicDto.getName());
        MainTopic mainTopic = mainTopicMapper.toEntity(mainTopicDto);
        mainTopic = mainTopicRepo.save(mainTopic);
        return mainTopicMapper.toDto(mainTopic);
    }

    private void checkExist(String name) {
        if (mainTopicRepo.existByName(name)) {
            log.error("Main Topic is exist");
            throw new DuplicateResource("Main Topic is exist");
        }
    }

    @Override
    public void delete(Long id) {
        MainTopic mainTopic = findMainTopicByIdOrThrow(id);
        subTopicService.deleteByMainTopicId(id);
        mainTopicRepo.delete(mainTopic);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email).orElseThrow(() -> {
            log.error("User not found");
            return new ResourceNotFoundException("User not found");
        });
    }

    @Override
    public MainTopicDto update(MainTopicDto mainTopicDto) {
        MainTopic mainTopicExist = findMainTopicByIdOrThrow(mainTopicDto.getId());
        mainTopicMapper.updateEntityFromDto(mainTopicDto, mainTopicExist);
        return convertMainTopicToDto(mainTopicRepo.save(mainTopicExist));
    }

    private MainTopicDto convertMainTopicToDto(MainTopic mainTopic) {
        MainTopicDto mainTopicDto = mainTopicMapper.toDto(mainTopic);
        LockStatusManager status = new LockStatusManager();
        status.setDiamond(mainTopic.getDiamond());
        status.setGold(mainTopic.getGold());
        User user = getCurrentUser();
        status.setLocked(!userMainTopicRepo.existsByUserIdAndMainTopicId(user.getId(), mainTopic.getId()));
        mainTopicDto.setStatus(status);
        return mainTopicDto;
    }

    private List<MainTopicDto> convertMainTopicToDto(List<MainTopic> mainTopics) {
        return mainTopics.stream().map(this::convertMainTopicToDto).collect(toList());
    }

    @Override
    public PageResponse<?> getPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<MainTopic> mainTopicPage = mainTopicRepo.findAll(pageRequest);
        List<MainTopicDto> mainTopicDtoList = this.convertMainTopicToDto(mainTopicPage.getContent());
        return PageResponse.builder()
                .items(mainTopicDtoList)
                .totalItems(mainTopicPage.getTotalElements())
                .totalPage(mainTopicPage.getTotalPages())
                .hasNext(mainTopicPage.hasNext())
                .pageNo(page)
                .pageSize(size).build();
    }

    @Override
    public List<MainTopicDto> getAll() {
        List<MainTopic> mainTopics = mainTopicRepo.findAll();
        return this.convertMainTopicToDto(mainTopics);
    }

    /**
     * Performs an advanced search for MainTopic entities based on the provided specifications.
     * The specifications are passed as an array of strings, which are parsed and used to build
     * search predicates. The method returns a paginated response containing a list of
     * MainTopicDto objects.
     *
     * @param pageable the pagination information. It includes page number, page size, and sorting options.
     * @param mainTopic an array of strings specifying search criteria. Each string should follow the pattern
     *                  "(attribute)(operation)(value)", where operation can be one of the following: <:>, ~, !.
     *                  This is used to construct search specifications dynamically.
     * @return a paginated PageResponse object containing a list of MainTopicDto that match the search criteria.
     *         If no criteria is provided, it returns a paginated response of all MainTopicDto.
     */
    @Override
    public PageResponse<List<MainTopicDto>> advanceSearchBySpecification(Pageable pageable, String[] mainTopic) {
        if (mainTopic != null && mainTopic.length > 0) {
            EntitySpecificationsBuilder<MainTopic> builder = new EntitySpecificationsBuilder<>();
            Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)"); //?page=0&size=10&sort=id,desc&subtopic=name~d
            for (String s : mainTopic) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }

            Page<MainTopic> mainTopicPage = mainTopicRepo.findAll(builder.build(), pageable);

            return convertToPageResponse(mainTopicPage, pageable);
        }
        return convertToPageResponse(mainTopicRepo.findAll(pageable), pageable);
    }

    /**
     * Mở khóa chủ đề chính được chỉ định cho người dùng hiện tại. Nếu chủ đề đã được mở khóa cho người dùng,
     * nó sẽ trả về chủ đề chính như cũ mà không có bất kỳ thay đổi nào. Ngược lại, nó sẽ tạo một liên kết mới
     * giữa người dùng hiện tại và chủ đề chính.
     *
     * @param id mã định danh duy nhất của chủ đề chính cần mở khóa
     * @return chủ đề chính đã được mở khóa dưới dạng một đối tượng MainTopicDto
     */
    @Override
    public MainTopicDto unLock(Long id) {
        User user = getCurrentUser();
        MainTopic mainTopic = findMainTopicByIdOrThrow(id);
        if (userMainTopicRepo.existsByUserIdAndMainTopicId(user.getId(), id)) {
            return convertMainTopicToDto(mainTopic);
        }
        UserMainTopic userMainTopic = new UserMainTopic();
        userMainTopic.setMainTopic(mainTopic);
        userMainTopic.setUser(user);
        userMainTopicRepo.save(userMainTopic);
        return convertMainTopicToDto(mainTopic);
    }

    private MainTopic findMainTopicByIdOrThrow(Long id) {
        return mainTopicRepo.findById(id).orElseThrow(() -> {
            log.error("Main Topic not found");
            return new ResourceNotFoundException("Main Topic not found");
        });
    }


    private PageResponse<List<MainTopicDto>> convertToPageResponse(Page<MainTopic> mainTopicPage, Pageable pageable) {
        List<MainTopicDto> response = mainTopicPage.stream().map(this::convertMainTopicToDto).collect(toList());
        return PageResponse.<List<MainTopicDto>>builder().items(response).totalItems(mainTopicPage.getTotalElements()).totalPage(mainTopicPage.getTotalPages()).hasNext(mainTopicPage.hasNext()).pageNo(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }
}
