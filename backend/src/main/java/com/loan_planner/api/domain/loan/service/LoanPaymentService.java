package com.loan_planner.api.domain.loan.service;

import com.loan_planner.api.domain.loan.dto.LoanPaymentHistoryItemDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentHistoryListDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentRequestDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentSearchDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LoanPaymentService {

    Long savePayment(LoanPaymentRequestDTO requestDTO);

    List<LoanPaymentHistoryListDTO> getPayHistoryList(LoanPaymentSearchDTO searchDto);

    ResponseEntity<Void> updatePayHistoryItem(LoanPaymentHistoryItemDTO itemDTO);

    ResponseEntity<Void> deletePayHistoryItem(LoanPaymentHistoryItemDTO itemDTO);

}
