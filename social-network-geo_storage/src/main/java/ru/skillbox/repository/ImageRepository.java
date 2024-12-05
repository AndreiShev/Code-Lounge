package ru.skillbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skillbox.entity.ImageRecord;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<ImageRecord, Long> {
  @Modifying
  void deleteByUserIdEqualsAndValueEquals (UUID userId, String oldImage);

  @Query("SELECT i.value FROM ImageRecord i WHERE i.userId = :id")
  List<String> findAllValueByUserIdEquals(UUID id);

  void deleteAllByUserIdEquals(UUID userId);

}
