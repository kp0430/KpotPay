package com.KpotTipouts.KpotPay.Controller;

import com.KpotTipouts.KpotPay.DTO.*;
import com.KpotTipouts.KpotPay.Repository.ShiftRepository;
import com.KpotTipouts.KpotPay.Service.ShiftService;
import com.KpotTipouts.KpotPay.Repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class   ShiftController {
    private final ShiftService shiftService;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;

    public ShiftController(ShiftService shiftService, UserRepository userRepository, ShiftRepository shiftRepository) {
        this.shiftService = shiftService;
        this.userRepository = userRepository;
        this.shiftRepository = shiftRepository;
    }

    @PostMapping("/shifts")
    public ResponseEntity<ShiftResponseDTO> createShift(Authentication authentication, @Valid @RequestBody ShiftCreateDTO dto) {
        return userRepository.findByEmail(authentication.getName())
                //look up user by email and if they exist, create shift and convert it into response DTO and send it back with 201 status
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(shiftService.toResponseDTO(shiftService.createShift(dto, user))))
                // if findByEmail is empty, return 404 not found instead
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/shifts/{shiftId}")
    public ResponseEntity<ShiftResponseDTO> getShiftById(@PathVariable Long shiftId, Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .flatMap(user -> shiftRepository.findByIdAndUser(shiftId,user))
                .map(shift -> ResponseEntity.ok(shiftService.toResponseDTO(shift)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/shifts")
    public ResponseEntity<List<ShiftResponseDTO>> getAllShiftsForUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .map(user -> shiftRepository.findByUser(user).stream()
                        .map(shiftService::toResponseDTO)
                        .toList())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/shifts/{shiftId}")
    public ResponseEntity<ShiftResponseDTO> patchShift(@PathVariable Long shiftId, @Valid @RequestBody ShiftPatchDTO dto, Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .flatMap(user -> shiftRepository.findByIdAndUser(shiftId,user))
                .map(shift -> shiftService.applyPatch(shift, dto))
                .map(shiftService::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/shifts/{shiftId}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long shiftId, Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .flatMap(user -> shiftRepository.findByIdAndUser(shiftId,user))
                .map(shift -> {
                    shiftRepository.delete(shift);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(()-> ResponseEntity.notFound().build());
    }
    @GetMapping("/shifts/summary")
    public ResponseEntity<EarningsSummaryDTO> getEarningsSummary(
            Authentication authentication,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        //get a user through userId
        return userRepository.findByEmail(authentication.getName())
                // map user into getEarningsSummary
                .map(user -> shiftService.getEarningsSummary(user, startDate, endDate))
                //map earnings summary into ResponseEntity
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



}