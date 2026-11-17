package com.loan_planner.api.domain.loan.mapper;

import com.loan_planner.api.domain.loan.dto.LoanPaymentHistoryItemDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentHistoryListDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentRequestDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentSearchDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionItemDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionRequestDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionSearchDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionsListDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoanMapper {

    /* 납부 이력 */
    void savePayment(LoanPaymentRequestDTO requestDTO);

    List<LoanPaymentHistoryListDTO> getPayHistoryList(LoanPaymentSearchDTO searchDto);

    void updatePayHistoryItem(LoanPaymentHistoryItemDTO itemDTO);

    void deletePayHistoryItem(LoanPaymentHistoryItemDTO itemDTO);

    /* 상환 방식 */
    void savePayOption(LoanPayOptionRequestDTO requestDTO);

    List<LoanPayOptionsListDTO> getPayOptionsList(LoanPayOptionSearchDTO searchDto);

    void updatePayOptionItem(LoanPayOptionItemDTO itemDTO);

    void deletePayOptionItem(LoanPayOptionItemDTO itemDTO);

}