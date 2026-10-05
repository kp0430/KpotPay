package com.KpotTipouts.KpotPay.Service;

import com.KpotTipouts.KpotPay.DTO.RegisterDTO;
import com.KpotTipouts.KpotPay.DTO.UserResponseDTO;
import com.KpotTipouts.KpotPay.Entity.User;
import com.KpotTipouts.KpotPay.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder =  passwordEncoder;
    }

    public UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
    }

    public User registerUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setEmail(registerDTO.email());
        user.setName(registerDTO.name());
        user.setPassword(passwordEncoder.encode(registerDTO.password()));

        return userRepository.save(user);
    }


}
