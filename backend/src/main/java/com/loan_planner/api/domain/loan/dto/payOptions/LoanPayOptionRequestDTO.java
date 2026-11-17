package com.loan_planner.api.domain.loan.dto.payOptions;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoanPayOptionRequestDTO {

    private Long optionId;
    private String optionType;
    private String description;
    private Character useYn;

}
