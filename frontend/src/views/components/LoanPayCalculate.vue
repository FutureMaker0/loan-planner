<script setup>
import {computed, onMounted, ref} from 'vue'
import axios from "axios";

const http = axios.create({
  baseURL: '/api',
  withCredentials: true, // 모든 요청에 쿠키를 포함하여 전송
  headers: {
    'Content-Type': 'application/json',
  },
});

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const loanPayCalculateDialog = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

// 사용자 입력값
// const loanAmount = ref(320000000)
// const monthlyInterestRate = ref(0.4)
// const repaymentMonths = ref(360)

// const paymentOptions = ref([])
const loanAmount = ref(null)
const monthlyInterestRate = ref(null)
const repaymentMonths = ref(null)

// 계산 결과
const schedule = ref([])
const totalAmount = ref(0)      // 총 납부금액 = 원금 + 이자
const principalAmount = ref(0)  // 원금 상환액
const interestAmount = ref(0)   // 이자 납부액
const monthlyAmount = ref(0)    // 월 납입액

const loanForm = ref(null);
const optionTypes = ref([]);
const selectedOptionType = ref(null);

const headers = [
  { title: '회차', key: 'month', align: 'center' },
  { title: '상환 원금', key: 'principal', align: 'end' },
  { title: '상환 이자', key: 'interest', align: 'end' },
  { title: '총 상환금', key: 'payment', align: 'end' },
  { title: '잔여 원금', key: 'balance', align: 'end' }
]

// 상환 방식 조회
const getPayOptionsList = async () => {
  try {
    const response = await http.get('/payment/options');
    optionTypes.value = response.data;
  } catch {
    /* EMPTY */
  }
};

// 원리금균등상환 계산
const calculateLoan = async () => {
  const principal = Number(loanAmount.value)

  // 사용자가 입력하는 값은 연이율(%)
  const annualRate = Number(monthlyInterestRate.value) / 100

  // 연이율을 월이율로 변환
  const rate = annualRate / 12

  const months = Number(repaymentMonths.value)

  // 입력값 검증
  if (
    !Number.isFinite(principal) ||
    !Number.isFinite(rate) ||
    !Number.isInteger(months) ||
    principal <= 0 ||
    rate < 0 ||
    months <= 0
  ) {
    alert('대출 원금, 이자율, 상환 기간을 올바르게 입력해주세요.')
    return
  }

  // 월 납입액 계산
  const payment = rate === 0
    ? principal / months
    : principal * rate * Math.pow(1 + rate, months)
    / (Math.pow(1 + rate, months) - 1)

  let remainingBalance = principal
  let interestSum = 0
  let paymentSum = 0

  const result = []

  for (let month = 1; month <= months; month++) {

    // 해당 월 이자
    const interest = remainingBalance * rate

    // 마지막 회차는 잔여 원금 전액 상환
    const principalPayment = month === months
      ? remainingBalance
      : Math.min(payment - interest, remainingBalance)

    const actualPayment = principalPayment + interest

    remainingBalance = Math.max(
      0,
      remainingBalance - principalPayment
    )

    interestSum += interest
    paymentSum += actualPayment

    result.push({
      month,
      principal: principalPayment,
      interest,
      payment: actualPayment,
      balance: remainingBalance
    })
  }

  // 계산 결과 반영 (반올림은 화면 표시 단계에서 처리)
  monthlyAmount.value = payment
  schedule.value = result
  interestAmount.value = interestSum
  totalAmount.value = paymentSum
}

const formatCurrency = value => {
  return Math.round(value).toLocaleString('ko-KR') + '원'
}

onMounted(() => {
  getPayOptionsList();
});
</script>

<template>
  <v-dialog v-model="loanPayCalculateDialog" max-width="900" persistent>
    <v-card>
      <v-card-title class="d-flex justify-space-between align-center">
        <v-btn
          icon="mdi-close"
          variant="text"
          @click="loanPayCalculateDialog = false"
        />
      </v-card-title>

      <v-divider />
        <!-- 사용자 입력 Form -->
        <h3 class="text-subtitle-1 mb-4">Loan Information</h3>

        <v-card-text class="pa-5">
          <v-form ref="loanForm" @submit.prevent="calculateLoan">
            <v-row>
              <!-- 상환 방식 -->
              <v-col cols="12">
                <v-select
                  v-model="selectedOptionType"
                  label="상환 방식"
                  :items="optionTypes"
                  item-title="optionType"
                  item-value="optionType"
                  placeholder="상환 방식을 선택하세요."
                  variant="outlined"
                />
              </v-col>

              <!-- 대출 원금 -->
              <v-col cols="12">
                <v-text-field
                  v-model.number="loanAmount"
                  label="대출 원금"
                  placeholder="대출 원금을 입력하세요."
                  suffix="원"
                  type="number"
                  variant="outlined"
                  :rules="[v => v !== null && v > 0 || '대출 원금을 입력하세요.']"
                />
              </v-col>

              <!-- 월 이자율 -->
              <v-col cols="12">
                <v-text-field
                  v-model.number="monthlyInterestRate"
                  label="월 이자율"
                  placeholder="월 이자율을 입력하세요."
                  suffix="%"
                  type="number"
                  variant="outlined"
                  :rules="[v => v !== null && v >= 0 || '월 이자율을 입력하세요.']"
                />
              </v-col>

              <!-- 상환 기간 -->
              <v-col cols="12">
                <v-text-field
                  v-model.number="repaymentMonths"
                  label="상환 기간"
                  placeholder="상환 기간을 입력하세요."
                  suffix="개월"
                  type="number"
                  variant="outlined"
                  :rules="[v => Number.isInteger(v) && v > 0 || '상환 기간을 입력하세요.']"
                />
              </v-col>
            </v-row>

            <div class="d-flex justify-end">
              <v-btn color="primary" type="submit">
                Calculate
              </v-btn>
            </div>
          </v-form>
        </v-card-text>

        <!-- 계산 결과 -->
        <div v-if="schedule.length > 0" class="mt-6">
          <v-divider class="mb-4" />

          <h3 class="text-subtitle-1 mb-4">Calculation Result</h3>

          <v-row class="mb-4">
            <v-col cols="12" md="3">
              <v-card variant="tonal" class="pa-3">
                <div class="text-caption">Total Amount</div>
                <div class="text-h6 font-weight-bold">
                  {{ formatCurrency(totalAmount) }}
                </div>
              </v-card>
            </v-col>

            <v-col cols="12" md="3">
              <v-card variant="tonal" class="pa-3">
                <div class="text-caption">Principal Amount</div>
                <div class="text-h6 font-weight-bold">
                  {{ formatCurrency(loanAmount) }}
                </div>
              </v-card>
            </v-col>

            <v-col cols="12" md="3">
              <v-card variant="tonal" class="pa-3">
                <div class="text-caption">Interest Amount</div>
                <div class="text-h6 font-weight-bold">
                  {{ formatCurrency(interestAmount) }}
                </div>
              </v-card>
            </v-col>

            <v-col cols="12" md="3">
              <v-card variant="tonal" class="pa-3">
                <div class="text-caption">Monthly Amount</div>
                <div class="text-h6 font-weight-bold">
                  {{ formatCurrency(monthlyAmount) }}
                </div>
              </v-card>
            </v-col>
          </v-row>

          <v-data-table
            :headers="headers"
            :items="schedule"
            :items-per-page="12"
            density="compact"
            class="elevation-1"
          >
            <template #item.principal="{ item }">
              {{ formatCurrency(item.principal) }}
            </template>

            <template #item.interest="{ item }">
              {{ formatCurrency(item.interest) }}
            </template>

            <template #item.payment="{ item }">
              {{ formatCurrency(item.payment) }}
            </template>

            <template #item.balance="{ item }">
              {{ formatCurrency(item.balance) }}
            </template>
          </v-data-table>
        </div>
      <v-divider />

      <v-card-actions class="pa-4">
        <v-spacer />
        <v-btn variant="outlined" @click="loanPayCalculateDialog = false">
          Close
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
