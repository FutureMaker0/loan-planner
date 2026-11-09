package com.loan_planner.api.domain.loan.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanPaymentRequestDTO {

    private Long loanId;
    private Long scheduleId;

    private LocalDate paymentDate;

    private BigDecimal totalAmount;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal remainingPrincipal;

    private String paymentMethod;
    private String paymentType;

    private String memo;

}
