/**
 * PharmaConnect — REST API client (Spring Boot @ localhost:8080)
 */
const API_BASE = "http://localhost:8080/api";

const PharmaAPI = {
  getSession() {
    try {
      const raw = sessionStorage.getItem("pharmaUser");
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  },

  setSession(user) {
    sessionStorage.setItem("pharmaUser", JSON.stringify(user));
  },

  clearSession() {
    sessionStorage.removeItem("pharmaUser");
    localStorage.removeItem("pharmaUser");
  },

  authHeaders(extra = {}) {
    const user = this.getSession();
    const headers = { ...extra };
    if (user) {
      headers["X-User-Id"] = String(user.id);
      headers["X-User-Role"] = user.role;
    }
    return headers;
  },

  async request(path, options = {}) {
    const headers = this.authHeaders(options.headers || {});
    if (!(options.body instanceof FormData)) {
      headers["Content-Type"] = headers["Content-Type"] || "application/json";
    }
    const res = await fetch(`${API_BASE}${path}`, { ...options, headers });
    let data = null;
    const text = await res.text();
    if (text) {
      try {
        data = JSON.parse(text);
      } catch {
        data = { error: text };
      }
    }
    if (!res.ok) {
      const msg = data?.error || data?.message || `Erreur HTTP ${res.status}`;
      throw new Error(msg);
    }
    return data;
  },

  async login(email, password) {
    return this.request("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    });
  },

  async register(payload) {
    return this.request("/auth/register", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },

  async getPatientOrdonnances(patientId) {
    return this.request(`/patient/ordonnances/${patientId}`);
  },

  async createOrdonnance(body) {
    return this.request("/patient/ordonnance", {
      method: "POST",
      body: JSON.stringify(body),
    });
  },

  async getPatientMedicaments() {
    return this.request("/patient/medicaments");
  },

  async getNotifications(userId) {
    return this.request(`/patient/notifications/${userId}`);
  },

  async getPatientCommandes(patientId) {
    return this.request(`/patient/commandes/${patientId}`);
  },

  async getPharmacyMedicaments() {
    return this.request("/pharmacy/medicaments");
  },

  async addPharmacyMedicament(body) {
    return this.request("/pharmacy/medicament", {
      method: "POST",
      body: JSON.stringify(body),
    });
  },

  async getPharmacyStats() {
    return this.request("/pharmacy/stats");
  },

  async getPendingOrdonnances() {
    return this.request("/pharmacy/ordonnances/pending");
  },

  async getAllOrdonnances() {
    return this.request("/pharmacy/ordonnances");
  },

  async approveOrdonnance(id) {
    return this.request(`/pharmacy/ordonnance/${id}/approve`, { method: "PUT" });
  },

  async rejectOrdonnance(id) {
    return this.request(`/pharmacy/ordonnance/${id}/reject`, { method: "PUT" });
  },

  async getPharmacyCommandes() {
    return this.request("/pharmacy/commandes");
  },

  async updateCommandeStatus(id, status) {
    return this.request(`/pharmacy/commande/${id}/status`, {
      method: "PUT",
      body: JSON.stringify({ status }),
    });
  },

  async getAllUsers() {
    return this.request("/admin/users");
  },

  async createUser(body) {
    return this.request("/admin/users", {
      method: "POST",
      body: JSON.stringify(body),
    });
  },

  async updateUser(id, body) {
    return this.request(`/admin/users/${id}`, {
      method: "PUT",
      body: JSON.stringify(body),
    });
  },

  async deleteUser(id) {
    return this.request(`/admin/users/${id}`, {
      method: "DELETE",
    });
  },
};

window.PharmaAPI = PharmaAPI;
