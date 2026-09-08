import http from 'k6/http';
import { check, fail } from 'k6';
import { Counter, Gauge } from 'k6/metrics';

const QUEUE_SIZE = Number(__ENV.QUEUE_SIZE || '1000');
const VUS = Number(__ENV.VUS || '10');
const BASE_URLS = (__ENV.BASE_URLS || 'http://localhost:8080,http://localhost:8081')
    .split(',')
    .map((url) => url.trim().replace(/\/$/, ''))
    .filter(Boolean);
const TEST_ID = __ENV.TEST_ID || `queue-${Date.now()}`;

const dequeueFailures = new Counter('queue_dequeue_failures');
const queueLengthAfter = new Gauge('queue_length_after');

export const options = {
    scenarios: {
        dequeue: {
            executor: 'shared-iterations',
            vus: VUS,
            iterations: QUEUE_SIZE,
            maxDuration: '2m',
            tags: {
                scenario: 'queue_dequeue',
            },
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<5000'],
        queue_dequeue_failures: ['count==0'],
    },
};

export function setup() {
    const enqueueUrl = `${BASE_URLS[0]}/queue/token/enqueue`;
    const params = {
        headers: { 'Content-Type': 'application/json' },
        tags: { operation: 'enqueue' },
    };
    const userIds = [];

    for (let index = 0; index < QUEUE_SIZE; index++) {
        const userId = `k6-${TEST_ID}-${index}`;
        const response = http.post(
            enqueueUrl,
            JSON.stringify({ userId }),
            params
        );
        const ok = check(response, {
            'enqueue status is 200': (result) => result.status === 200,
        });
        if (!ok) {
            fail(`enqueue failed at index ${index}: status=${response.status}`);
        }
        userIds.push(userId);
    }

    return { userIds };
}

export default function () {
    const instanceIndex = (__VU - 1) % BASE_URLS.length;
    const dequeueUrl = `${BASE_URLS[instanceIndex]}/queue/token/dequeue`;
    const response = http.post(dequeueUrl, null, {
        tags: {
            operation: 'dequeue',
            app_instance: `instance-${instanceIndex + 1}`,
        },
    });
    const ok = check(response, {
        'dequeue status is 200': (result) => result.status === 200,
    });
    if (!ok) {
        dequeueFailures.add(1);
    }
}

export function teardown() {
    const response = http.get(`${BASE_URLS[0]}/queue/token/length`, {
        tags: { operation: 'queue_length' },
    });
    let queueLength = -1;
    if (response.status === 200) {
        try {
            queueLength = Number(response.json('data'));
        } catch (error) {
            queueLength = -1;
        }
    }
    queueLengthAfter.add(queueLength);
    check(response, {
        'queue length endpoint is 200': (result) => result.status === 200,
        'queue is empty after dequeue': () => queueLength === 0,
    });
}
