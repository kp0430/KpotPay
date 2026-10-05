package com.KpotTipouts.KpotPay.Service;

import com.KpotTipouts.KpotPay.DTO.LoginDTO;
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
    private final JwtService jwtService;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder =  passwordEncoder;
        this.jwtService = jwtService;
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
    public String login(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.email())
                .orElseThrow(()-> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return jwtService.generateToken(user.getEmail());
    }


}
