<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const loanCalculateDialog = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

// 사용자 입력값
// const loanAmount = ref(320000000)
// const monthlyInterestRate = ref(0.4)
// const repaymentMonths = ref(360)
const loanAmount = ref(null)
const monthlyInterestRate = ref(null)
const repaymentMonths = ref(null)

// 계산 결과
const schedule = ref([])
const monthlyPayment = ref(0)
const totalInterest = ref(0)
const totalPayment = ref(0)

const loanForm = ref(null);

const headers = [
  { title: '회차', key: 'month', align: 'center' },
  { title: '상환 원금', key: 'principal', align: 'end' },
  { title: '이자', key: 'interest', align: 'end' },
  { title: '월 상환금', key: 'payment', align: 'end' },
  { title: '잔여 원금', key: 'balance', align: 'end' }
]

// 원리금균등상환 계산
const calculateLoan = async () => {
  // const { valid } = await loanForm.value.validate()
  // if (!valid) {
  //   return
  // }

  const principal = loanAmount.value
  const rate = monthlyInterestRate.value / 100
  const months = repaymentMonths.value

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

  // 월 상환금 계산
  const payment = rate === 0
    ? principal / months
    : principal * rate * Math.pow(1 + rate, months)
    / (Math.pow(1 + rate, months) - 1)

  monthlyPayment.value = payment

  let remainingBalance = principal
  let interestSum = 0
  let paymentSum = 0

  const result = []

  for (let month = 1; month <= months; month++) {
    const interest = remainingBalance * rate

    // 마지막 회차에는 잔여 원금을 전액 상환
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

  schedule.value = result
  totalInterest.value = interestSum
  totalPayment.value = paymentSum
}

const formatCurrency = value => {
  return Math.round(value).toLocaleString('ko-KR') + '원'
}
</script>

<template>
  <v-dialog v-model="loanCalculateDialog" max-width="900" persistent>
    <v-card>
      <v-card-title class="d-flex justify-space-between align-center">
        <v-btn
          icon="mdi-close"
          variant="text"
          @click="loanCalculateDialog = false"
        />
      </v-card-title>

      <v-divider />
        <!-- 사용자 입력 Form -->
        <h3 class="text-subtitle-1 mb-4">Loan Information</h3>

        <v-card-text class="pa-5">
          <v-form ref="loanForm" @submit.prevent="calculateLoan">
            <v-row>
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
            <v-col cols="12" md="4">
              <v-card variant="tonal" class="pa-3">
                <div class="text-caption">Monthly Payment</div>
                <div class="text-h6 font-weight-bold">
                  {{ formatCurrency(monthlyPayment) }}
                </div>
              </v-card>
            </v-col>

            <v-col cols="12" md="4">
              <v-card variant="tonal" class="pa-3">
                <div class="text-caption">Total Interest</div>
                <div class="text-h6 font-weight-bold">
                  {{ formatCurrency(totalInterest) }}
                </div>
              </v-card>
            </v-col>

            <v-col cols="12" md="4">
              <v-card variant="tonal" class="pa-3">
                <div class="text-caption">Total Payment</div>
                <div class="text-h6 font-weight-bold">
                  {{ formatCurrency(totalPayment) }}
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
        <v-btn variant="outlined" @click="loanCalculateDialog = false">
          Close
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
