<script setup>
import {ref, computed, onMounted} from 'vue'
import axios from "axios";

const http = axios.create({
  baseURL: '/api',
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
  },
  paymentId: { type: Number, default: null },
})

const emit = defineEmits(['update:modelValue'])

// 목록 및 선택 데이터
const items = ref([]);
const checkedItems = ref([]);
const loading = ref(false);
const detailInfo = ref({});

const dialog = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

const headers = [
  { title: '납부일', key: 'paymentDate', align: 'center' },
  { title: '총 납부금액', key: 'totalAmount', align: 'end' },
  { title: '원금 상환액', key: 'principalAmount', align: 'end' },
  { title: '이자 납부액', key: 'interestAmount', align: 'end' },
  { title: '잔여 원금', key: 'remainingPrincipal', align: 'end' },
  { title: '납부 방법', key: 'paymentMethod', align: 'center' },
  { title: '관리', key: 'actions', align: 'center', sortable: false }
]

const paymentMethods = [
  '자동이체',
  '계좌이체',
  '기타'
]

const payHistoryList = ref([]);

// Form 상태
const formVisible = ref(false)
const isEdit = ref(false)
const editingPaymentId = ref(null)

// Form 초기값
const initialForm = () => ({
  paymentId: null,            // 항목 번호
  paymentDate: '',            // 납부일
  totalAmount: null,          // 총 납부금액
  principalAmount: null,      // 원금 상환액
  interestAmount: null,       // 이자 납부액
  remainingPrincipal: null,   // 납부 후 잔여원금
  paymentMethod: '자동이체',    // 납부 방법
  memo: '',                   // 비고
  useYn: null                 // 사용 여부
})

const form = ref(initialForm())

// 등록 폼 열기
const openCreateForm = () => {
  isEdit.value = false
  editingPaymentId.value = null
  form.value = initialForm()
  formVisible.value = true

  getPayHistoryList()
}

// 수정 폼 열기
const openEditForm = (item) => {
  isEdit.value = true
  editingPaymentId.value = item.paymentId
  form.value = { ...item }
  formVisible.value = true

  console.log("@@@ item = " + JSON.stringify(item))
  console.log("@@@ editingPaymentId.value = " + editingPaymentId.value)
  console.log("@@@ item.paymentId = " + item.paymentId)
}

// Form 닫기
const closeForm = () => {
  formVisible.value = false
  form.value = initialForm()
}

// 상세 정보 및 이력 조회
const getPayHistoryList = async () => {
  try {
    const response = await http.get('/payment/history');
    payHistoryList.value = response.data;
  } catch {
    /* EMPTY */
  }
};

// 등록 / 수정 저장
const savePayHistory = async (paymentId) => {
  if (
    !form.value.paymentDate ||
    form.value.totalAmount == null ||
    form.value.principalAmount == null ||
    form.value.interestAmount == null
  ) {
    alert('필수 입력 항목을 확인해주세요.')
    return
  }

  if (
    form.value.totalAmount < 0 ||
    form.value.principalAmount < 0 ||
    form.value.interestAmount < 0 ||
    (form.value.remainingPrincipal != null &&
      form.value.remainingPrincipal < 0)
  ) {
    alert('금액은 음수로 입력할 수 없습니다.')
    return
  }

  if (
    Math.round(form.value.totalAmount) !==
    Math.round(form.value.principalAmount + form.value.interestAmount)
  ) {
    alert('총 납부금액은 원금과 이자의 합계와 일치해야 합니다.')
    return
  }

  const requestData = {
    // paymentId : form.value.paymentId,
    paymentDate: form.value.paymentDate,
    totalAmount: form.value.totalAmount,
    principalAmount: form.value.principalAmount,
    interestAmount: form.value.interestAmount,
    remainingPrincipal: form.value.remainingPrincipal,
    paymentMethod: form.value.paymentMethod,
    memo: form.value.memo,
    useYn: 'Y'
  }

  // 기존 수정
  if (isEdit.value) {
    try {
      await http.put(`/payment/${paymentId}`, requestData);
      alert('납부 이력이 수정 되었습니다.')
      await getPayHistoryList()
    } catch (error) {
      console.error('납부 이력 수정 실패:', error)
      alert('납부 이력 수정에 실패했습니다.')
      return
    }

  // 신규 추가
  } else {
    try {
      const response = await http.post('/payment', requestData)
      // 서버가 저장된 데이터를 반환하는 경우 목록 반영
      if (response.data && typeof response.data === 'object') {
        payHistoryList.value.unshift(response.data)
      }
      alert('납부 이력이 등록 되었습니다.')
      await getPayHistoryList()
    } catch (error) {
      console.error('납부 이력 등록 실패:', error)
      alert('납부 이력 등록에 실패했습니다.')
      return
    }
  }

  closeForm()
}

// 삭제
const deletePayHistory = async (paymentId) => {
  const confirmed = window.confirm('해당 납부 내역을 삭제하시겠습니까?')
  if (!confirmed) { return }

  try {
    await http.delete(`/payment/${paymentId}` );
    alert('납부 이력이 삭제 되었습니다.')
    await getPayHistoryList();
  } catch (error) {
    console.error('납부 이력 삭제 실패:', error)
    alert('납부 이력 삭제를 실패했습니다.')
    return
  }
}

const formatCurrency = (value) => {
  return `${Number(value || 0).toLocaleString('ko-KR')}원`
}

onMounted(() => {
  getPayHistoryList();
});
</script>

<template>
  <v-dialog v-model="dialog" max-width="1100" persistent>
    <v-card>
      <v-card-title class="d-flex justify-space-between align-center">
        <span>납부 이력</span>

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

            <template #item.totalAmount="{ item }">
              {{ formatCurrency(item.totalAmount) }}
            </template>

            <template #item.principalAmount="{ item }">
              {{ formatCurrency(item.principalAmount) }}
            </template>

            <template #item.interestAmount="{ item }">
              {{ formatCurrency(item.interestAmount) }}
            </template>

            <template #item.remainingPrincipal="{ item }">
              {{ formatCurrency(item.remainingPrincipal) }}
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
                  @click="deletePayHistory(item.paymentId)"
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
                  v-model.number="form.totalAmount"
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
                  v-model.number="form.remainingPrincipal"
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
