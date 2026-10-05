package com.KpotTipouts.KpotPay.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginDTO(
        @NotNull @Email String email,
        @NotNull @NotBlank String password) {
}
