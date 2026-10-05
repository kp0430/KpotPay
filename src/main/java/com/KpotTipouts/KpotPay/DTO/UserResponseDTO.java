package com.KpotTipouts.KpotPay.DTO;

import jakarta.validation.constraints.Email;


public record UserResponseDTO(
        Long id,
        String name,
        @Email String email
) {
}
