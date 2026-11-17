package com.loan_planner.api.domain.loan.service;

import com.loan_planner.api.domain.loan.dto.LoanPaymentHistoryItemDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentHistoryListDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentRequestDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentSearchDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionItemDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionRequestDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionSearchDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionsListDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LoanPaymentService {

    /* 납부 이력 */
    Long savePayment(LoanPaymentRequestDTO requestDTO);

    List<LoanPaymentHistoryListDTO> getPayHistoryList(LoanPaymentSearchDTO searchDto);

    ResponseEntity<Void> updatePayHistoryItem(LoanPaymentHistoryItemDTO itemDTO);

    ResponseEntity<Void> deletePayHistoryItem(LoanPaymentHistoryItemDTO itemDTO);

    /* 상환 방식 */
    Long savePayOption(LoanPayOptionRequestDTO requestDTO);

    List<LoanPayOptionsListDTO> getPayOptionsList(LoanPayOptionSearchDTO searchDto);

    ResponseEntity<Void> updatePayOptionItem(LoanPayOptionItemDTO itemDTO);

    ResponseEntity<Void> deletePayOptionItem(LoanPayOptionItemDTO itemDTO);

}
