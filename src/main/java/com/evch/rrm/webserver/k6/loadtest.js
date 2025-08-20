import http from "k6/http";
import { check, sleep } from "k6";

export let options = {
  vus: 10, // количество виртуальных пользователей
  duration: "30s", // время теста
};

export default function () {
  let res = http.get("http://localhost:8080/api/time");

  check(res, {
    "status is 200": (r) => r.status === 200,
  });

  sleep(1);
}
// k6 run loadtest.js
