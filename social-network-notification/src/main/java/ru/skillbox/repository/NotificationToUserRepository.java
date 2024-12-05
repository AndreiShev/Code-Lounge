package ru.skillbox.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.model.notification.NotificationToUserEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationToUserRepository extends JpaRepository<NotificationToUserEntity, UUID> {

    @Query("SELECT n FROM NotificationToUserEntity n WHERE n.toUserId = :toUserId AND n.isStatusSent = false")
    Page<NotificationToUserEntity> findByToUserIddWithPageable(@Param("toUserId") UUID toUserId, Pageable pageable);
    @Query("SELECT n FROM NotificationToUserEntity n WHERE n.toUserId = :toUserId AND n.isStatusSent = false")
    List<NotificationToUserEntity> findNotSendingByToUserId(@Param("toUserId") UUID toUserId);

    @Modifying
    @Query("UPDATE NotificationToUserEntity n SET n.isStatusSent = :sentStatus WHERE n.id in (:ids)")
    void updateSendingStatus(Boolean sentStatus, List<UUID> ids);
    @Query("SELECT count(n.id) FROM NotificationToUserEntity n WHERE n.toUserId = :toUserId  AND n.isStatusSent = false")
    Integer getCountNotSentUserNotification(UUID toUserId);
}
