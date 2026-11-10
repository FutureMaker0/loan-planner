<script setup>
import { ref, computed } from 'vue'
import axios from "axios";

const http = axios.create({
  baseURL: import.meta.env.BASE_URL + 'api',
  withCredentials: true, // 모든 요청에 쿠키를 포함하여 전송
  headers: {
    'Content-Type': 'application/json',
  },
});

// 부모와 Dialog 연결
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const dialog = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

const headers = [
  { title: '납부일', key: 'paymentDate', align: 'center' },
  { title: '총 납부금액', key: 'paymentAmount', align: 'end' },
  { title: '원금', key: 'principalAmount', align: 'end' },
  { title: '이자', key: 'interestAmount', align: 'end' },
  { title: '잔여 원금', key: 'remainingBalance', align: 'end' },
  { title: '납부 방법', key: 'paymentMethod', align: 'center' },
  { title: '관리', key: 'actions', align: 'center', sortable: false }
]

const paymentMethods = [
  '자동이체',
  '계좌이체',
  '기타'
]

// (임시) 추후 API 조회 결과로 대체
// const payHistoryList = ref([]);
const payHistoryList = ref([
  {
    paymentId: 1,
    paymentDate: '2026-01-25',
    paymentAmount: 524670,
    principalAmount: 124670,
    interestAmount: 400000,
    remainingBalance: 99875330,
    paymentMethod: '자동이체',
    memo: ''
  },
  {
    paymentId: 2,
    paymentDate: '2026-02-25',
    paymentAmount: 524670,
    principalAmount: 125169,
    interestAmount: 399501,
    remainingBalance: 99750161,
    paymentMethod: '자동이체',
    memo: ''
  }
])

// Form 상태
const formVisible = ref(false)
const isEdit = ref(false)
const editingPaymentId = ref(null)

// Form 초기값
const initialForm = () => ({
  paymentDate: '',
  paymentAmount: null,
  principalAmount: null,
  interestAmount: null,
  remainingBalance: null,
  paymentMethod: '자동이체',
  memo: ''
})

const form = ref(initialForm())

// 등록 폼 열기
const openCreateForm = () => {
  isEdit.value = false
  editingPaymentId.value = null
  form.value = initialForm()
  formVisible.value = true
}

// 수정 폼 열기
const openEditForm = (item) => {
  isEdit.value = true
  editingPaymentId.value = item.paymentId
  form.value = { ...item }
  formVisible.value = true
}

// Form 닫기
const closeForm = () => {
  formVisible.value = false
  form.value = initialForm()
}

// 등록 / 수정 저장
const savePayHistory = async () => {
  if (
    !form.value.paymentDate ||
    form.value.paymentAmount == null ||
    form.value.principalAmount == null ||
    form.value.interestAmount == null
  ) {
    alert('필수 입력 항목을 확인해주세요.')
    return
  }

  if (
    form.value.paymentAmount < 0 ||
    form.value.principalAmount < 0 ||
    form.value.interestAmount < 0 ||
    (form.value.remainingBalance != null &&
      form.value.remainingBalance < 0)
  ) {
    alert('금액은 음수로 입력할 수 없습니다.')
    return
  }

  if (
    Math.round(form.value.paymentAmount) !==
    Math.round(form.value.principalAmount + form.value.interestAmount)
  ) {
    alert('총 납부금액은 원금과 이자의 합계와 일치해야 합니다.')
    return
  }

  // 기존 수정
  if (isEdit.value) {
    const index = payHistoryList.value.findIndex(
      item => item.paymentId === editingPaymentId.value
    )

    // 프론트 임시 처리
    if (index !== -1) {
      payHistoryList.value[index] = {
        ...form.value,
        paymentId: editingPaymentId.value
      }
    }

    // 백단 정규 처리
    // await http.put(`/loan/${formData.value.tmsrsSn}`, payload);
    // await dialogStore.success('수정되었습니다.');

  // 신규 추가
  } else {
    const newPayment = {
      ...form.value,
      paymentId: Date.now()
    }

    // 프론트 임시 처리
    payHistoryList.value.unshift(newPayment)

    // 백단 정규 처리
    await http.post('/payment', form);
    await alert("Created Successfully!!")
  }

  closeForm()
}

// 삭제
const deletePayment = (paymentId) => {
  const confirmed = window.confirm('해당 납부 내역을 삭제하시겠습니까?')

  if (!confirmed) return

  payHistoryList.value = payHistoryList.value.filter(
    item => item.paymentId !== paymentId
  )
}

const formatCurrency = (value) => {
  return `${Number(value || 0).toLocaleString('ko-KR')}원`
}
</script>

<template>
  <v-dialog v-model="dialog" max-width="1100" persistent>
    <v-card>
      <v-card-title class="d-flex justify-space-between align-center">
        <span>Payment History</span>

        <v-btn
          icon="mdi-close"
          variant="text"
          @click="dialog = false"
        />
      </v-card-title>

      <v-divider />

      <v-card-text class="pa-5">

        <!-- 조회 영역 -->
        <template v-if="!formVisible">

          <div class="d-flex justify-space-between align-center mb-4">
            <div>
              <div class="text-subtitle-1 font-weight-bold">
                납부 이력
              </div>
              <div class="text-caption text-medium-emphasis">
                총 {{ payHistoryList.length }}건의 납부 내역
              </div>
            </div>

            <v-btn
              color="primary"
              prepend-icon="mdi-plus"
              @click="openCreateForm"
            >
              추가
            </v-btn>
          </div>

          <!-- 납부 이력 테이블 -->
          <v-data-table
            :headers="headers"
            :items="payHistoryList"
            item-value="paymentId"
            :items-per-page="10"
            density="comfortable"
            no-data-text="등록된 납부 이력이 없습니다."
          >

            <template #item.paymentAmount="{ item }">
              {{ formatCurrency(item.paymentAmount) }}
            </template>

            <template #item.principalAmount="{ item }">
              {{ formatCurrency(item.principalAmount) }}
            </template>

            <template #item.interestAmount="{ item }">
              {{ formatCurrency(item.interestAmount) }}
            </template>

            <template #item.remainingBalance="{ item }">
              {{ formatCurrency(item.remainingBalance) }}
            </template>

            <!-- 수정 / 삭제 버튼 -->
            <template #item.actions="{ item }">
              <div class="d-flex justify-center">
                <v-btn
                  icon="mdi-pencil"
                  size="small"
                  variant="text"
                  color="primary"
                  @click="openEditForm(item)"
                />

                <v-btn
                  icon="mdi-delete"
                  size="small"
                  variant="text"
                  color="error"
                  @click="deletePayment(item.paymentId)"
                />
              </div>
            </template>

          </v-data-table>

        </template>

        <!-- 신규 등록 / 수정 Form -->
        <template v-else>

          <div class="d-flex align-center mb-5">
            <v-btn
              icon="mdi-arrow-left"
              variant="text"
              @click="closeForm"
            />

            <span class="text-subtitle-1 font-weight-bold">
              {{ isEdit ? '납부 내역 수정' : '신규 납부 등록' }}
            </span>
          </div>

          <v-form @submit.prevent="savePayHistory">
            <v-row>

              <!-- 납부일 -->
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="form.paymentDate"
                  label="납부일"
                  type="date"
                  variant="outlined"
                  required
                />
              </v-col>

              <!-- 납부금액 -->
              <v-col cols="12" md="6">
                <v-text-field
                  v-model.number="form.paymentAmount"
                  label="총 납부금액"
                  type="number"
                  suffix="원"
                  variant="outlined"
                  required
                />
              </v-col>

              <!-- 원금 -->
              <v-col cols="12" md="6">
                <v-text-field
                  v-model.number="form.principalAmount"
                  label="원금 상환액"
                  type="number"
                  suffix="원"
                  variant="outlined"
                  required
                />
              </v-col>

              <!-- 이자 -->
              <v-col cols="12" md="6">
                <v-text-field
                  v-model.number="form.interestAmount"
                  label="이자 납부액"
                  type="number"
                  suffix="원"
                  variant="outlined"
                  required
                />
              </v-col>

              <!-- 잔여 원금 -->
              <v-col cols="12" md="6">
                <v-text-field
                  v-model.number="form.remainingBalance"
                  label="납부 후 잔여 원금"
                  type="number"
                  suffix="원"
                  variant="outlined"
                />
              </v-col>

              <!-- 납부 방법 -->
              <v-col cols="12" md="6">
                <v-select
                  v-model="form.paymentMethod"
                  label="납부 방법"
                  :items="paymentMethods"
                  variant="outlined"
                />
              </v-col>

              <!-- 비고 -->
              <v-col cols="12">
                <v-textarea
                  v-model="form.memo"
                  label="비고"
                  rows="2"
                  variant="outlined"
                />
              </v-col>

            </v-row>

            <div class="d-flex justify-end ga-2">
              <v-btn
                variant="outlined"
                @click="closeForm"
              >
                취소
              </v-btn>

              <v-btn
                color="primary"
                type="submit"
              >
                {{ isEdit ? '수정' : '등록' }}
              </v-btn>
            </div>
          </v-form>

        </template>

      </v-card-text>

      <v-divider />

      <v-card-actions class="pa-4">
        <v-spacer />

        <v-btn
          variant="outlined"
          @click="dialog = false"
        >
          닫기
        </v-btn>
      </v-card-actions>

    </v-card>
  </v-dialog>
</template>
