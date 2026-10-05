package com.KpotTipouts.KpotPay.Controller;

import com.KpotTipouts.KpotPay.DTO.RegisterDTO;
import com.KpotTipouts.KpotPay.DTO.UserResponseDTO;
import com.KpotTipouts.KpotPay.Entity.User;
import com.KpotTipouts.KpotPay.Repository.UserRepository;
import com.KpotTipouts.KpotPay.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }
    @PostMapping("/auth/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterDTO dto) {
        User user = userService.registerUser(dto);
        return ResponseEntity.ok(userService.toResponseDTO(user));
    }



}