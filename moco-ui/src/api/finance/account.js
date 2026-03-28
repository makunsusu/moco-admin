import request from '@/utils/request'

export function listAccount(query) {
  return request({
    url: '/finance/account/list',
    method: 'get',
    params: query
  })
}

export function accountOptions(query) {
  return request({
    url: '/finance/account/options',
    method: 'get',
    params: query
  })
}

export function getAccount(accountId) {
  return request({
    url: '/finance/account/' + accountId,
    method: 'get'
  })
}

export function addAccount(data) {
  return request({
    url: '/finance/account',
    method: 'post',
    data
  })
}

export function updateAccount(data) {
  return request({
    url: '/finance/account',
    method: 'put',
    data
  })
}

export function delAccount(accountId) {
  return request({
    url: '/finance/account/' + accountId,
    method: 'delete'
  })
}
