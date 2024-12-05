package ru.skillbox.repository;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.entity.Dialog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DialogRepository extends JpaRepository<Dialog, Long> {


    @EntityGraph(attributePaths = {"messages"})
    @Query("SELECT d FROM Dialog d WHERE " +
            "(d.participantOne = :participantOneId AND d.participantTwo = :participantTwoId) " +
            "OR (d.participantOne = :participantTwoId AND d.participantTwo = :participantOneId)")
    Optional<Dialog> findByParticipants(@Param("participantOneId") UUID participantOneId,
                                    @Param("participantTwoId") UUID participantTwoId);


    @EntityGraph(attributePaths = "messages")
    List<Dialog> findByParticipantOneOrParticipantTwo(UUID participantOne, UUID participantTwo);


}
