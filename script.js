/**
 * PharmaConnect — dashboard UI wired to Spring Boot API
 */

let currentUser = null;
let currentRole = "client";
let currentPanel = "dashboard";
let cachedOrdonnances = [];
let cachedMedicaments = [];
let cachedCommandes = [];
let cachedNotifications = [];
let cachedUsers = [];
let dashboardStats = null;

function $(sel) {
  return document.querySelector(sel);
}

function $all(sel) {
  return document.querySelectorAll(sel);
}

function formatTnd(amount) {
  return `${Number(amount).toFixed(2)} TND`;
}

function escapeHtml(str) {
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

function showToast(message) {
  const el = $("#appToast");
  const body = $("#appToastBody");
  if (!el || !body) return;
  body.textContent = message;
  bootstrap.Toast.getOrCreateInstance(el, { delay: 3200 }).show();
}

function showLoading(show) {
  const el = $("#globalLoading");
  if (el) el.classList.toggle("d-none", !show);
}

function statusBadgeClass(status) {
  const s = String(status).toLowerCase();
  if (s === "en attente") return "badge-status-pending";
  if (s === "validee" || s === "pret") return s === "pret" ? "badge-status-ready" : "badge-status-approved";
  if (s === "livre") return "badge-status-approved";
  if (s === "refusee") return "bg-secondary";
  return "bg-secondary";
}

function isStaff() {
  return currentUser && (currentUser.role === "PHARMACIEN" || currentUser.role === "ADMIN");
}

function isAdmin() {
  return currentUser && currentUser.role === "ADMIN";
}

function isPatient() {
  return currentUser && currentUser.role === "PATIENT";
}

function requireAuth() {
  currentUser = PharmaAPI.getSession();
  if (!currentUser) {
    window.location.href = "index.html";
    return false;
  }
  return true;
}

function getDashboardRole() {
  return isStaff() ? "owner" : "client";
}

function renderTopbarUserInfo() {
  const nameEl = $("#topbarUserName");
  const roleTextEl = $("#topbarUserRoleText");
  const badgeEl = $("#topbarUserRoleBadge");
  if (!currentUser) return;
  const displayName = currentUser.nom || currentUser.name || "Utilisateur";
  const displayRole = isAdmin() ? "Administrateur" : isStaff() ? "Pharmacien" : "Patient";
  if (nameEl) nameEl.textContent = displayName;
  if (roleTextEl) roleTextEl.textContent = displayRole;
  if (badgeEl) {
    badgeEl.textContent = displayRole;
    badgeEl.className = `badge badge-role ${isAdmin() ? "badge-role-admin" : isStaff() ? "badge-role-pharmacien" : "badge-role-patient"}`;
  }
}

function applyRoleVisibility() {
  currentRole = getDashboardRole();
  updateRoleViews();
  updateAdminControls();
  const welcome = $("#clientWelcome");
  if (welcome && currentUser) {
    welcome.textContent = `Welcome back, ${currentUser.nom}`;
  }
  const hint = $("#sidebarRoleHint");
  if (hint && currentUser) {
    const displayRole = isStaff() ? (isAdmin() ? "Administrateur" : "Pharmacien") : "Patient";
    hint.textContent = `${currentUser.nom} (${displayRole})`;
  }
  renderTopbarUserInfo();
}

async function refreshPatientData() {
  const pid = currentUser.id;
  showLoading(true);
  try {
    const [ordonnances, medicaments, commandes, notifications] = await Promise.all([
      PharmaAPI.getPatientOrdonnances(pid),
      PharmaAPI.getPatientMedicaments(),
      PharmaAPI.getPatientCommandes(pid),
      PharmaAPI.getNotifications(pid),
    ]);
    cachedOrdonnances = ordonnances;
    cachedMedicaments = medicaments;
    cachedCommandes = commandes;
    cachedNotifications = notifications;
  } finally {
    showLoading(false);
  }
}

async function refreshPharmacyData() {
  showLoading(true);
  try {
    const [medicaments, ordonnances, commandes, stats] = await Promise.all([
      PharmaAPI.getPharmacyMedicaments(),
      PharmaAPI.getAllOrdonnances(),
      PharmaAPI.getPharmacyCommandes(),
      PharmaAPI.getPharmacyStats(),
    ]);
    cachedMedicaments = medicaments;
    cachedOrdonnances = ordonnances;
    cachedCommandes = commandes;
    dashboardStats = stats;
  } finally {
    showLoading(false);
  }
}

async function refreshAdminData() {
  if (!isAdmin()) {
    cachedUsers = [];
    return;
  }
  showLoading(true);
  try {
    cachedUsers = await PharmaAPI.getAllUsers();
  } catch (e) {
    showToast(e.message);
  } finally {
    showLoading(false);
  }
}

async function refreshData() {
  if (isAdmin()) {
    await refreshPharmacyData();
    await refreshAdminData();
  } else if (currentRole === "owner" && isStaff()) {
    await refreshPharmacyData();
  } else {
    await refreshPatientData();
  }
  renderAll();
}

function renderAll() {
  renderClientRecentRx();
  renderClientOrders();
  renderMedicinesTable();
  renderPrescriptionsTable();
  renderOwnerRecentRx();
  renderOrdersPanel();
  updateStats();
  updateNotificationBanner();
}

function renderClientRecentRx() {
  const tbody = $("#clientRecentRxBody");
  if (!tbody) return;
  const rows = cachedOrdonnances.slice(0, 3);
  tbody.innerHTML = rows
    .map(
      (o) => `
    <tr>
      <td class="fw-medium">${escapeHtml(o.medicineName || "-")}</td>
      <td><span class="badge rounded-pill ${statusBadgeClass(o.statut)}">${escapeHtml(o.statut)}</span></td>
      <td class="text-end text-muted small">${escapeHtml(o.dateEmission || "-")}</td>
    </tr>`
    )
    .join("");
}

function getClientOrdersDisplay() {
  if (cachedCommandes.length > 0) return cachedCommandes;
  return cachedOrdonnances.map((o) => ({
    code: `PC-TN-${1000 + o.id}`,
    items: o.items || o.medicineName,
    total: o.total,
    date: o.dateEmission,
    status: o.statut,
  }));
}

function renderClientOrders() {
  const tbody = $("#clientOrderHistoryBody");
  if (!tbody) return;
  tbody.innerHTML = getClientOrdersDisplay()
    .map(
      (o) => `
    <tr>
      <td class="fw-medium">${escapeHtml(o.code || o.id)}</td>
      <td class="text-muted small">${escapeHtml(o.items || "-")}</td>
      <td class="text-end fw-semibold">${escapeHtml(formatTnd(o.total))}</td>
    </tr>`
    )
    .join("");
}

function renderMedicinesTable() {
  const tbody = $("#medicinesTableBody");
  if (!tbody) return;
  tbody.innerHTML = cachedMedicaments
    .map(
      (m) => `
    <tr>
      <td class="fw-medium">${escapeHtml(m.nom)}</td>
      <td class="text-end">${m.quantiteDisponible ?? 0}</td>
      <td class="text-end">${formatTnd(m.prixUnitaire)}</td>
    </tr>`
    )
    .join("");
}

function renderPrescriptionsTable() {
  const tbody = $("#prescriptionsTableBody");
  if (!tbody) return;
  const list =
    currentRole === "client" && isPatient()
      ? cachedOrdonnances
      : cachedOrdonnances;
  tbody.innerHTML = list
    .map(
      (o) => `
    <tr>
      <td>${escapeHtml(o.patientName || currentUser?.nom || "-")}</td>
      <td>${escapeHtml(o.medicineName || "-")}</td>
      <td><span class="badge rounded-pill ${statusBadgeClass(o.statut)}">${escapeHtml(o.statut)}</span></td>
    </tr>`
    )
    .join("");
}

function renderOwnerRecentRx() {
  const tbody = $("#ownerRecentRxBody");
  if (!tbody) return;
  const prescribers = ["Dr. Ben Ali", "Dr. Mansour", "Dr. Selma"];
  tbody.innerHTML = cachedOrdonnances
    .map(
      (o, i) => `
    <tr>
      <td class="fw-medium">${escapeHtml(o.patientName || "-")}</td>
      <td>${escapeHtml(o.medicineName || "-")}</td>
      <td><span class="badge rounded-pill ${statusBadgeClass(o.statut)}">${escapeHtml(o.statut)}</span></td>
      <td class="text-end text-muted small">${escapeHtml(prescribers[i] || "-")}</td>
    </tr>`
    )
    .join("");
}

function renderOrdersPanel() {
  const tbody = $("#ordersTableBody");
  if (!tbody) return;
  if (currentRole === "client" && isPatient()) {
    tbody.innerHTML = getClientOrdersDisplay()
      .map(
        (o) => `
      <tr>
        <td class="fw-medium">${escapeHtml(o.code || o.id)}</td>
        <td>${escapeHtml(o.date || "-")}</td>
        <td><span class="badge bg-light text-dark border">${escapeHtml(o.status)}</span></td>
        <td class="text-end">${escapeHtml(formatTnd(o.total))}</td>
      </tr>`
      )
      .join("");
  } else {
    tbody.innerHTML = cachedCommandes
      .map(
        (o) => `
      <tr>
        <td class="fw-medium">${escapeHtml(o.code || o.id)}</td>
        <td>${escapeHtml(o.date || "-")}</td>
        <td><span class="badge rounded-pill ${statusBadgeClass(o.status)}">${escapeHtml(o.status)}</span></td>
        <td class="text-end">${escapeHtml(formatTnd(o.total))}</td>
      </tr>`
      )
      .join("");
  }
}

function renderUsersTable() {
  const tbody = $("#usersTableBody");
  if (!tbody) return;
  tbody.innerHTML = cachedUsers
    .map(
      (user) => `
      <tr>
        <td class="fw-medium">${escapeHtml(user.nom || "-")}</td>
        <td>${escapeHtml(user.email || "-")}</td>
        <td>${escapeHtml(user.role || "-")}</td>
        <td>${escapeHtml(user.telephone || "-")}</td>
        <td>${escapeHtml(user.adresse || "-")}</td>
        <td class="text-end">
          <button type="button" class="btn btn-sm btn-outline-pharma me-2" data-action="edit-user" data-id="${user.id}">Modifier</button>
          <button type="button" class="btn btn-sm btn-outline-danger" data-action="delete-user" data-id="${user.id}">Supprimer</button>
        </td>
      </tr>`
    )
    .join("");
}

function updateStats() {
  if (dashboardStats && currentRole === "owner") {
    $("#statTotalMedicines") && ($("#statTotalMedicines").textContent = String(dashboardStats.totalMedicines));
    $("#statLowStock") && ($("#statLowStock").textContent = String(dashboardStats.lowStock));
    $("#statPendingRx") && ($("#statPendingRx").textContent = String(dashboardStats.pendingRx));
    $("#statTodayOrders") && ($("#statTodayOrders").textContent = String(dashboardStats.todayOrders));
  } else if (isPatient()) {
    $("#statTotalMedicines") && ($("#statTotalMedicines").textContent = String(cachedMedicaments.length));
    const low = cachedMedicaments.filter((m) => (m.quantiteDisponible ?? 0) < 10).length;
    $("#statLowStock") && ($("#statLowStock").textContent = String(low));
    const pending = cachedOrdonnances.filter((o) => o.statut === "En attente").length;
    $("#statPendingRx") && ($("#statPendingRx").textContent = String(pending));
    $("#statTodayOrders") && ($("#statTodayOrders").textContent = String(cachedCommandes.length));
  }
}

function updateNotificationBanner() {
  const unread = cachedNotifications.filter((n) => !n.isRead);
  const banner = document.querySelector(".notification-banner");
  if (banner && unread.length > 0) {
    const p = banner.querySelector("p");
    if (p) p.textContent = unread[0].message;
  }
}

function fillRequestMedicinesSelect() {
  const sel = $("#selectRequestMedicines");
  if (!sel) return;
  const available = cachedMedicaments.filter((m) => (m.quantiteDisponible ?? 0) > 0);
  sel.innerHTML = available
    .map((m) => `<option value="${m.id}">${escapeHtml(m.nom)} (${formatTnd(m.prixUnitaire)})</option>`)
    .join("");
}

async function fillApproveSelect() {
  const sel = $("#selectPendingRx");
  if (!sel) return;
  showLoading(true);
  try {
    const pending = await PharmaAPI.getPendingOrdonnances();
    sel.innerHTML =
      pending.length === 0
        ? '<option value="">Aucune ordonnance en attente</option>'
        : pending
            .map(
              (o) =>
                `<option value="${o.id}">${escapeHtml(o.patientName)} - ${escapeHtml(o.medicineName)}</option>`
            )
            .join("");
  } catch (e) {
    showToast(e.message);
  } finally {
    showLoading(false);
  }
}

function fillOrderStatusSelect() {
  const sel = $("#selectOrderToUpdate");
  if (!sel) return;
  sel.innerHTML = cachedCommandes
    .map((o) => `<option value="${o.id}">${escapeHtml(o.code)} - ${escapeHtml(o.status)}</option>`)
    .join("");
}

function updateRoleButtons() {
  const btnClient = $("#btnRoleClient");
  const btnOwner = $("#btnRoleOwner");
  if (!btnClient || !btnOwner) return;
  const active = "btn-role-active";
  const inactive = "btn-role-inactive";
  if (currentRole === "client") {
    btnClient.classList.add(active);
    btnClient.classList.remove(inactive);
    btnOwner.classList.add(inactive);
    btnOwner.classList.remove(active);
  } else {
    btnOwner.classList.add(active);
    btnOwner.classList.remove(inactive);
    btnClient.classList.add(inactive);
    btnClient.classList.remove(active);
  }
}

function updateRoleViews() {
  const clientDash = $("#view-client-dashboard");
  const ownerDash = $("#view-owner-dashboard");
  if (clientDash && ownerDash) {
    if (currentRole === "client") {
      clientDash.classList.remove("d-none");
      ownerDash.classList.add("d-none");
    } else {
      ownerDash.classList.remove("d-none");
      clientDash.classList.add("d-none");
    }
  }

  $all(".owner-only").forEach((el) => {
    if (currentRole === "owner" && isStaff()) el.classList.remove("d-none");
    else el.classList.add("d-none");
  });

  const medTitle = $("#medicinesPanelTitle");
  if (medTitle) {
    medTitle.innerHTML =
      currentRole === "client"
        ? '<i class="fa-solid fa-pills text-pharma me-2"></i>Catalogue et prix'
        : '<i class="fa-solid fa-pills text-pharma me-2"></i>Stock pharmacie';
  }
  const rxTitle = $("#rxPanelTitle");
  if (rxTitle) {
    rxTitle.innerHTML =
      currentRole === "client"
        ? '<i class="fa-solid fa-file-prescription text-pharma me-2"></i>Mes ordonnances'
        : '<i class="fa-solid fa-file-prescription text-pharma me-2"></i>Toutes les ordonnances';
  }
  const ordTitle = $("#ordersPanelTitle");
  if (ordTitle) {
    ordTitle.innerHTML =
      currentRole === "client"
        ? '<i class="fa-solid fa-box text-pharma me-2"></i>Mes commandes'
        : '<i class="fa-solid fa-box text-pharma me-2"></i>Commandes pharmacie';
  }
}

async function setRole(role) {
  if (role !== "client" && role !== "owner") return;
  if (role === "owner" && !isStaff()) return;
  currentRole = role;
  updateRoleButtons();
  updateRoleViews();
  await refreshData();
  if (currentPanel === "orders") renderOrdersPanel();
}

function showPanel(panel) {
  currentPanel = panel;
  $all(".panel-section").forEach((sec) => sec.classList.add("d-none"));
  const map = {
    dashboard: "#panel-dashboard",
    medicines: "#panel-medicines",
    prescriptions: "#panel-prescriptions",
    orders: "#panel-orders",
    users: "#panel-users",
  };
  const el = $(map[panel]);
  if (el) el.classList.remove("d-none");

  $all(".sidebar .nav-link").forEach((a) => {
    a.classList.toggle("active", a.getAttribute("data-panel") === panel);
  });

  const titles = {
    dashboard: "Dashboard",
    medicines: "Medicaments",
    prescriptions: "Ordonnances",
    orders: "Commandes",
  };
  $("#pageTitle") && ($("#pageTitle").textContent = titles[panel] || "Dashboard");
  $("#breadcrumbText") && ($("#breadcrumbText").textContent = panel === "dashboard" ? "Overview" : "Workspace");

  if (panel === "medicines") renderMedicinesTable();
  if (panel === "prescriptions") renderPrescriptionsTable();
  if (panel === "orders") renderOrdersPanel();
  if (panel === "users") renderUsersTable();
}

function wireSidebar() {
  $all(".sidebar .nav-link").forEach((link) => {
    link.addEventListener("click", (e) => {
      e.preventDefault();
      const panel = link.getAttribute("data-panel");
      if (panel) showPanel(panel);
      closeSidebarMobile();
    });
  });
}

function closeSidebarMobile() {
  $("#sidebar")?.classList.remove("show");
  $("#sidebarBackdrop")?.classList.remove("show");
}

function wireMobileSidebar() {
  $("#sidebarToggle")?.addEventListener("click", () => {
    $("#sidebar")?.classList.toggle("show");
    $("#sidebarBackdrop")?.classList.toggle("show");
  });
  $("#sidebarBackdrop")?.addEventListener("click", closeSidebarMobile);
}

function updateAdminControls() {
  $all(".admin-only").forEach((el) => {
    if (isAdmin()) {
      el.classList.remove("d-none");
    } else {
      el.classList.add("d-none");
    }
  });
}

function clearManageUserForm() {
  $("#manageUserError")?.classList.add("d-none");
  $("#inputUserName").value = "";
  $("#inputUserEmail").value = "";
  $("#inputUserPassword").value = "";
  const roleSelect = $("#selectUserRole");
  if (roleSelect) {
    roleSelect.value = "PATIENT";
    roleSelect.disabled = false;
  }
  $("#inputUserTelephone").value = "";
  $("#inputUserAdresse").value = "";
  $("#inputUserDiplome").value = "";
  $("#inputUserAccess").value = "1";
  $("#modalManageUser").dataset.mode = "create";
  $("#modalManageUser").dataset.userId = "";
  updateManageUserFields();
}

function updateManageUserFields() {
  const role = $("#selectUserRole")?.value;
  const diplomeGroup = $("#userDiplomeGroup");
  const accessGroup = $("#userAccessGroup");
  if (diplomeGroup) {
    diplomeGroup.classList.toggle("d-none", role !== "PHARMACIEN");
  }
  if (accessGroup) {
    accessGroup.classList.toggle("d-none", role !== "ADMIN");
  }
}

function fillManageUserForm(user) {
  if (!user) return;
  $("#inputUserName").value = user.nom || "";
  $("#inputUserEmail").value = user.email || "";
  $("#inputUserPassword").value = "";
  const roleSelect = $("#selectUserRole");
  if (roleSelect) {
    roleSelect.value = user.role || "PATIENT";
    roleSelect.disabled = true;
  }
  $("#inputUserTelephone").value = user.telephone || "";
  $("#inputUserAdresse").value = user.adresse || "";
  $("#inputUserDiplome").value = user.diplome || "";
  $("#inputUserAccess").value = user.niveauAcces ? String(user.niveauAcces) : "1";
  $("#modalManageUser").dataset.mode = "edit";
  $("#modalManageUser").dataset.userId = String(user.id);
  updateManageUserFields();
}

function showUserModalForEdit(userId) {
  const user = cachedUsers.find((u) => u.id === Number(userId));
  if (!user) return;
  fillManageUserForm(user);
  const modalLabel = $("#modalManageUserLabel");
  if (modalLabel) modalLabel.textContent = `Modifier ${user.nom}`;
  bootstrap.Modal.getOrCreateInstance($("#modalManageUser")).show();
}

function showUserModalForCreate() {
  clearManageUserForm();
  const modalLabel = $("#modalManageUserLabel");
  if (modalLabel) modalLabel.textContent = "Ajouter utilisateur";
  bootstrap.Modal.getOrCreateInstance($("#modalManageUser")).show();
}

async function saveManageUser() {
  const mode = $("#modalManageUser").dataset.mode || "create";
  const userId = Number($("#modalManageUser").dataset.userId || 0);
  const payload = {
    nom: $("#inputUserName").value.trim(),
    email: $("#inputUserEmail").value.trim().toLowerCase(),
    role: $("#selectUserRole").value,
    password: $("#inputUserPassword").value,
    telephone: $("#inputUserTelephone").value.trim(),
    adresse: $("#inputUserAdresse").value.trim(),
    diplome: $("#inputUserDiplome").value.trim(),
    niveauAcces: Number($("#inputUserAccess").value) || 1,
  };
  if (!payload.nom || !payload.email || !payload.role) {
    showManageUserError("Nom, email et role sont obligatoires.");
    return;
  }
  if (mode === "create" && !payload.password) {
    showManageUserError("Veuillez renseigner un mot de passe pour le nouvel utilisateur.");
    return;
  }
  $("#manageUserError")?.classList.add("d-none");
  showLoading(true);
  try {
    if (mode === "edit") {
      await PharmaAPI.updateUser(userId, payload);
      showToast("Utilisateur mis a jour.");
    } else {
      await PharmaAPI.createUser(payload);
      showToast("Utilisateur ajoute.");
    }
    await refreshAdminData();
    renderUsersTable();
    bootstrap.Modal.getInstance($("#modalManageUser"))?.hide();
  } catch (err) {
    showManageUserError(err.message);
  } finally {
    showLoading(false);
  }
}

function showManageUserError(message) {
  const el = $("#manageUserError");
  if (!el) return;
  el.textContent = message;
  el.classList.remove("d-none");
}

async function removeUser(userId) {
  if (!confirm("Voulez-vous vraiment supprimer cet utilisateur ?")) {
    return;
  }
  showLoading(true);
  try {
    await PharmaAPI.deleteUser(userId);
    showToast("Utilisateur supprime.");
    await refreshAdminData();
    renderUsersTable();
  } catch (err) {
    showToast(err.message);
  } finally {
    showLoading(false);
  }
}

function wireUserManagement() {
  $("#btnAddUser")?.addEventListener("click", () => {
    clearManageUserForm();
    const modalLabel = $("#modalManageUserLabel");
    if (modalLabel) modalLabel.textContent = "Ajouter utilisateur";
    updateManageUserFields();
  });

  $("#selectUserRole")?.addEventListener("change", updateManageUserFields);
  $("#btnSaveUser")?.addEventListener("click", async () => await saveManageUser());

  $("#usersTableBody")?.addEventListener("click", (event) => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;
    const action = button.getAttribute("data-action");
    const id = Number(button.getAttribute("data-id"));
    if (action === "edit-user") {
      showUserModalForEdit(id);
    }
    if (action === "delete-user") {
      removeUser(id);
    }
  });
}

function wireLogout() {
  $("#btnLogout")?.addEventListener("click", () => {
    sessionStorage.clear();
    localStorage.clear();
    PharmaAPI.clearSession();
    window.location.replace("index.html");
  });
}

async function createPrescriptionFromRequest() {
  const selectedMedicineIds = Array.from($("#selectRequestMedicines")?.selectedOptions || []).map((opt) =>
    Number(opt.value)
  );
  if (selectedMedicineIds.length === 0) {
    showToast("Selectionnez au moins un medicament disponible.");
    return;
  }
  const file = $("#inputOrdonnancePhoto")?.files?.[0];
  const notes = $("#inputRequestNotes")?.value?.trim() || "";
  const lignes = selectedMedicineIds.map((medId) => ({
    medicamentId: medId,
    quantite: 1,
    posologie: notes || "A confirmer",
    duree: 7,
  }));

  showLoading(true);
  try {
    await PharmaAPI.createOrdonnance({
      patientId: currentUser.id,
      lignes,
      signatureNumerique: file ? `upload:${file.name}` : "sans_photo",
      notes,
    });
    await refreshData();
    showToast(`Ordonnance envoyee (${lignes.length} medicament(s)).`);
    $("#inputRequestNotes") && ($("#inputRequestNotes").value = "");
  } catch (e) {
    showToast(e.message);
  } finally {
    showLoading(false);
  }
}

function wireModals() {
  $("#modalRequestRx")?.addEventListener("show.bs.modal", fillRequestMedicinesSelect);
  $("#modalApproveRx")?.addEventListener("show.bs.modal", fillApproveSelect);
  $("#modalUpdateOrderStatus")?.addEventListener("show.bs.modal", fillOrderStatusSelect);

  $("#btnSubmitRequestRx")?.addEventListener("click", async (e) => {
    e.preventDefault();
    await createPrescriptionFromRequest();
    bootstrap.Modal.getInstance($("#modalRequestRx"))?.hide();
  });

  $("#btnSaveMedicine")?.addEventListener("click", async () => {
    const name = $("#inputNewMedName")?.value?.trim() || "Nouveau medicament";
    const stock = parseInt($("#inputNewMedStock")?.value, 10) || 0;
    const price = parseFloat($("#inputNewMedPrice")?.value) || 0;
    showLoading(true);
    try {
      await PharmaAPI.addPharmacyMedicament({
        nom: name,
        categorie: "Divers",
        prixUnitaire: price,
        stock,
      });
      await refreshData();
      showToast(`"${name}" ajoute au stock (${formatTnd(price)}).`);
      if ($("#inputNewMedName")) $("#inputNewMedName").value = "";
    } catch (err) {
      showToast(err.message);
    } finally {
      showLoading(false);
    }
  });

  $("#btnConfirmApprove")?.addEventListener("click", async () => {
    const selectedId = Number($("#selectPendingRx")?.value || 0);
    if (!selectedId) {
      showToast("Aucune ordonnance selectionnee.");
      return;
    }
    showLoading(true);
    try {
      await PharmaAPI.approveOrdonnance(selectedId);
      await refreshData();
      showToast(`Ordonnance #${selectedId} validee. Stock mis a jour.`);
      bootstrap.Modal.getInstance($("#modalApproveRx"))?.hide();
    } catch (err) {
      showToast(err.message);
    } finally {
      showLoading(false);
    }
  });

  $("#btnSaveOrderStatus")?.addEventListener("click", async () => {
    const orderId = Number($("#selectOrderToUpdate")?.value);
    const newStatus = $("#selectOrderStatusValue")?.value;
    if (!orderId || !newStatus) {
      showToast("Impossible de modifier le statut.");
      return;
    }
    showLoading(true);
    try {
      await PharmaAPI.updateCommandeStatus(orderId, newStatus);
      await refreshData();
      fillOrderStatusSelect();
      showToast(`Commande mise a jour -> ${newStatus}.`);
      bootstrap.Modal.getInstance($("#modalUpdateOrderStatus"))?.hide();
    } catch (err) {
      showToast(err.message);
    } finally {
      showLoading(false);
    }
  });
}

async function init() {
  if (!requireAuth()) return;
  applyRoleVisibility();
  wireSidebar();
  wireMobileSidebar();
  wireLogout();
  wireUserManagement();
  wireModals();
  updateRoleButtons();
  updateRoleViews();
  try {
    await refreshData();
    showPanel("dashboard");
  } catch (e) {
    console.error(e);
    showToast("API indisponible. Demarrez Spring Boot sur le port 8080.");
  }
}

document.addEventListener("DOMContentLoaded", init);
