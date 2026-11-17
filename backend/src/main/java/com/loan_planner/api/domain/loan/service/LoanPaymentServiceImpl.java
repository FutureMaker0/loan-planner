package com.loan_planner.api.domain.loan.service;

import com.loan_planner.api.domain.loan.dto.LoanPaymentHistoryItemDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentHistoryListDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentRequestDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentSearchDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionItemDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionRequestDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionSearchDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionsListDTO;
import com.loan_planner.api.domain.loan.mapper.LoanMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanPaymentServiceImpl implements LoanPaymentService {

    private final LoanMapper loanMapper;

    public LoanPaymentServiceImpl(LoanMapper loanMapper) {
        this.loanMapper = loanMapper;
    }

    /**
     * 납부 이력 관리
     */
    @Override
    public Long savePayment(LoanPaymentRequestDTO requestDTO) {
        loanMapper.savePayment(requestDTO);
        return 0L;
    }

    @Override
    public List<LoanPaymentHistoryListDTO> getPayHistoryList(LoanPaymentSearchDTO searchDto) {
        return loanMapper.getPayHistoryList(searchDto);
    }

    @Override
    public ResponseEntity<Void> updatePayHistoryItem(LoanPaymentHistoryItemDTO itemDTO) {
        loanMapper.updatePayHistoryItem(itemDTO);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deletePayHistoryItem(LoanPaymentHistoryItemDTO itemDTO) {
        loanMapper.deletePayHistoryItem(itemDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * 상환 방식 관리
     */
    @Override
    public Long savePayOption(LoanPayOptionRequestDTO requestDTO) {
        loanMapper.savePayOption(requestDTO);
        return 0L;
    }

    @Override
    public List<LoanPayOptionsListDTO> getPayOptionsList(LoanPayOptionSearchDTO searchDto) {
        return loanMapper.getPayOptionsList(searchDto);
    }

    @Override
    public ResponseEntity<Void> updatePayOptionItem(LoanPayOptionItemDTO itemDTO) {
        loanMapper.updatePayOptionItem(itemDTO);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deletePayOptionItem(LoanPayOptionItemDTO itemDTO) {
        loanMapper.deletePayOptionItem(itemDTO);
        return ResponseEntity.ok().build();
    }

}
