package com.example.UserService.dto.hospital;

import lombok.Data;

@Data
public class FavoriteHospitalRequest {
    private String hpid;
    private String dutyName;
    private String userid;
}
