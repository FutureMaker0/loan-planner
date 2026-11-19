import {ref} from "vue";

/**
 * 원리금 균등분할 상환
 * principal: 상환 원금
 * annualRate: 사용자가 입력 연이율(%)
 * months: 상환 개월수
 */
export const calculateEqualPayment = (principal, annualRate, months) => {
  // 연이율을 월이율로 변환
  const rate = annualRate / 100 / 12
  // 월 납입액 계산
  const payment = rate === 0 ? principal / months : principal * rate * Math.pow(1 + rate, months) / (Math.pow(1 + rate, months) - 1)

  let remainingPrincipal = principal  // 잔여 대출 원금
  let interestSum = 0         // 이자 합계
  let paymentSum = 0          // 월 납입액 합계

  const schedule = [];          // 월별 납부 현황 변수

  for (let month = 1; month <= months; month++) {
    // 해당 월 이자
    const interest = remainingPrincipal * rate
    // 마지막 회차는 잔여 원금 전액 상환
    const principalPayment = month === months ? remainingPrincipal : Math.min(payment - interest, remainingPrincipal)
    const actualPayment = principalPayment + interest

    remainingPrincipal = Math.max(0, remainingPrincipal - principalPayment)

    interestSum += interest
    paymentSum += actualPayment

    schedule.push({
      month,
      principal: principalPayment,
      interest,
      payment: actualPayment,
      remaining: remainingPrincipal
    })
  }

  return {
    monthlyAmount: payment,
    schedule,
    interestAmount: interestSum,
    totalAmount: paymentSum
  }
}

/**
 * 원금 균등분할 상환
 * principal: 상환 원금
 * annualRate: 사용자가 입력 연이율(%)
 * months: 상환 개월수
 */
export const calculateEqualPrincipal = (principal, annualRate, months) => {
  const rate = annualRate / 100 / 12
  const principalPerMonth = principal / months

  let remainingPrincipal = principal
  let interestSum = 0
  let paymentSum = 0
  const schedule = []

  for (let month = 1; month <= months; month++) {
    const interest = remainingPrincipal * rate

    const principalPayment = month === months ? remainingPrincipal : Math.min(principalPerMonth, remainingPrincipal)
    const payment = principalPayment + interest

    remainingPrincipal = Math.max(0, remainingPrincipal - principalPayment)

    interestSum += interest
    paymentSum += payment

    schedule.push({
      month,
      principal: principalPayment,
      interest,
      payment,
      remaining: remainingPrincipal
    })
  }

  return {
    monthlyAmount: schedule[0]?.payment ?? 0,
    schedule,
    interestAmount: interestSum,
    totalAmount: paymentSum
  }
}

// 상환 방식별 함수 매핑
const repaymentCalculators = {
  EQUAL_PAYMENT: calculateEqualPayment,
  EQUAL_PRINCIPAL: calculateEqualPrincipal
}

// 공통 계산 함수
export const calculateRepayment = (repaymentType, principal, annualRate, months) => {
  const calculator = repaymentCalculators[repaymentType]
  if (!calculator) {
    throw new Error(`지원하지 않는 상환 방식입니다: ${repaymentType}`)
  }
  return calculator(principal, annualRate, months)
}

/**
 * 체증식 분할 상환
 */
