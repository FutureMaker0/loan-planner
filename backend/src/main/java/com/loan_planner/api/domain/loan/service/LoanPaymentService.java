package com.loan_planner.api.domain.loan.service;

import com.loan_planner.api.domain.loan.dto.LoanPaymentRequestDTO;
import org.springframework.stereotype.Service;

@Service
public interface LoanPaymentService {

    Long savePayment(LoanPaymentRequestDTO requestDTO);

}
