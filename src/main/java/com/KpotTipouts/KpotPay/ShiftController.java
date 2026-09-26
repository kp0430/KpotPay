package com.KpotTipouts.KpotPay;

import com.KpotTipouts.KpotPay.DTO.ShiftCreateDTO;
import com.KpotTipouts.KpotPay.DTO.ShiftPatchDTO;
import com.KpotTipouts.KpotPay.DTO.ShiftResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ShiftController {
    private final ShiftService shiftService;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;

    public ShiftController(ShiftService shiftService, UserRepository userRepository, ShiftRepository shiftRepository) {
        this.shiftService = shiftService;
        this.userRepository = userRepository;
        this.shiftRepository = shiftRepository;
    }

    @PostMapping("/users/{userId}/shifts")

    public ResponseEntity<ShiftResponseDTO> createShift(@PathVariable Long userId, @Valid @RequestBody ShiftCreateDTO dto) {
       return userRepository.findById(userId)
               //look up user by id and if they exist, create shift and convert it into response DTO and send it back with 201 status
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(shiftService.toResponseDTO(shiftService.createShift(dto, user))))
               // if findById is empty, return 404 not found instead
                .orElseGet(()->ResponseEntity.notFound().build());
    }
    @GetMapping("/shifts/{shiftId}")
    public ResponseEntity<ShiftResponseDTO> getShiftById(@PathVariable Long shiftId) {
       return shiftRepository.findById(shiftId)
               //if shift found in database, convert into responseDTO and send it back with 200 OK response
               .map(shift -> ResponseEntity.ok(shiftService.toResponseDTO(shift)))
               .orElseGet(()->ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}/shifts")
    public ResponseEntity<List<ShiftResponseDTO>> getAllShiftsForUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> shiftRepository.findByUser(user).stream()
                        .map(shiftService::toResponseDTO)
                        .toList())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/shifts/{shiftId}")
    public ResponseEntity<ShiftResponseDTO> patchShift(@PathVariable Long shiftId, @Valid @RequestBody ShiftPatchDTO dto) {
        return shiftRepository.findById(shiftId)
                .map(shift -> shiftService.applyPatch(shift, dto))
                    .map(shiftService::toResponseDTO)
                .map(ResponseEntity::ok)
                    .orElseGet(()->ResponseEntity.notFound().build());
    }

    @DeleteMapping("/shifts/{shiftId}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long shiftId) {
        if  (shiftRepository.existsById(shiftId)) {
            shiftRepository.deleteById(shiftId);
            return ResponseEntity.noContent().build();
        }
        else  {
            return ResponseEntity.notFound().build();
        }
    }

}