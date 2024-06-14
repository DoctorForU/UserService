package com.example.UserService.dto.apiServer;

import lombok.Data;

@Data
public class ApiServerRequest {
    private String userIdentity; // 프론트 받아와야 함
    private String token; // doctorforUdb - table Root -> access, refresh, organizationName, organizationEmail, organizationPW
}
