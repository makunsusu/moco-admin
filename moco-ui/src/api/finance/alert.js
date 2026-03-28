import request from '@/utils/request'

export function listAlertRule(query) {
  return request({
    url: '/finance/alert/rule/list',
    method: 'get',
    params: query
  })
}

export function getAlertRule(ruleId) {
  return request({
    url: '/finance/alert/rule/' + ruleId,
    method: 'get'
  })
}

export function addAlertRule(data) {
  return request({
    url: '/finance/alert/rule',
    method: 'post',
    data
  })
}

export function updateAlertRule(data) {
  return request({
    url: '/finance/alert/rule',
    method: 'put',
    data
  })
}

export function delAlertRule(ruleId) {
  return request({
    url: '/finance/alert/rule/' + ruleId,
    method: 'delete'
  })
}

export function listAlertEvent(query) {
  return request({
    url: '/finance/alert/event/list',
    method: 'get',
    params: query
  })
}

export function updateAlertEventStatus(eventId, status) {
  return request({
    url: '/finance/alert/event/' + eventId + '/status/' + status,
    method: 'put'
  })
}
