async function loadTime() {
  const res = await fetch('/api/time');
  const data = await res.json();
  document.getElementById('timeOutput').textContent = JSON.stringify(data, null, 2);
}

async function loadStats() {
  const res = await fetch('/api/stats');
  const data = await res.json();
  document.getElementById('statsOutput').textContent = JSON.stringify(data, null, 2);
}

document.getElementById('echoForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const body = { message: document.getElementById('echoInput').value };
  const res = await fetch('/api/echo', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  });
  const data = await res.json();
  document.getElementById('echoOutput').textContent = JSON.stringify(data, null, 2);
});
