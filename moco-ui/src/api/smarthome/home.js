import request from '@/utils/request'

export function listHomes(query) {
  return request({
    url: '/smarthome/home/list',
    method: 'get',
    params: query
  })
}

export function listRooms(query) {
  return request({
    url: '/smarthome/room/list',
    method: 'get',
    params: query
  })
}
