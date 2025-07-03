import { check } from "k6";
import http from "k6/http";
import { Rate, Trend } from "k6/metrics";

// HTML 리포트 생성용 라이브러리
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
// 콘솔 텍스트 요약도 함께 보고 싶다면
import { textSummary } from "https://jslib.k6.io/k6-summary/0.0.1/index.js";

let responseTime = new Trend("response_time");
let errorRate = new Rate("error_rate");

export let options = {
  thresholds: {
    // 테스트 성공/실패 기준을 정의합니다.
    http_req_failed: ["rate<0.01"], // HTTP 에러율이 1% 미만이어야 함
    http_req_duration: ["p(95)<500"], // 95%의 요청이 500ms 안에 처리되어야 함
    response_time: ["p(95)<500"],
  },
  scenarios: {
    rampTps: {
      executor: "ramping-arrival-rate",
      startRate: 10,
      timeUnit: "1s",
      stages: [
        { target: 50, duration: "1m" },
        { target: 100, duration: "1m" },
        { target: 0, duration: "1m" },
      ],
      preAllocatedVUs: 120,
      maxVUs: 200,
    },
  },
};

export default function () {
  // !! 중요: shortId는 실제 존재하는 QR 이벤트의 ID여야 합니다.
  const shortId = "8AA61AF3VK5W";
  const authToken = __ENV.AUTH_TOKEN || "YOUR_FALLBACK_TOKEN_HERE";

  const url = `http://localhost:8080/api/qrcode/${shortId}/guestbook/`;

  // 시나리오별로 다른 전화번호 생성 (하이픈 없이)
  // 각 시나리오마다 완전히 다른 번호 범위 사용
  let stageNumber = 1;
  if (__VU <= 100) {
    stageNumber = 1; // Stage 1
  } else if (__VU <= 200) {
    stageNumber = 2; // Stage 2
  } else {
    stageNumber = 3; // Stage 3
  }

  // 절대 중복 없는 전화번호: 010 + 시나리오(1자리) + VU*10000+ITER (7자리)
  const uniqueNumber = __VU * 10000 + __ITER;
  const phoneNumber = `011${stageNumber}${String(uniqueNumber).padStart(
    7,
    "0"
  )}`;

  const payload = JSON.stringify({
    deviceId: `k6-device-${stageNumber}-${__VU}-${__ITER}`,
    name: `K6 User ${stageNumber}-${__VU}-${__ITER}`,
    phoneNumber: phoneNumber,
  });

  const params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  let res = http.post(url, payload, params);

  const isSuccess = check(res, {
    "status is 200": (r) => r.status === 200,
  });

  responseTime.add(res.timings.duration);
  errorRate.add(!isSuccess);
  // sleep(1); // 쿠폰 시스템에서는 연속적으로 빠르게 요청을 보내야 하므로 sleep 제거
}

export function handleSummary(data) {
  return {
    stdout: textSummary(data, { indent: " ", enableColors: true }),
    "results.html": htmlReport(data),
  };
}
