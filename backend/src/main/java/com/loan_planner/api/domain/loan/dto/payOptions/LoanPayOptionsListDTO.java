package com.loan_planner.api.domain.loan.dto.payOptions;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LoanPayOptionsListDTO {

    private Long repaymentId;
    private String repaymentType;
    private String repaymentName;
    private String description;
    private Character useYn;

}


