/**
 * Load testing
 *
 * @link https://k6.io/docs/es/tipos-de-prueba/load-testing
 * @link https://k6.io/docs/using-k6/k6-options/reference
 */

import http from 'k6/http';
import { check, group, sleep } from 'k6';

//export const options = {
//  stages: [
//    { duration: '5m', target: 60 }, // simulate ramp-up of traffic from 1 to 60 users over 5 minutes.
//    { duration: '10m', target: 60 }, // stay at 60 users for 10 minutes
//    { duration: '3m', target: 100 }, // ramp-up to 100 users over 3 minutes (peak hour starts)
//    { duration: '2m', target: 100 }, // stay at 100 users for short amount of time (peak hour)
//    { duration: '3m', target: 60 }, // ramp-down to 60 users over 3 minutes (peak hour ends)
//    { duration: '10m', target: 60 }, // continue at 60 for additional 10 minutes
//    { duration: '5m', target: 0 }, // ramp-down to 0 users
//  ],
//  thresholds: {
//    http_req_duration: ['p(99)<1500'], // 99% of requests must complete below 1.5s
//  },
//};

export const options = {
    vus: 1,
    duration: '30m',
    iterations: 1000,
};

const BASE_URL = `http://${__ENV.HOST}:${__ENV.PORT}`; // make sure this is not production

export default function () {
  group('GET List Tasks', function() {
    let res = http.get(`${BASE_URL}/api/v1/tasks?categoryId=${__ENV.CATEGORY_ID}`);

    check(res, {
      'is status 200': r => r.status === 200,
    });

    sleep(1);
  });
}
