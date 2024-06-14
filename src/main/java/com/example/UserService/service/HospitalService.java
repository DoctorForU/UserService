package com.example.UserService.service;

import com.example.UserService.entity.FavoriteHospital;
import com.example.UserService.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HospitalService {
    private final HospitalRepository hospitalRepository;
    public void register(FavoriteHospital favoriteHospital){
        hospitalRepository.save(favoriteHospital);
    }


}
