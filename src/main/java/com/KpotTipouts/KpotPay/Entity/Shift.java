package com.KpotTipouts.KpotPay.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
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

@Column(precision = 10, scale = 2, nullable = false)
@PositiveOrZero
private BigDecimal kidsLunchSales;

@Column(precision = 10, scale = 2, nullable = false)
@PositiveOrZero
private BigDecimal kidsDinnerSales;

@Column(precision = 10, scale = 2, nullable = false)
@PositiveOrZero
private BigDecimal adultLunchSales;

@Column(precision = 10, scale = 2, nullable = false)
@PositiveOrZero
private BigDecimal adultDinnerSales;

@Column(precision = 10, scale = 2, nullable = false)
private BigDecimal barSales;

@Column(precision = 4, scale = 2, nullable = false)
private BigDecimal hoursWorked;

@Enumerated(EnumType.STRING)
private Role role;

@ManyToOne
@JoinColumn(name = "user_id")
User user;
@Column(precision = 10, scale = 2, nullable = false)
private BigDecimal tips;
}
