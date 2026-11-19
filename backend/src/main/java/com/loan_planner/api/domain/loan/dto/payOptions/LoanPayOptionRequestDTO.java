package com.loan_planner.api.domain.loan.dto.payOptions;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoanPayOptionRequestDTO {

    private Long repaymentId;
    private String repaymentType;
    private String repaymentName;
    private String description;
    private Character useYn;

}
