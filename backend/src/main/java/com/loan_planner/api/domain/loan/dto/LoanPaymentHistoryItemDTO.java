package com.loan_planner.api.domain.loan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanPaymentHistoryItemDTO {

    private Long loanId;
    private Long paymentId;

}
