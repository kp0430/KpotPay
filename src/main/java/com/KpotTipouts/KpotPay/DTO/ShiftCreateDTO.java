package com.KpotTipouts.KpotPay.DTO;

import com.KpotTipouts.KpotPay.Entity.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ShiftCreateDTO(
        @NotNull LocalDate date,
        @PositiveOrZero BigDecimal foodSales,
        @PositiveOrZero BigDecimal barSales,
        @NotNull @PositiveOrZero BigDecimal hoursWorked,
        @NotNull Role role,
        @PositiveOrZero BigDecimal tips
    ) {}
