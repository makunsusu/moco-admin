import request from '@/utils/request'

export function listFamily(query) {
  return request({
    url: '/finance/family/list',
    method: 'get',
    params: query
  })
}

export function familyOptions() {
  return request({
    url: '/finance/family/options',
    method: 'get'
  })
}

export function getFamily(familyId) {
  return request({
    url: '/finance/family/' + familyId,
    method: 'get'
  })
}

export function addFamily(data) {
  return request({
    url: '/finance/family',
    method: 'post',
    data
  })
}

export function updateFamily(data) {
  return request({
    url: '/finance/family',
    method: 'put',
    data
  })
}

export function delFamily(familyId) {
  return request({
    url: '/finance/family/' + familyId,
    method: 'delete'
  })
}

export function listMember(query) {
  return request({
    url: '/finance/family/member/list',
    method: 'get',
    params: query
  })
}

export function memberOptions(familyId) {
  return request({
    url: '/finance/family/member/options/' + familyId,
    method: 'get'
  })
}

export function addMember(data) {
  return request({
    url: '/finance/family/member',
    method: 'post',
    data
  })
}

export function updateMember(data) {
  return request({
    url: '/finance/family/member',
    method: 'put',
    data
  })
}

export function delMember(memberId) {
  return request({
    url: '/finance/family/member/' + memberId,
    method: 'delete'
  })
}

export function getOverview(query) {
  return request({
    url: '/finance/family/overview',
    method: 'get',
    params: query
  })
}
