const API_BASE = "https://kpotpay-production.up.railway.app";
let token = localStorage.getItem("token");
let currentShifts = [];
let editingId = null;
let authMode = "login";

function money(n){ return Number(n ?? 0).toFixed(2); }
function moneyClass(n){ return Number(n) < 0 ? "neg" : "pos"; }
function $(id){ return document.getElementById(id); }

// every request to a protected endpoint goes through here so the token is always attached
async function api(path, options = {}){
  const headers = { ...(options.headers || {}) };
  if(token) headers["Authorization"] = `Bearer ${token}`;
  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });
  if(res.status === 401 || res.status === 403){
    logout("Session expired. Please log in again.");
    throw new Error("Not authorized");
  }
  return res;
}

function showApp(show){
  $("authView").style.display = show ? "none" : "block";
  $("appView").style.display = show ? "block" : "none";
}

function toggleAuthMode(){
  authMode = authMode === "login" ? "register" : "login";
  const reg = authMode === "register";
  $("authTitle").textContent = reg ? "Create account" : "Log in";
  $("authSubmit").textContent = reg ? "Register" : "Log in";
  $("authToggle").textContent = reg ? "Have an account? Log in" : "Need an account? Register";
  $("nameRow").style.display = reg ? "block" : "none";
  $("authStatus").textContent = "";
}

async function submitAuth(){
  const email = $("a-email").value, password = $("a-password").value, name = $("a-name").value;
  const statusEl = $("authStatus");
  statusEl.className = "status";
  statusEl.textContent = "Working...";
  const post = (path, body) => fetch(`${API_BASE}${path}`, {
    method: "POST", headers: {"Content-Type": "application/json"}, body: JSON.stringify(body)
  });
  try{
    if(authMode === "register"){
      const r = await post("/auth/register", { name, email, password });
      if(!r.ok) throw new Error("Registration failed. Check your details; the email may already be registered.");
    }
    const res = await post("/auth/login", { email, password });
    if(!res.ok) throw new Error("Invalid email or password.");
    token = (await res.text()).trim();   // login returns the raw token string, not JSON
    localStorage.setItem("token", token);
    $("a-password").value = "";
    statusEl.textContent = "";
    init();
  }catch(err){
    statusEl.textContent = err.message.includes("fetch") ? "Can't reach the server." : err.message;
    statusEl.className = "status error";
  }
}

function logout(message){
  token = null;
  localStorage.removeItem("token");
  currentShifts = [];
  resetForm();
  $("shiftsBody").innerHTML = "";
  $("summaryResult").innerHTML = "";
  showApp(false);
  const s = $("authStatus");
  s.textContent = typeof message === "string" ? message : "";
  s.className = "status error";
}

async function init(){
  if(!token){ showApp(false); return; }
  try{
    const res = await api("/me");
    if(!res.ok) throw new Error("Could not load your account.");
    const me = await res.json();
    $("whoami").textContent = `Signed in as ${me.name} (${me.email})`;
    showApp(true);
    loadShifts();
  }catch(err){
    showApp(false);
  }
}

async function loadShifts(){
  const statusEl = $("listStatus");
  try{
    const res = await api("/shifts");
    if(!res.ok) throw new Error("Could not load shifts.");
    currentShifts = await res.json();
    renderShifts();
    statusEl.textContent = "";
  }catch(err){
    statusEl.textContent = err.message;
    statusEl.className = "status error";
  }
}

function renderShifts(){
  const body = $("shiftsBody");
  body.innerHTML = "";
  $("shiftsEmpty").style.display = currentShifts.length ? "none" : "block";
  currentShifts.forEach(s => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${s.date}</td>
      <td>${s.role}</td>
      <td>${money(s.foodSales)}</td>
      <td>${money(s.barSales)}</td>
      <td>${money(s.tips)}</td>
      <td class="money neg">-${money(s.totalTipOut)}</td>
      <td class="money ${moneyClass(s.netTips)}">${money(s.netTips)}</td>
      <td class="actions">
        <button class="link" onclick="editShift(${s.id})">Edit</button>
        &nbsp;
        <button class="link danger" onclick="deleteShift(${s.id})">Delete</button>
      </td>`;
    body.appendChild(tr);
  });
}

function editShift(id){
  const s = currentShifts.find(x => x.id === id);
  if(!s) return;
  editingId = id;
  $("formTitle").textContent = `Editing shift #${id}`;
  $("f-date").value = s.date;
  $("f-role").value = s.role;
  $("f-hours").value = s.hoursWorked;
  $("f-food").value = s.foodSales;
  $("f-bar").value = s.barSales;
  $("f-tips").value = s.tips;
  $("submitBtn").textContent = "Save changes";
  $("cancelBtn").style.display = "inline-block";
  window.scrollTo({top:0, behavior:"smooth"});
}

function resetForm(){
  editingId = null;
  $("formTitle").textContent = "Log a shift";
  ["f-date","f-hours","f-food","f-bar","f-tips"].forEach(id => $(id).value = "");
  $("f-role").value = "SERVER";
  $("submitBtn").textContent = "Save shift";
  $("cancelBtn").style.display = "none";
}

async function submitShift(){
  const statusEl = $("formStatus");
  const body = {
    date: $("f-date").value || null,
    role: $("f-role").value,
    hoursWorked: $("f-hours").value || null,
    foodSales: $("f-food").value || null,
    barSales: $("f-bar").value || null,
    tips: $("f-tips").value || null,
  };

  // quick checks before sending; the backend still validates everything
  const missing = [];
  if(!body.date) missing.push("date");
  if(!body.hoursWorked) missing.push("hours worked");
  if(missing.length){
    statusEl.textContent = `Please fill in: ${missing.join(" and ")}.`;
    statusEl.className = "status error";
    return;
  }
  const amounts = [body.hoursWorked, body.foodSales, body.barSales, body.tips];
  if(amounts.some(v => v !== null && Number(v) < 0)){
    statusEl.textContent = "Amounts can't be negative.";
    statusEl.className = "status error";
    return;
  }

  try{
    const res = await api(editingId ? `/shifts/${editingId}` : "/shifts", {
      method: editingId ? "PATCH" : "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify(body)
    });
    if(!res.ok) throw new Error(res.status === 404 ? "Shift not found." : "Save failed. Check every field is valid.");
    statusEl.textContent = editingId ? "Shift updated." : "Shift saved.";
    statusEl.className = "status ok";
    resetForm();
    loadShifts();
  }catch(err){
    statusEl.textContent = err.message;
    statusEl.className = "status error";
  }
}

async function deleteShift(id){
  if(!confirm("Delete this shift?")) return;
  try{
    const res = await api(`/shifts/${id}`, { method: "DELETE" });
    if(!res.ok) throw new Error("Delete failed.");
    loadShifts();
  }catch(err){
    alert(err.message);
  }
}

async function getSummary(){
  const start = $("s-start").value, end = $("s-end").value;
  const statusEl = $("summaryStatus"), resultEl = $("summaryResult");
  if(!start || !end){ statusEl.textContent = "Pick both dates."; statusEl.className = "status error"; return; }
  statusEl.textContent = "Calculating...";
  statusEl.className = "status";
  try{
    const res = await api(`/shifts/summary?startDate=${start}&endDate=${end}`);
    if(!res.ok) throw new Error("Request failed.");
    const d = await res.json();
    statusEl.textContent = "";
    resultEl.innerHTML = `
      <div class="ticket">
        <h3>${d.startDate} → ${d.endDate}</h3>
        <div class="line"><span>Food sales</span><span>${money(d.totalFoodSales)}</span></div>
        <div class="line"><span>Bar sales</span><span>${money(d.totalBarSales)}</span></div>
        <div class="line"><span>Tips earned</span><span>${money(d.totalTips)}</span></div>
        <div class="line money neg"><span>Food tip-out</span><span>-${money(d.totalFoodTipOut)}</span></div>
        <div class="line money neg"><span>Bar tip-out</span><span>-${money(d.totalBarTipOut)}</span></div>
        <div class="line total ${moneyClass(d.totalNetTips)}"><span>You keep</span><span>${money(d.totalNetTips)}</span></div>
        <p class="sub" style="margin-top:1rem;">${d.shifts.length} shift(s) in this range.</p>
      </div>`;
  }catch(err){
    statusEl.textContent = err.message;
    statusEl.className = "status error";
  }
}

resetForm();
init();