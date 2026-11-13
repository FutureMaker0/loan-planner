package com.loan_planner.api.domain.loan.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class LoanPaymentRequestDTO {

    private Long paymentId;
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
    private Character useYn;

}
