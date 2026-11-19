/**
 * 금액 포맷팅 (천 단위 콤마 + 원)
 * @param {number} value
 * @returns {string}
 */
export const formatCurrency = (value) => {
  return Math.round(value).toLocaleString('ko-KR') + '원'
}
