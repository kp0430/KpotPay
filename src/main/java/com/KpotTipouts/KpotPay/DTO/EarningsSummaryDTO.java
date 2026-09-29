package com.KpotTipouts.KpotPay.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record EarningsSummaryDTO(LocalDate startDate,
                                 LocalDate endDate,
                                 BigDecimal totalFoodSales,
                                 BigDecimal totalBarSales,
                                 BigDecimal totalFoodTipOut,
                                 BigDecimal TotalBarTipOut,
                                 BigDecimal TotalTipOut,
                                 List<ShiftResponseDTO> shifts) {

}
