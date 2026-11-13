package com.loan_planner.api.domain.loan.web;

import com.loan_planner.api.domain.loan.dto.LoanPaymentHistoryItemDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentHistoryListDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentRequestDTO;
import com.loan_planner.api.domain.loan.dto.LoanPaymentSearchDTO;
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
        System.out.println("### savePayment requestDTO: " + requestDTO.toString());
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
        System.out.println("### deletePayHistoryItem paymentId: " + paymentId);
        LoanPaymentHistoryItemDTO itemDTO = new LoanPaymentHistoryItemDTO();
        itemDTO.setPaymentId(paymentId);
        System.out.println("### deletePayHistoryItem itemDTO: " + itemDTO.toString());
        loanPaymentService.deletePayHistoryItem(itemDTO);
        return ResponseEntity.ok().build();
    }

}
