package com.loan_planner.api.domain.loan.dto.payOptions;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoanPayOptionSearchDTO {

    private String repaymentType;
    private String repaymentName;
    private String description;

}
