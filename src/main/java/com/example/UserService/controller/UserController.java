package com.example.UserService.controller;

import com.example.UserService.api.CommonResponse;
import com.example.UserService.api.status.ErrorStatus;
import com.example.UserService.api.status.SuccessStatus;
import com.example.UserService.dto.user.UserFeignResponse;
import com.example.UserService.dto.user.UserInfoResponse;
import com.example.UserService.dto.user.UserRequest;
import com.example.UserService.dto.user.UserResponse;
import com.example.UserService.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service")
@Slf4j
public class UserController {
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    //아이디 중복 확인
    @GetMapping("/register/{userId}")
    public CommonResponse<String> confirmId(@PathVariable String userId){
        logger.info("userId: " + userId);
        userService.confirmId(userId);
        //logger.info("" + userId);
        return CommonResponse.ok("사용가능한 아이디입니다.");
    }

    //회원가입
    @PostMapping("/join")
    public CommonResponse<String> join(@RequestBody UserRequest.JoinRequest joinRequest){
        userService.join(joinRequest);

        return CommonResponse.ok("회원가입 완료");
    }

    //로그인
    @PostMapping("/login")
    public CommonResponse<String> login(@RequestBody UserRequest.LoginRequest loginRequest, HttpServletResponse res) {
        logger.info("request userId: " + loginRequest.getUserId());
        try {
            UserResponse response = userService.login(loginRequest, res);
            logger.info("response :" + response);
            // return ResponseEntity.ok(response);
            return CommonResponse.ok("로그인 완료");
        } catch (Exception e) {
            logger.error("Error occurred while getting auth token", e);
            return CommonResponse.onFailure(null,"e", "로그인 실패");
        }
    }

    // 유저 정보 확인
    @GetMapping("/userinfo/{userId}")
    public ResponseEntity getUserInfo(@PathVariable String userId) {
        UserInfoResponse response = userService.getUserInfo(userId);
        logger.info("response: " + response);

        return ResponseEntity.ok(response);
    }

    // 유저 정보 수정
    @PostMapping("/updateuser/{userId}")
    public CommonResponse postUpdateUser(@PathVariable String userId, @RequestBody UserRequest.UpdateRequest request){
        log.info("userPassword: " + request.getUserPassword() + "userPhoneNumber: " + request.getUserPhoneNumber());

        userService.postUpdateUser(userId, request);
        return CommonResponse.ok("유저 정보 수정 완료");
    }

    @GetMapping("/auth")
    public UserFeignResponse getUserIdByToken(HttpServletRequest request) {
        log.info("open feign communication success!");
        return userService.getUserIdByToken(request);
    }

//    @PostMapping("/refresh")
//    public CommonResponse<String> refreshToken(HttpServletRequest req, HttpServletResponse res) {
//        try {
//            UserResponse response = userService.refresh(req, res);
//            logger.info("response :" + response);
//            // return ResponseEntity.ok(response);
//            return CommonResponse.ok("토큰 재발급 완료");
//        } catch (Exception e) {
//            logger.error("Error occurred while getting auth token", e);
//        }
//    }
}
