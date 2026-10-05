const API_BASE = "http://localhost:8080";
let currentShifts = [];
let editingId = null;

function money(value) {
  return Number(value ?? 0).toFixed(2);
}

function moneyClass(value) {
  return Number(value) < 0 ? "neg" : "pos";
}

function getElement(id) {
  return document.getElementById(id);
}

function setStatus(element, message, type) {
  element.textContent = message;
  element.className = type ? `status ${type}` : "status";
}

function getShiftPayload() {
  return {
    date: getElement("f-date").value || null,
    role: getElement("f-role").value,
    hoursWorked: getElement("f-hours").value || null,
    foodSales: getElement("f-food").value || null,
    barSales: getElement("f-bar").value || null,
    tips: getElement("f-tips").value || null,
  };
}

async function loadShifts() {
  const userId = getElement("userId").value;
  const statusEl = getElement("userStatus");

  setStatus(statusEl, "Loading...", "");

  try {
    const response = await fetch(`${API_BASE}/users/${userId}/shifts`);

    if (!response.ok) {
      throw new Error(response.status === 404 ? "No user with that ID" : "Request failed");
    }

    currentShifts = await response.json();
    renderShifts();
    setStatus(statusEl, `Loaded ${currentShifts.length} shift(s).`, "ok");
  } catch (error) {
    setStatus(statusEl, `${error.message} — is the backend running?`, "error");
  }
}

function renderShifts() {
  const body = getElement("shiftsBody");
  const empty = getElement("shiftsEmpty");

  body.innerHTML = "";
  empty.classList.toggle("hidden", currentShifts.length > 0);

  currentShifts.forEach((shift) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${shift.date}</td>
      <td>${shift.role}</td>
      <td>${money(shift.foodSales)}</td>
      <td>${money(shift.barSales)}</td>
      <td>${money(shift.tips)}</td>
      <td class="money neg">-${money(shift.totalTipOut)}</td>
      <td class="money ${moneyClass(shift.netTips)}">${money(shift.netTips)}</td>
      <td class="actions">
        <button class="link" onclick="editShift(${shift.id})">Edit</button>
        &nbsp;
        <button class="link danger" onclick="deleteShift(${shift.id})">Delete</button>
      </td>
    `;
    body.appendChild(row);
  });
}

function editShift(id) {
  const shift = currentShifts.find((item) => item.id === id);

  if (!shift) {
    return;
  }

  editingId = id;
  const shiftDate = new Date(`${shift.date}T00:00:00`);
  const formattedDate = shiftDate.toLocaleDateString("en-US", {
  weekday: "short",
  month: "short",
  day: "numeric",
  year: "numeric",
});
  getElement("formTitle").textContent = `Editing shift #${id} where you worked on ${formattedDate}`;
  getElement("f-date").value = shift.date;
  getElement("f-role").value = shift.role;
  getElement("f-hours").value = shift.hoursWorked;
  getElement("f-food").value = shift.foodSales;
  getElement("f-bar").value = shift.barSales;
  getElement("f-tips").value = shift.tips;
  getElement("submitBtn").textContent = "Save changes";
  getElement("cancelBtn").classList.remove("hidden");
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function resetForm() {
  editingId = null;
  getElement("formTitle").textContent = "Log a shift";

  ["f-date", "f-hours", "f-food", "f-bar", "f-tips"].forEach((id) => {
    getElement(id).value = "";
  });

  getElement("f-role").value = "SERVER";
  getElement("submitBtn").textContent = "Save shift";
  getElement("cancelBtn").classList.add("hidden");
}

async function submitShift() {
  const userId = getElement("userId").value;
  const statusEl = getElement("formStatus");
  const payload = getShiftPayload();

  try {
    let response;

    if (editingId) {
      response = await fetch(`${API_BASE}/shifts/${editingId}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
    } else {
      response = await fetch(`${API_BASE}/users/${userId}/shifts`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
    }

    if (!response.ok) {
      throw new Error("Save failed — check every field is valid.");
    }

    setStatus(statusEl, editingId ? "Shift updated." : "Shift saved.", "ok");
    resetForm();
    loadShifts();
  } catch (error) {
    setStatus(statusEl, error.message, "error");
  }
}

async function deleteShift(id) {
  if (!confirm("Delete this shift?")) {
    return;
  }

  try {
    const response = await fetch(`${API_BASE}/shifts/${id}`, { method: "DELETE" });

    if (!response.ok) {
      throw new Error("Delete failed.");
    }

    loadShifts();
  } catch (error) {
    alert(error.message);
  }
}

async function getSummary() {
  const userId = getElement("userId").value;
  const start = getElement("s-start").value;
  const end = getElement("s-end").value;
  const statusEl = getElement("summaryStatus");
  const resultEl = getElement("summaryResult");

  if (!start || !end) {
    setStatus(statusEl, "Pick both dates.", "error");
    return;
  }

  setStatus(statusEl, "Calculating...", "");

  try {
    const response = await fetch(
      `${API_BASE}/users/${userId}/shifts/summary?startDate=${start}&endDate=${end}`
    );

    if (!response.ok) {
      throw new Error("Request failed.");
    }

    const data = await response.json();
    setStatus(statusEl, "", "");

    resultEl.innerHTML = `
      <div class="ticket">
        <h3>${data.startDate} → ${data.endDate}</h3>
        <div class="line"><span>Food sales</span><span>${money(data.TotalFoodSales)}</span></div>
        <div class="line"><span>Bar sales</span><span>${money(data.TotalBarSales)}</span></div>
        <div class="line"><span>Tips earned</span><span>${money(data.TotalTips)}</span></div>
        <div class="line money neg"><span>Food tip-out</span><span>-${money(data.TotalFoodTipOut)}</span></div>
        <div class="line money neg"><span>Bar tip-out</span><span>-${money(data.TotalBarTipOut)}</span></div>
        <div class="line total ${moneyClass(data.TotalNetTips)}">
          <span>You keep</span>
          <span>${money(data.TotalNetTips)}</span>
        </div>
        <p class="sub" style="margin-top:1rem;">${data.shifts.length} shift(s) in this range.</p>
      </div>
    `;
  } catch (error) {
    setStatus(statusEl, error.message, "error");
  }
}

resetForm();
