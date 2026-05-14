import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

// ═══════════════════════════════════════════════════════════════════
//  PLANTAS
// ═══════════════════════════════════════════════════════════════════

export const plantasAPI = {
  getAll: (page = 0, size = 10) => 
    api.get(`/plantas?page=${page}&size=${size}`),
  
  getById: (id) => 
    api.get(`/plantas/${id}`),
  
  create: (data) => 
    api.post('/plantas', data),
  
  update: (id, data) => 
    api.put(`/plantas/${id}`, data),
  
  delete: (id) => 
    api.delete(`/plantas/${id}`),
}

// ═══════════════════════════════════════════════════════════════════
//  SENSORES
// ═══════════════════════════════════════════════════════════════════

export const sensoresAPI = {
  getAll: () => 
    api.get('/sensores'),
  
  getByPlanta: (plantaId) => 
    api.get(`/sensores/planta/${plantaId}`),
  
  getById: (id) => 
    api.get(`/sensores/${id}`),
  
  create: (data) => 
    api.post('/sensores', data),
  
  update: (id, data) => 
    api.put(`/sensores/${id}`, data),
  
  delete: (id) => 
    api.delete(`/sensores/${id}`),
}

// ═══════════════════════════════════════════════════════════════════
//  LECTURAS
// ═══════════════════════════════════════════════════════════════════

export const lecturasAPI = {
  getBySensor: (sensorId, page = 0, size = 20) => 
    api.get(`/lecturas/sensor/${sensorId}?page=${page}&size=${size}`),
  
  getById: (id) => 
    api.get(`/lecturas/${id}`),
  
  create: (data) => 
    api.post('/lecturas', data),
  
  delete: (id) => 
    api.delete(`/lecturas/${id}`),
}

// ═══════════════════════════════════════════════════════════════════
//  ALERTAS
// ═══════════════════════════════════════════════════════════════════

export const alertasAPI = {
  getAll: () => 
    api.get('/alertas'),
  
  getPendientes: () => 
    api.get('/alertas/pendientes'),
  
  getByPlanta: (plantaId) => 
    api.get(`/alertas/planta/${plantaId}`),
  
  getById: (id) => 
    api.get(`/alertas/${id}`),
  
  marcarRevisada: (id) => 
    api.patch(`/alertas/${id}/revisar`),
  
  countPendientes: () => 
    api.get('/alertas/count/pendientes'),
  
  delete: (id) => 
    api.delete(`/alertas/${id}`),
}

export default api
