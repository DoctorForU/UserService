package com.example.UserService.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@Builder
@Table(name = "reservationHospital")
@NoArgsConstructor
@AllArgsConstructor
public class ReservationHospital { // 진료 예약 테이블 -> hospitalService랑 feign연결 하기
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false) // 이거 fk로 만들기
    private String userId;

    @Column(name = "duty_name", nullable = false)
    private String dutyName; // 병원명

    @Column(name = "hpid", nullable = false)
    private String hpid; // 병원 코드

    @Column(name = "reservation_date", nullable = false)
    private LocalDateTime reservationDate;  // 예약 날짜

    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime; // 예약 시간

    @Column(name = "symptom", nullable = false)
    private String symptom; // 증상
}
