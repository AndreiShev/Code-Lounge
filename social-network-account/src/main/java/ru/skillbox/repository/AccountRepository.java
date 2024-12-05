package ru.skillbox.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.entity.Account;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE Account a SET a.isBlocked = :blocked, a.updatedOn = CURRENT_TIMESTAMP WHERE a.id = :accountId")
    int updateBlocked(UUID accountId, boolean blocked);

    @Modifying
    @Query("UPDATE Account a SET a.isDeleted = :deleted, a.updatedOn = CURRENT_TIMESTAMP, a.isOnline = false WHERE a.id = :accountId")
    int updateDeleted(UUID accountId, boolean deleted);

    @Query("SELECT a.id FROM Account a")
    List<UUID> findAllIds();

    @Query("SELECT a FROM Account a WHERE a.id IN :ids")
    Page<Account> findAccountsByIds(List<UUID> ids, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.isOnline = true, a.lastOnlineTime = CURRENT_TIMESTAMP WHERE a.id = :accountId")
    void updateOnlineStatus(UUID accountId);

    @Modifying
    @Query("UPDATE Account a SET a.isOnline = false WHERE a.lastOnlineTime < :fiveMinutesAgo")
    void updateOfflineStatus(LocalDateTime fiveMinutesAgo);

}
