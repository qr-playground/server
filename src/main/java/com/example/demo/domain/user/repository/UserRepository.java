package com.example.demo.domain.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPhoneNumber(String phoneNumber);

    // @Query(value = "select u from User u left join fetch u.qrcodeEvents",
    // countQuery = "select count(u) from User u")
    // Page<User> findAllWithQrcodeEvents(Pageable pageable);
}
