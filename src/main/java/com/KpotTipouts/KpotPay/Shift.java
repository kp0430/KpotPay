package com.KpotTipouts.KpotPay;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "shifts")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private BigDecimal foodSales;
    private BigDecimal barSales;
    private BigDecimal hoursWorked;
    private Role role;
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user = new User();
}
