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
    "http://localhost:8080/api/statistic/qrcode/user?page=0&size=1000",
    {
      headers: {
        accept: "*/*",
        Authorization:
          "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdHJpbmciLCJhdXRoIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzUwNzAwODkzLCJleHAiOjE3NTA3MDI2OTN9.fW3wZXFMBuQBGQyq5av2oATkTqSapvZsyzBqBbQdTeKvFMDwzo6-_id-4KdWqTYDJ78qVSp4eB-l8v8dX7obAw",
      },
    }
  );

  // 4) 메트릭에 기록
  responseTime.add(res.timings.duration);
  errorRate.add(res.status !== 200);
}