package com.loan_planner.api.domain.loan.mapper;

import com.loan_planner.api.domain.loan.dto.LoanPaymentRequestDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanMapper {

    void savePayment(LoanPaymentRequestDTO requestDTO);

}