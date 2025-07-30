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
  // 500명의 사용자(VU)가 각 1회씩 동시 실행 → 총 500개 동시 요청
  scenarios: {
    concurrentLoad: {
      executor: "per-vu-iterations",
      vus: 500,
      iterations: 1,
      maxDuration: "1m",
    },
  },
};

export default function () {
  // !! 중요: shortId는 실제 존재하는 QR 이벤트의 ID여야 합니다.
  const shortId = "PQ97PBKSZ9ZT";
  const authToken = __ENV.AUTH_TOKEN || "YOUR_FALLBACK_TOKEN_HERE";

  const url = `http://localhost:8080/api/qrcode/${shortId}/guestbook/`;

  // 절대 중복 없는 전화번호 생성 (길이 제한 없음)
  // 타임스탬프 + VU번호 조합으로 100% 고유성 보장
  const timestamp = Date.now(); // 밀리초 타임스탬프
  const phoneNumber = `10999${timestamp}${__VU}`;

  const payload = JSON.stringify({
    deviceId: `k6-device-${timestamp}-${__VU}`,
    name: `K6 User ${__VU}`,
    phoneNumber: phoneNumber,
  });

  const params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  let res = http.post(url, payload, params);

  const isSuccess = check(res, {
    "guestbook status is 200": (r) => r.status === 200,
  });

  responseTime.add(res.timings.duration);
  errorRate.add(!isSuccess);

  // // ----- 추가: guestbook 완료 후 search API 호출 -----
  // const searchKeyword = uniqueNumberCount; // 검색할 키워드 (예: "98989898")
  // const searchUrl = `http://localhost:8080/api/qrcode/event/search?keyword=${encodeURIComponent(
  //   searchKeyword
  // )}&page=0&size=20`;

  // const searchRes = http.get(searchUrl);

  // const isSearchSuccess = check(searchRes, {
  //   "search status is 200": (r) => r.status === 200,
  // });

  // responseTime.add(searchRes.timings.duration);
  // errorRate.add(!isSearchSuccess);
  // // --------------------------------------------------

  // sleep(1); // 쿠폰 시스템에서는 연속적으로 빠르게 요청을 보내야 하므로 sleep 제거
}

export function handleSummary(data) {
  return {
    stdout: textSummary(data, { indent: " ", enableColors: true }),
    "results.html": htmlReport(data),
  };
}
