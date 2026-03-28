import request from '@/utils/request'

export function listLatestQuote() {
  return request({
    url: '/finance/quote/latest',
    method: 'get'
  })
}

export function refreshQuote(data) {
  return request({
    url: '/finance/quote/refresh',
    method: 'post',
    data
  })
}
