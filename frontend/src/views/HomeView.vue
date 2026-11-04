<script setup>
import { ref, computed } from 'vue';
import axios from 'axios';
import '@/assets/css/loan.css';
import '@/assets/css/rate.css';
import LoanCalculate from '../views/components/LoanCalculate.vue'
import LoanPayHistory from './components/LoanPayHistory.vue'

const loanCalculateDialog = ref(false);
const loanPayHistoryDialog = ref(false);

const serviceKey = ref('fdb134fabfbf0fdd5a9a839eb75331107ecdc863a50fff257287991601ea9cfa');
const resultType = ref('json');
const likeFinPrdNm = ref('디딤돌');

const loading = ref(false);

const loanData = ref(null);
const rateData = ref(null);
const errorMessage = ref('');

// 이율 데이터
const pageNo = ref('1');
const numOfRows = ref('10');
const dateType = ref('JSON');

const loanItems = computed(() => {
const items = loanData.value?.response?.body?.items?.item;

if (!items) return [];

// API가 단일 객체를 반환하는 경우도 처리
return Array.isArray(items) ? items : [items];
});

const totalCount = computed(() => {
return loanData.value?.response?.body?.totalCount ?? 0;
});

const searchLoanProducts = async () => {
  loading.value = true;
  errorMessage.value = '';
  loanData.value = null;
  rateData.value = null;

  try {
    const response = await axios.get('https://apis.data.go.kr/1160100/service/GetSmallLoanFinanceInstituteInfoService/getOrdinaryFinanceInfo', {
    params: {
      serviceKey: serviceKey.value || undefined,
      resultType: resultType.value || undefined,
      likeFinPrdNm: likeFinPrdNm.value || undefined,
    },
  });

  loanData.value = response.data;
  console.log('대출상품 API 응답:', response.data);

} catch (error) {
  console.error('대출상품 조회 실패:', error);

  errorMessage.value =
  error.response?.data?.message ||
  error.message ||
  '대출상품 API 호출 중 오류가 발생했습니다.';

  } finally {
    loading.value = false;
  }
};

const searchLoanRate = async () => {
  loading.value = true;
  errorMessage.value = '';
  rateData.value = null;
  loanData.value = null;

  try {
    const response = await axios.get('https://apis.data.go.kr/B551408/didimdol-loan-rate/didimdol-info', {
      params: {
        serviceKey: serviceKey.value || undefined,
        pageNo: pageNo.value || undefined,
        numOfRows: numOfRows.value || undefined,
        dateType: dateType.value || undefined,
      },
      responseType: 'text',
    });

    rateData.value = response.data;
    console.log('대출이율 API 응답:', response.data);

  } catch (error) {
    console.error('대출이율 조회 실패:', error);

    errorMessage.value =
      error.response?.data?.message ||
      error.message ||
      '대출이율 API 호출 중 오류가 발생했습니다.';

  } finally {
    loading.value = false;
  }
};

const formatXml = (xml) => {
  if (!xml) return '';

  const parser = new DOMParser();
  const xmlDoc = parser.parseFromString(xml, 'text/xml');

  // XML 파싱 오류 확인
  if (xmlDoc.querySelector('parsererror')) {
    return xml;
  }

  const formatNode = (node, depth = 0) => {
    const indent = '  '.repeat(depth);

    // 텍스트 노드
    if (node.nodeType === Node.TEXT_NODE) {
      return node.textContent.trim();
    }

    // 요소 노드
    if (node.nodeType === Node.ELEMENT_NODE) {
      const children = [...node.childNodes].filter((child) => {
        return (
          child.nodeType === Node.ELEMENT_NODE ||
          (child.nodeType === Node.TEXT_NODE && child.textContent.trim())
        );
      });

      // 자식 노드가 없는 경우
      if (children.length === 0) {
        return `${indent}<${node.nodeName}></${node.nodeName}>`;
      }

      // 텍스트만 있는 경우
      if (
        children.length === 1 &&
        children[0].nodeType === Node.TEXT_NODE
      ) {
        return `${indent}<${node.nodeName}>${children[0].textContent.trim()}</${node.nodeName}>`;
      }

      // 자식 요소가 있는 경우
      const childContent = children
        .map((child) => formatNode(child, depth + 1))
        .join('\n');

      return `${indent}<${node.nodeName}>\n${childContent}\n${indent}</${node.nodeName}>`;
    }

    return '';
  };

  return formatNode(xmlDoc.documentElement);
};
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <h2 class="text-h5 mb-4">API 연동 테스트</h2>

        <!-- 검색 영역 -->
        <v-card class="pa-4 mb-4" variant="outlined">
          <v-row align="center">
<!--            <v-col cols="12" md="4">-->
<!--              <v-text-field-->
<!--                v-model="keyword"-->
<!--                label="대출상품명"-->
<!--                placeholder="예: 디딤돌"-->
<!--                variant="outlined"-->
<!--                density="compact"-->
<!--                hide-details-->
<!--                clearable-->
<!--                @keyup.enter="searchLoanProducts"-->
<!--              />-->
<!--            </v-col>-->

            <v-col cols="12" md="2">
              <v-btn
                color="primary"
                block
                :loading="loading"
                @click="searchLoanProducts"
              >
                상품조회
              </v-btn>
              <br>
              <v-btn
                color="primary"
                block
                :loading="loading"
                @click="searchLoanRate"
              >
                이율조회
              </v-btn>
              <br>
              <v-btn
                color="primary"
                block
                :loading="loading"
                @click="loanCalculateDialog = true"
              >
                계산
              </v-btn>
              <LoanCalculate v-model="loanCalculateDialog"/>
              <br>
              <v-btn
                color="primary"
                block
                :loading="loading"
                @click="loanPayHistoryDialog = true"
              >
                납부
              </v-btn>
              <LoanPayHistory v-model="loanPayHistoryDialog"/>
            </v-col>
          </v-row>
        </v-card>

        <!-- 결과 요약 -->
        <v-card
          v-if="loanData"
          class="pa-4 mb-4"
          variant="outlined"
        >
          <v-row>
            <v-col cols="6" md="3">
              <div class="text-caption text-grey">응답 코드</div>
              <div class="text-h6">
                {{ loanData.response?.header?.resultCode }}
              </div>
            </v-col>

            <v-col cols="6" md="3">
              <div class="text-caption text-grey">응답 메시지</div>
              <div class="text-h6">
                {{ loanData.response?.header?.resultMsg }}
              </div>
            </v-col>

            <v-col cols="6" md="3">
              <div class="text-caption text-grey">전체 데이터</div>
              <div class="text-h6">
                {{ totalCount }}
              </div>
            </v-col>
          </v-row>
        </v-card>

        <!-- 에러 메시지 -->
        <v-alert
          v-if="errorMessage"
          type="error"
          variant="tonal"
          class="mb-4"
        >
          {{ errorMessage }}
        </v-alert>

        <!-- 상품 목록 -->
        <v-card
          v-if="loanItems.length > 0"
          variant="outlined"
        >
          <v-card-title>
            조회 결과
            <v-chip class="ml-2" color="primary">
              {{ loanItems.length }}건
            </v-chip>
          </v-card-title>

          <v-divider />

          <br><br>

          <v-table>
            <thead>
            <tr>
              <th>상품명</th>
              <th>취급기관</th>
              <th>금리</th>
              <th>대출한도</th>
              <th>대출기간</th>
            </tr>
            </thead>

            <tbody>
            <tr
              v-for="(item, index) in loanItems"
              :key="`${item.basYm}-${item.snq}-${index}`"
            >
              <td>{{ item.finPrdNm }}</td>
              <td>{{ item.ofrInstNm }}</td>
              <td>{{ item.irt }}</td>
              <td>{{ item.lnLmt }}</td>
              <td>{{ item.maxTotLnTrm }}</td>
            </tr>
            </tbody>
          </v-table>
        </v-card>

        <v-alert
          v-else-if="loanData && !loading"
          type="info"
          variant="tonal"
        >
          조회된 대출상품이 없습니다.
        </v-alert>

        <!-- 원본 JSON 응답 -->
        <v-card
          v-if="loanData"
          class="mt-4"
          variant="outlined"
        >
          <br><br>
          <v-card-title>
            API 원본 JSON 응답
          </v-card-title>

          <v-divider />

          <v-card-text>
            <pre class="json-view">{{ JSON.stringify(loanData, null, 2) }}</pre>
          </v-card-text>
        </v-card>

        <!-- 이율 데이터 -->
        <v-card
          v-if="rateData"
          class="pa-4 mb-4"
          variant="outlined"
        >
          <br><br>
          <v-card-title>
            대출이율 API 원본 응답
          </v-card-title>

          <v-divider class="mb-4" />

          <v-card-text>
            <pre class="xml-view">{{ formatXml(rateData) }}</pre>
          </v-card-text>
        </v-card>

        <!-- 에러 메시지 -->
        <v-alert
          v-if="errorMessage"
          type="error"
          variant="tonal"
          class="mb-4"
        >
          {{ errorMessage }}
        </v-alert>

      </v-col>
    </v-row>
  </v-container>
</template>
