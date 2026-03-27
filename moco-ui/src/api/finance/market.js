import request from '@/utils/request'

export function listMarket(query) {
  return request({
    url: '/finance/market/list',
    method: 'get',
    params: query
  })
}

export function getMarket(marketId) {
  return request({
    url: '/finance/market/' + marketId,
    method: 'get'
  })
}

export function addMarket(data) {
  return request({
    url: '/finance/market',
    method: 'post',
    data: data
  })
}

export function updateMarket(data) {
  return request({
    url: '/finance/market',
    method: 'put',
    data: data
  })
}

export function delMarket(marketId) {
  return request({
    url: '/finance/market/' + marketId,
    method: 'delete'
  })
}
