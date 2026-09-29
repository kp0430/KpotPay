package com.KpotTipouts.KpotPay.DTO;

import com.KpotTipouts.KpotPay.Role;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ShiftResponseDTO(
        Long id,
        LocalDate date,
        BigDecimal foodSales,
        BigDecimal barSales,
        BigDecimal hoursWorked,
        Role role,
        BigDecimal foodTipOut,
        BigDecimal barTipOut,
        BigDecimal totalTipOut,
        BigDecimal tips,
        BigDecimal netTips
)     {}


