import http from "k6/http";
import { Rate, Trend } from "k6/metrics";

let responseTime = new Trend("response_time");
let errorRate = new Rate("error_rate");

export let options = {
  vus: 1,
  iterations: 10,
};

export default function () {
  const shortId = "EV95SQB1JNVY";
  const authToken =
    "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdHJpbmciLCJhdXRoIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzUwOTMzMjk1LCJleHAiOjE3NTA5MzUwOTV9.5R5w0f18ryXEKlVgdUHI6SuMifNJnrWWn5J-MbAwWhGLZE9WFHBmhOiucJJ_HNUdvi3mHolNllhD2Exzf5KA5w";

  const url = `http://localhost:8080/api/qrcode/${shortId}/guestbook/`;

  const payload = JSON.stringify({
    deviceId: `k6-device-${__VU}-${__ITER}`,
    name: `K6 User ${__ITER}`,
    phoneNumber: `010-0000-${String(__ITER).padStart(4, "0")}`,
  });

  const params = {
    headers: {
      "Content-Type": "application/json",
      Authorization: authToken,
    },
  };

  let res = http.post(url, payload, params);

  console.log(
    `Iteration: ${__ITER + 1}, Status: ${res.status}, Body: ${res.body}`
  );

  responseTime.add(res.timings.duration);
  errorRate.add(res.status !== 200 && res.status !== 429);
}
