package com.loan_planner.api.domain.loan.web;

import com.loan_planner.api.domain.loan.dto.LoanPaymentRequestDTO;
import com.loan_planner.api.domain.loan.service.LoanPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoanPaymentController {

    private final LoanPaymentService loanPaymentService;

    /**
     * 대출 납부 이력 등록
     */
    @PostMapping("/payment")
    public ResponseEntity<Long> savePayment(
            @RequestBody LoanPaymentRequestDTO requestDTO
    ) {
        Long paymentId = loanPaymentService.savePayment(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentId);
    }

}
