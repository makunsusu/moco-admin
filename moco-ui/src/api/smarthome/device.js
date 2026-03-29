import request from '@/utils/request'

export function listDevices(query) {
  return request({
    url: '/smarthome/device/list',
    method: 'get',
    params: query
  })
}

export function getDevice(deviceId) {
  return request({
    url: '/smarthome/device/' + deviceId,
    method: 'get'
  })
}

export function controlDevicePower(deviceId, action) {
  return request({
    url: '/smarthome/device/' + deviceId + '/power/' + action,
    method: 'post'
  })
}
