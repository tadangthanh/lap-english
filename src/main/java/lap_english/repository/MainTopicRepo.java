package lap_english.repository;

import lap_english.entity.MainTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainTopicRepo extends JpaRepository<MainTopic, Long> , JpaSpecificationExecutor<MainTopic> {
    @Query("SELECT CASE WHEN COUNT(mt) > 0 THEN TRUE ELSE FALSE END FROM MainTopic mt WHERE lower(mt.name) = lower(?1)")
    boolean existByName(String name);
    @Query("SELECT mt FROM MainTopic mt WHERE mt.isWord = true")
    List<MainTopic> findAllMainTopicIsWord();

    @Query("SELECT mt FROM MainTopic mt WHERE mt.isWord = false")
    List<MainTopic> findAllMainTopicIsSentence();
}
