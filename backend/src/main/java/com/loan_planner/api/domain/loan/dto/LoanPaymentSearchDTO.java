package com.loan_planner.api.domain.loan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoanPaymentSearchDTO {

    private String keyword;
    private String keywordHash;
    private String memo;

}
