package ru.skillbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.entities.Account;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Override
    Optional<Account> findById(UUID uuid);

    boolean existsAccountByEmail(String email);

    Optional<Account> findAccountByEmail(String email);
}
