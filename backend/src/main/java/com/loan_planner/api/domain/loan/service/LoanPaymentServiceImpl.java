package com.loan_planner.api.domain.loan.service;

import com.loan_planner.api.domain.loan.dto.LoanPaymentRequestDTO;
import com.loan_planner.api.domain.loan.mapper.LoanMapper;
import org.springframework.stereotype.Service;

@Service
public class LoanPaymentServiceImpl implements LoanPaymentService {

    private final LoanMapper loanMapper;

    public LoanPaymentServiceImpl(LoanMapper loanMapper) {
        this.loanMapper = loanMapper;
    }

    @Override
    public Long savePayment(LoanPaymentRequestDTO requestDTO) {

        loanMapper.savePayment(requestDTO);

        return 0L;
    }
}
