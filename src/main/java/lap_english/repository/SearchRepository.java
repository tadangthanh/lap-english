package lap_english.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lap_english.dto.response.PageResponse;
import lap_english.entity.SubTopic;
import lap_english.entity.Word;
import lap_english.repository.specification.SpecSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
@Slf4j
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;
    // tìm kiếm join 2 bảng
    public PageResponse<?> searchWordByCriteriaWithJoin(Pageable pageable, String[] word, String[] address) {
        log.info("-------------- searchUserByCriteriaWithJoin --------------");
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Word> query = builder.createQuery(Word.class);
        Root<Word> wordRoot = query.from(Word.class);
        // join với bảng address bằng field addresses trong entity user
        Join<SubTopic,Word> addressRoot = wordRoot.join("subTopic");
        List<Predicate> userPreList = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
        for (String u : word) {
            Matcher matcher = pattern.matcher(u);
            if (matcher.find()) {
                SpecSearchCriteria searchCriteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                userPreList.add(toPredicate(wordRoot, builder, searchCriteria));
            }
        }
        List<Predicate> addressPreList = new ArrayList<>();
        for (String a : address) {
            Matcher matcher = pattern.matcher(a);
            if (matcher.find()) {
                SpecSearchCriteria searchCriteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                addressPreList.add(toPredicate(addressRoot, builder, searchCriteria));
            }
        }

        Predicate userPre = builder.or(userPreList.toArray(new Predicate[0]));
        Predicate addPre = builder.or(addressPreList.toArray(new Predicate[0]));
        Predicate finalPre = builder.and(userPre, addPre);

        query.where(finalPre);

        List<Word> words = entityManager.createQuery(query)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long count = countWordJoinSubTopic(word, address);

        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalItems(count)
                .items(words)
                .build();
    }
    private Predicate toPredicate(Root<Word> root, CriteriaBuilder builder, SpecSearchCriteria criteria) {
        log.info("-------------- toUserPredicate --------------");
        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }
    private Predicate toPredicate(Join<SubTopic,Word> root, CriteriaBuilder builder, SpecSearchCriteria criteria) {
        log.info("-------------- toAddressPredicate --------------");
        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }
    private long countWordJoinSubTopic(String[] user, String[] address) {
        log.info("-------------- countWordJoinSubTopic --------------");

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Word> wordRoot = query.from(Word.class);
        Join<SubTopic,Word> addressRoot = wordRoot.join("subTopic");

        List<Predicate> userPreList = new ArrayList<>();

        Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
        for (String u : user) {
            Matcher matcher = pattern.matcher(u);
            if (matcher.find()) {
                SpecSearchCriteria searchCriteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                userPreList.add(toPredicate(wordRoot, builder, searchCriteria));
            }
        }

        List<Predicate> addressPreList = new ArrayList<>();
        for (String a : address) {
            Matcher matcher = pattern.matcher(a);
            if (matcher.find()) {
                SpecSearchCriteria searchCriteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                addressPreList.add(toPredicate(addressRoot, builder, searchCriteria));
            }
        }

        Predicate userPre = builder.or(userPreList.toArray(new Predicate[0]));
        Predicate addPre = builder.or(addressPreList.toArray(new Predicate[0]));
        Predicate finalPre = builder.and(userPre, addPre);

        query.select(builder.count(wordRoot));
        query.where(finalPre);

        return entityManager.createQuery(query).getSingleResult();
    }

}
