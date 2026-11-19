<script setup>
import {computed, onMounted, ref} from 'vue'
import axios from "axios";
import { formatCurrency } from '@/utils/commonUtils'
import {calculateEqualPayment, calculateEqualPrincipal} from '@/utils/repaymentCalculator'

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

const monthlyInterestRate = ref(null) // 연 이율
const repaymentMonths = ref(null)     // 상환 기간

// 계산 결과
const schedule = ref([])
const totalAmount = ref(null)      // 총 납부금액 = 원금 + 이자
const principalAmount = ref(null)  // 원금 상환액
const interestAmount = ref(null)   // 이자 납부액
const monthlyAmount = ref(null)    // 월 납입액

const repaymentTypes = ref([]);
const selectedRepaymentName = ref(null);

const result = ref({});

const headers = [
  { title: '회차', key: 'month', align: 'center' },
  { title: '상환 원금', key: 'principal', align: 'end' },
  { title: '상환 이자', key: 'interest', align: 'end' },
  { title: '총 상환금', key: 'payment', align: 'end' },
  { title: '잔여 원금', key: 'remaining', align: 'end' }
]

// 상환 방식 조회
const getPayOptionsList = async () => {
  try {
    const response = await http.get('/payment/options');
    repaymentTypes.value = response.data;
  } catch {
    /* EMPTY */
  }
};

// 원리금균등상환 계산
const calculateLoan = async () => {
  // 상환 방식
  const repaymentName = selectedRepaymentName.value
  // 상환 원금
  const principal = Number(principalAmount.value)
  // 사용자가 입력 연이율(%)
  const annualRate = Number(monthlyInterestRate.value)
  // 연이율을 월이율로 변환
  const rate = annualRate / 12
  // 상환 개월수
  const months = Number(repaymentMonths.value)

  // 입력값 검증
  if (!repaymentName) {
    alert('상환 방식을 선택해주세요.')
    return
  }
  if (
    !Number.isFinite(principal) ||
    !Number.isFinite(rate) ||
    !Number.isInteger(months) ||
    principal <= 0 ||
    rate < 0 ||
    months <= 0
  ) {
    alert('상환 방식, 대출 원금, 이자율, 상환 기간을 올바르게 입력해주세요.')
    return
  }

  try {
    if (repaymentName === '원리금 균등분할상환') {
      console.log("@@@ Execute 1")
      result.value = calculateEqualPayment(principal, annualRate, months)
    } else if (repaymentName === '원금 균등분할상환') {
      console.log("@@@ Execute 2")
      result.value = calculateEqualPrincipal(principal, annualRate, months)
    } else {
      console.log("@@@ Execute 3")
    }
  } catch (e) {
    /* */
  }

  // 화면 데이터 반영
  totalAmount.value = result.value.totalAmount
  principalAmount.value = principal
  interestAmount.value = result.value.interestAmount
  monthlyAmount.value = result.value.monthlyAmount
  schedule.value = result.value.schedule
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
                  v-model="selectedRepaymentName"
                  label="상환 방식"
                  :items="repaymentTypes"
                  item-title="repaymentName"
                  item-value="repaymentName"
                  placeholder="상환 방식을 선택하세요."
                  variant="outlined"
                  :rules="[v => v !== null || '상환 방식을 입력하세요.']"
                />
              </v-col>

              <!-- 원금 상환액 -->
              <v-col cols="12">
                <v-text-field
                  v-model.number="principalAmount"
                  label="원금 상환액"
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
                  label="연 이율"
                  placeholder="연 이율을 입력하세요."
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
                  {{ formatCurrency(result.totalAmount) }}
                </div>
              </v-card>
            </v-col>

            <v-col cols="12" md="3">
              <v-card variant="tonal" class="pa-3">
                <div class="text-caption">Principal Amount</div>
                <div class="text-h6 font-weight-bold">
                  {{ formatCurrency(principalAmount) }}
                </div>
              </v-card>
            </v-col>

            <v-col cols="12" md="3">
              <v-card variant="tonal" class="pa-3">
                <div class="text-caption">Interest Amount</div>
                <div class="text-h6 font-weight-bold">
                  {{ formatCurrency(result.interestAmount) }}
                </div>
              </v-card>
            </v-col>

            <v-col cols="12" md="3">
              <v-card variant="tonal" class="pa-3">
                <div class="text-caption">Monthly Amount</div>
                <div class="text-h6 font-weight-bold">
                  {{ formatCurrency(result.monthlyAmount) }}
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

            <template #item.remaining="{ item }">
              {{ formatCurrency(item.remaining) }}
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
