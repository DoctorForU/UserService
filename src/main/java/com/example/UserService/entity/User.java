package com.example.UserService.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false, length = 30)
    private String userId;

    @Column(name = "user_name", nullable = false, length = 20)
    private String userName;

    @Column(name = "user_password", nullable = false, length = 200)
    private String userPassword;

    @Column(name = "user_email", nullable = false, length = 255)
    private String userEmail;

    @Column(name = "user_phone_number", nullable = false, length = 255)
    private String userPhoneNumber;
//    public User(String userId, String userName, String userPassword, String userEmail, String userPhoneNumber) {
//        this.userId = userId;
//        this.userName = userName;
//        this.userPassword = userPassword;
//        this.userEmail = userEmail;
//        this.userPhoneNumber = userPhoneNumber;
//    }
}
