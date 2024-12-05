package ru.skillbox.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.entity.Dialog;
import ru.skillbox.entity.Message;
import ru.skillbox.entity.Status;


@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {


    @Query("SELECT COUNT(m) FROM Message m WHERE m.status = :status")
    Long countUnreadMessages(@Param("status") Status status);


    Page<Message> findByDialog(Dialog dialog, Pageable pageable);

}
