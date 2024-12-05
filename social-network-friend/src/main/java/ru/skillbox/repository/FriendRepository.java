package ru.skillbox.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.skillbox.entity.Friend;
import ru.skillbox.enums.StatusCode;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>, JpaSpecificationExecutor<Friend> {
    List<Friend> findAllByAccountIdAndStatusCode(UUID accountId, StatusCode statusCode);

    boolean existsByAccountIdAndFriendId(UUID accountId, UUID friendId);

    Optional<Friend> findByAccountIdAndFriendId(UUID principalId, UUID friendId);

    Long countByAccountIdAndStatusCode(UUID principalId, StatusCode statusCode);
}
