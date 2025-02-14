package ru.skillbox.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.skillbox.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    @Override
    Optional<Post> findById(Long aLong);
}

