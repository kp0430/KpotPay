package com.KpotTipouts.KpotPay.DTO;

import com.KpotTipouts.KpotPay.Role;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ShiftPatchDTO(
        LocalDate date,
        @PositiveOrZero BigDecimal foodSales,
        @PositiveOrZero BigDecimal barSales,
        @PositiveOrZero BigDecimal hoursWorked,
        Role role

) {}
