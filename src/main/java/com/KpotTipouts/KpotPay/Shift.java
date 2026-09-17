package com.KpotTipouts.KpotPay;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "shifts")
@Getter
@Setter
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    @Column(precision = 10, scale = 2)
    private BigDecimal foodSales;
    @Column(precision = 10, scale = 2)
    private BigDecimal barSales;
    @Column(precision = 10, scale = 2)
    private BigDecimal hoursWorked;
    @Enumerated(EnumType.STRING)
    private Role role;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
