package com.KpotTipouts.KpotPay.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegisterDTO(
        @NotNull String name,
        @NotNull @Email String email,
        @NotNull String password
) {
}
