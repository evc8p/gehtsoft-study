import http from 'k6/http';
import { check } from 'k6';

export let options = {
    stages: [
        { duration: '10s', target: 100 },   // плавный разгон до 10 VUs
        { duration: '20s', target: 800 },   // до 50 VUs
        { duration: '20s', target: 15000 },  // до 100 VUs
        { duration: '10s', target: 0 },    // спуск
    ],
    thresholds: {
        'http_req_failed': ['rate<0.05'], // не более 5% ошибок
        'http_req_duration': ['p(95)<1000'], // 95% запросов < 1s
    }
};

export default function () {
    const res1 = http.get('http://localhost:8080/api/time', {
        headers: {
            'User-Agent': 'Mozilla/5.0',
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
            'Host': 'localhost:8080'
        }
    });

    const res2 = http.get('http://localhost:8081/api/time', {
        headers: {
            'User-Agent': 'Mozilla/5.0',
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
            'Host': 'localhost:8081'
        }
    });

    check(res1, { '8080 status 200': (r) => r.status === 200 });
    check(res2, { '8081 status 200': (r) => r.status === 200 });
}
// k6 run loadtest1.js
