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
  optionId: { type: Number, default: null },
})

const dialog = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

const emit = defineEmits(['update:modelValue'])

const headers = [
  { title: '순번', key: 'optionId', align: 'center' },
  { title: '상환 방식명', key: 'optionType', align: 'center' },
  { title: '비고', key: 'description', align: 'center' },
  { title: '관리', key: 'actions', align: 'center', sortable: false }
]

const payOptionsList = ref([]);

// Form 상태
const formVisible = ref(false)
const isEdit = ref(false)
const editingOptionId = ref(null)

// Form 초기값
const initialForm = () => ({
  optionId: null,             // 항목 번호
  optionType: null,           // 상환 방식명
  description: null,          // 비고
  useYn: null                 // 사용 여부
})

const form = ref(initialForm())

// 등록 폼 열기
const openCreateForm = () => {
  isEdit.value = false
  editingOptionId.value = null
  form.value = initialForm()
  formVisible.value = true

  getPayOptionsList()
}

// 수정 폼 열기
const openEditForm = (item) => {
  isEdit.value = true
  editingOptionId.value = item.optionId
  form.value = { ...item }
  formVisible.value = true
}

// Form 닫기
const closeForm = () => {
  formVisible.value = false
  form.value = initialForm()
}

// 상세 정보 및 이력 조회
const getPayOptionsList = async () => {
  try {
    const response = await http.get('/payment/options');
    payOptionsList.value = response.data;
  } catch {
    /* EMPTY */
  }
};

// 등록 / 수정 저장
const savePayOption = async (optionId) => {
  if (
    !form.value.optionType ||
    form.value.description == null
  ) {
    alert('필수 입력 항목을 확인해주세요.')
    return
  }

  const requestData = {
    optionType : form.value.optionType,
    description: form.value.description,
    useYn: 'Y'
  }

  if (isEdit.value) { // 기존 수정
    try {
      await http.put(`/payment/options/${editingOptionId.value}`, requestData);
      alert('상환 방식이 수정 되었습니다.')
      await getPayOptionsList()
    } catch (error) {
      console.error('납부 이력 수정 실패:', error)
      alert('상환 방식 수정에 실패했습니다.')
      return
    }
  } else { // 신규 추가
    try {
      const response = await http.post('/payment/options', requestData)
      // 서버가 저장된 데이터를 반환하는 경우 목록 반영
      if (response.data && typeof response.data === 'object') {
        payOptionsList.value.unshift(response.data)
      }
      alert('상환 방식이 등록 되었습니다.')
      await getPayOptionsList()
    } catch (error) {
      console.error('상환 방식 등록 실패:', error)
      alert('상환 방식 등록에 실패했습니다.')
      return
    }
  }

  closeForm()
}

// 삭제
const deletePayOption = async (optionId) => {
  const confirmed = window.confirm('해당 상환 방식을 삭제하시겠습니까?')
  if (!confirmed) { return }

  try {
    await http.delete(`/payment/options/${optionId}` );
    alert('상환 방식이 삭제 되었습니다.')
    await getPayOptionsList();
  } catch (error) {
    console.error('상환 방식 삭제 실패:', error)
    alert('상환 방식 삭제를 실패했습니다.')
    return
  }
}

onMounted(() => {
  getPayOptionsList();
});
</script>

<template>
  <v-dialog v-model="dialog" max-width="1100" persistent>
    <v-card>
      <v-card-title class="d-flex justify-space-between align-center">
        <span>상환 종류</span>
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
                총 {{ payOptionsList.length }}건의 납부 내역
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

          <!-- 상환 방식 종류 테이블 -->
          <v-data-table
            :headers="headers"
            :items="payOptionsList"
            item-value="optionId"
            :items-per-page="10"
            density="comfortable"
            no-data-text="등록된 상환 방식이 없습니다."
          >
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
                  @click="deletePayOption(item.optionId)"
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
              {{ isEdit ? '내역 수정' : '신규 등록' }}
            </span>
          </div>

          <v-form @submit.prevent="savePayOption">
            <v-row>
              <!-- 상환 방식 -->
              <v-col cols="12">
                <v-textarea
                  v-model="form.optionType"
                  label="상환 방식"
                  variant="outlined"
                />
              </v-col>

              <!-- 비고 -->
              <v-col cols="12">
                <v-textarea
                  v-model="form.description"
                  label="비고"
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
