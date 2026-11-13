package com.loan_planner.api.domain.loan.mapper;

import com.loan_planner.api.domain.loan.dto.LoanPaymentHistoryItemDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentHistoryListDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentRequestDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentSearchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoanMapper {

    void savePayment(LoanPaymentRequestDTO requestDTO);

    List<LoanPaymentHistoryListDTO> getPayHistoryList(LoanPaymentSearchDTO searchDto);

    void updatePayHistoryItem(LoanPaymentHistoryItemDTO itemDTO);

    void deletePayHistoryItem(LoanPaymentHistoryItemDTO itemDTO);

}