package ru.skillbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.model.setting.UserNotificationSettingEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationSettingRepository extends JpaRepository<UserNotificationSettingEntity, UUID> {
    Optional<UserNotificationSettingEntity> findByUserId(UUID userId);
}
