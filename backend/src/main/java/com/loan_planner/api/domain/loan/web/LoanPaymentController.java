package com.loan_planner.api.domain.loan.web;

import com.loan_planner.api.domain.loan.dto.LoanPaymentHistoryItemDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentHistoryListDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentRequestDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentSearchDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionItemDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionRequestDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionSearchDTO;
import com.loan_planner.api.domain.loan.dto.payOptions.LoanPayOptionsListDTO;
import com.loan_planner.api.domain.loan.service.LoanPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class LoanPaymentController {

    private final LoanPaymentService loanPaymentService;

    /**
     * 대출 납부 이력 등록
     */
    @PostMapping
    public ResponseEntity<Void> savePayment(@RequestBody LoanPaymentRequestDTO requestDTO) {
        loanPaymentService.savePayment(requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<LoanPaymentHistoryListDTO>> getPayHistoryList(LoanPaymentSearchDTO searchDto) {
        return ResponseEntity.ok(loanPaymentService.getPayHistoryList(searchDto));
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<Void> updatePayHistoryItem(@PathVariable Long paymentId, @RequestBody LoanPaymentHistoryItemDTO itemDTO) {
        itemDTO.setPaymentId(paymentId);
        loanPaymentService.updatePayHistoryItem(itemDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayHistoryItem(@PathVariable Long paymentId) {
        LoanPaymentHistoryItemDTO itemDTO = new LoanPaymentHistoryItemDTO();
        itemDTO.setPaymentId(paymentId);
        loanPaymentService.deletePayHistoryItem(itemDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * 상환 방식 등록
     */
    @PostMapping("/options")
    public ResponseEntity<Void> savePayOption(@RequestBody LoanPayOptionRequestDTO requestDTO) {
        loanPaymentService.savePayOption(requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/options")
    public ResponseEntity<List<LoanPayOptionsListDTO>> getPayOptionsList(LoanPayOptionSearchDTO searchDto) {
        return ResponseEntity.ok(loanPaymentService.getPayOptionsList(searchDto));
    }

    @PutMapping("/options/{optionId}")
    public ResponseEntity<Void> updatePayOptionItem(@PathVariable Long optionId, @RequestBody LoanPayOptionItemDTO itemDTO) {
        itemDTO.setOptionId(optionId);
        loanPaymentService.updatePayOptionItem(itemDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<Void> deletePayOptionItem(@PathVariable Long optionId) {
        LoanPayOptionItemDTO itemDTO = new LoanPayOptionItemDTO();
        itemDTO.setOptionId(optionId);
        loanPaymentService.deletePayOptionItem(itemDTO);
        return ResponseEntity.ok().build();
    }

}
