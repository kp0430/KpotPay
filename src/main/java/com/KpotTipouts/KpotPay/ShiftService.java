package com.KpotTipouts.KpotPay;

import com.KpotTipouts.KpotPay.DTO.EarningsSummaryDTO;
import com.KpotTipouts.KpotPay.DTO.ShiftCreateDTO;
import com.KpotTipouts.KpotPay.DTO.ShiftPatchDTO;
import com.KpotTipouts.KpotPay.DTO.ShiftResponseDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShiftService {
    private final ShiftRepository shiftRepository;

    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

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
                    .multiply(new BigDecimal("0.10"))
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
    private BigDecimal calculateNetTip(Shift shift) {
        return shift.getTips().subtract(calculateTotalTipOut(shift))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public Shift createShift(ShiftCreateDTO shiftCreateDTO, User user) {
        Shift shift = new Shift();
        shift.setDate(shiftCreateDTO.date());
        shift.setFoodSales(shiftCreateDTO.foodSales() != null ? shiftCreateDTO.foodSales() : BigDecimal.ZERO);
        shift.setBarSales(shiftCreateDTO.barSales() != null ? shiftCreateDTO.barSales() : BigDecimal.ZERO);
        shift.setHoursWorked(shiftCreateDTO.hoursWorked());
        shift.setRole(shiftCreateDTO.role());
        shift.setUser(user);
        shift.setTips(shiftCreateDTO.tips() != null ? shiftCreateDTO.tips() : BigDecimal.ZERO);
        return shiftRepository.save(shift);
    }

    public ShiftResponseDTO toResponseDTO(Shift shift) {

        return new ShiftResponseDTO(
                shift.getId(),
                shift.getDate(),
                shift.getFoodSales(),
                shift.getBarSales(),
                shift.getHoursWorked(),
                shift.getRole(),
                calculateFoodTipOut(shift),
                calculateBarTipOut(shift),
                calculateTotalTipOut(shift),
                shift.getTips(),
                calculateNetTip(shift)
        );
    }
    // we are updating the shift with the shiftPatchDTO, if it's not null, patch the shift, if it is, just keep the shift variable the same
    public Shift applyPatch(Shift shift, ShiftPatchDTO shiftPatchDTO) {
        shift.setDate(shiftPatchDTO.date() != null ? shiftPatchDTO.date() : shift.getDate());
        shift.setFoodSales(shiftPatchDTO.foodSales() != null ? shiftPatchDTO.foodSales() : shift.getFoodSales());
        shift.setBarSales(shiftPatchDTO.barSales() != null ? shiftPatchDTO.barSales() : shift.getBarSales());
        shift.setHoursWorked(shiftPatchDTO.hoursWorked() != null ? shiftPatchDTO.hoursWorked() : shift.getHoursWorked());
        shift.setRole(shiftPatchDTO.role() != null ? shiftPatchDTO.role() : shift.getRole());
        shift.setTips(shiftPatchDTO.tips());
        return shiftRepository.save(shift);
    }
    public EarningsSummaryDTO getEarningsSummary(User user, LocalDate startDate, LocalDate endDate) {
        List<ShiftResponseDTO> shiftDTOs = new ArrayList<>();
        List<Shift> shifts = shiftRepository.findByUserAndDateBetween(user, startDate, endDate);
        BigDecimal totalFoodSales = BigDecimal.ZERO;
        BigDecimal totalBarSales = BigDecimal.ZERO;
        BigDecimal totalTips = BigDecimal.ZERO;
        BigDecimal totalNetTips = BigDecimal.ZERO;
        BigDecimal totalFoodTipOut = BigDecimal.ZERO;
        BigDecimal totalBarTipOut = BigDecimal.ZERO;
        BigDecimal totalTipOut = BigDecimal.ZERO;

        for(Shift shift : shifts){
            totalFoodSales = totalFoodSales.add(shift.getFoodSales());
            totalBarSales = totalBarSales.add(shift.getBarSales());
            totalFoodTipOut = totalFoodTipOut.add(calculateFoodTipOut(shift));
            totalBarTipOut = totalBarTipOut.add(calculateBarTipOut(shift));
            totalTipOut = totalTipOut.add(calculateTotalTipOut(shift));
            totalTips = totalTips.add(shift.getTips());
            totalNetTips = totalNetTips.add(calculateNetTip(shift));
            shiftDTOs.add(toResponseDTO(shift));
        }
        return new EarningsSummaryDTO(
                startDate,
                endDate,
                totalFoodSales,
                totalBarSales,
                totalFoodTipOut,
                totalBarTipOut,
                totalTipOut,
                totalTips,
                totalNetTips,
                shiftDTOs
        );

    }

}
