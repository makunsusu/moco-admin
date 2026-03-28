import request from '@/utils/request'

export function listTransaction(query) {
  return request({
    url: '/finance/transaction/list',
    method: 'get',
    params: query
  })
}

export function getTransaction(transactionId) {
  return request({
    url: '/finance/transaction/' + transactionId,
    method: 'get'
  })
}

export function addTransaction(data) {
  return request({
    url: '/finance/transaction',
    method: 'post',
    data
  })
}

export function updateTransaction(data) {
  return request({
    url: '/finance/transaction',
    method: 'put',
    data
  })
}

export function delTransaction(transactionId) {
  return request({
    url: '/finance/transaction/' + transactionId,
    method: 'delete'
  })
}
