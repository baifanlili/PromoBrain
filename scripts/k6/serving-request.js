import http from 'k6/http'
import { check } from 'k6'

export const options = {
  vus: 100,
  duration: '30s',
}

export default function () {
  const payload = JSON.stringify({
    requestId: `req_${__VU}_${__ITER}`,
    keyword: '冰丝防晒衣',
    userTags: ['female', 'commute', 'summer'],
    placement: 'SEARCH_TOP',
    topK: 3,
  })

  const res = http.post('http://localhost:8080/api/serving/request', payload, {
    headers: { 'Content-Type': 'application/json' },
  })

  check(res, {
    'status is 200': (r) => r.status === 200,
  })
}

