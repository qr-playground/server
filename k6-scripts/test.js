import http from "k6/http";
import { check, sleep } from "k6";
import { Counter } from "k6/metrics";

export let errorRate = new Counter("errors");

export default function () {
  // 1분(60초) 기다렸다가
  sleep(60);

  // 그 후에 헬스체크 API 호출
  let res = http.get("http://app:8080/actuator/health");
  check(res, { "status was 200": (r) => r.status === 200 }) || errorRate.add(1);

  // 이후는 필요한 대로 반복 딜레이
  sleep(1);
}
