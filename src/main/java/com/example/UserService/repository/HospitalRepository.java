package com.example.UserService.repository;

import com.example.UserService.entity.FavoriteHospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<FavoriteHospital, Long> {
    Optional<FavoriteHospital> findByUserId(String userid);

}
