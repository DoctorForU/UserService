package com.example.UserService.repository;

import com.example.UserService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {
    //Optional<User> findByUserId(String userId);

    Optional<User> findByUserId(String userId);
    //Optional<User> findByUserId(String userId);
    //Optional<User> findByMemberId(String memberId);
}