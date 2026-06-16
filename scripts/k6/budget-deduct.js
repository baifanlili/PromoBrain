import http from 'k6/http'
import { check } from 'k6'

export const options = {
  vus: 100,
  duration: '30s',
}

export default function () {
  const payload = JSON.stringify({
    requestId: `click_${__VU}_${__ITER}`,
    campaignId: 1001,
    creativeId: 5001,
    cost: 0.8,
  })

  const res = http.post('http://localhost:8080/api/budget/deduct', payload, {
    headers: { 'Content-Type': 'application/json' },
  })

  check(res, {
    'status is 200': (r) => r.status === 200,
  })
}

