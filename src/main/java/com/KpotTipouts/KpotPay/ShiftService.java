package com.KpotTipouts.KpotPay;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ShiftService {
    // amount you owe to restaurant based off food sales pretty much everything except bar drinks
    private BigDecimal calculateFoodTipOut(Shift shift) {
        return shift.getFoodSales()
                .multiply(new BigDecimal("0.05"))
                .setScale(2, RoundingMode.HALF_UP);
    }
    // bar tipout as in the amount you owe to the restaurant from the bar
    private BigDecimal calculateBarTipOut(Shift shift) {
        if(shift.getRole().equals(Role.SERVER)){
            return shift.getBarSales()
                    .multiply(new BigDecimal("0.05"))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        else {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal calculateTotalTipOut(Shift shift) {
        return calculateFoodTipOut(shift).add(calculateBarTipOut(shift))
                .setScale(2, RoundingMode.HALF_UP);
    }

}
