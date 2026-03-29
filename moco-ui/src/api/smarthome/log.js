import request from '@/utils/request'

export function listSyncLogs(query) {
  return request({
    url: '/smarthome/sync-log/list',
    method: 'get',
    params: query
  })
}
