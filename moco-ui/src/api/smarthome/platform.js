import request from '@/utils/request'

export function getPlatformAccount() {
  return request({
    url: '/smarthome/platform/account',
    method: 'get'
  })
}

export function updatePlatformAccount(data) {
  return request({
    url: '/smarthome/platform/account',
    method: 'put',
    data: data
  })
}

export function startPlatformQrLogin() {
  return request({
    url: '/smarthome/platform/qr/start',
    method: 'post'
  })
}

export function checkPlatformQrLogin(sessionId) {
  return request({
    url: '/smarthome/platform/qr/status/' + sessionId,
    method: 'get'
  })
}

export function testPlatformConnection() {
  return request({
    url: '/smarthome/platform/test',
    method: 'post'
  })
}

export function syncPlatformFull() {
  return request({
    url: '/smarthome/platform/sync/full',
    method: 'post'
  })
}

export function syncPlatformStatus() {
  return request({
    url: '/smarthome/platform/sync/status',
    method: 'post'
  })
}
