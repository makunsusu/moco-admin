import request from '@/utils/request'

export function listAsset(query) {
  return request({
    url: '/finance/asset/list',
    method: 'get',
    params: query
  })
}

export function assetOptions() {
  return request({
    url: '/finance/asset/options',
    method: 'get'
  })
}

export function getAsset(assetId) {
  return request({
    url: '/finance/asset/' + assetId,
    method: 'get'
  })
}

export function getAssetDetail(assetId, query) {
  return request({
    url: '/finance/asset/detail/' + assetId,
    method: 'get',
    params: query
  })
}

export function addAsset(data) {
  return request({
    url: '/finance/asset',
    method: 'post',
    data
  })
}

export function updateAsset(data) {
  return request({
    url: '/finance/asset',
    method: 'put',
    data
  })
}

export function delAsset(assetId) {
  return request({
    url: '/finance/asset/' + assetId,
    method: 'delete'
  })
}
