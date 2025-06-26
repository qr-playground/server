import http from "k6/http";
import { Trend, Rate } from "k6/metrics";

let responseTime = new Trend("response_time");
let errorRate = new Rate("error_rate");

export let options = {
  vus: 1,
  iterations: 100,
};

export default function () {
  // 3) GET 요청 실행 (Bearer 토큰 + accept 헤더 포함)
  let res = http.get(
    "http://localhost:8080/api/statistic/qrcode/user/total",
    {
      headers: {
        accept: "*/*",
        Authorization:
          "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdHJpbmciLCJhdXRoIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzUwODU4NjE2LCJleHAiOjE3NTA4NjA0MTZ9.0kp7PnnsImvsJ_pLTffbZ8P5RubEm0FQv4LtrI5nVwzg4UO_7iLJS7Frt_jV48SrdMRvL6rBRygGd409Lc6nGQ",
      },
    }
  );

  // 4) 메트릭에 기록
  responseTime.add(res.timings.duration);
  errorRate.add(res.status !== 200);
}