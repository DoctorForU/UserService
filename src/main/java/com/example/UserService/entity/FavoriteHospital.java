package com.example.UserService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name = "favoriteHospital")
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteHospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false, length = 30) // 이거 fk로 만들기
    private String userId;

    @Column(name = "duty_name", nullable = false)
    private String dutyName; // 병원명

    @Column(name = "hpid", nullable = false)
    private String hpid; // 병원 코드

}
