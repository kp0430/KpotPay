package com.KpotTipouts.KpotPay.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record EarningsSummaryDTO(LocalDate startDate,
                                 LocalDate endDate,
                                 BigDecimal TotalFoodSales,
                                 BigDecimal TotalBarSales,
                                 BigDecimal TotalFoodTipOut,
                                 BigDecimal TotalBarTipOut,
                                 BigDecimal TotalTipOut,
                                 BigDecimal TotalTips,
                                 BigDecimal TotalNetTips,
                                 List<ShiftResponseDTO> shifts) {
}
