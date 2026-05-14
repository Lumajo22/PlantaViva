import axios from 'axios'

const authAPI = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true, // Importante para cookies de sesión
})

/**
 * Obtiene la información del usuario autenticado.
 * 
 * @returns {Promise} Datos del usuario logueado
 */
export const getCurrentUser = async () => {
  try {
    const response = await authAPI.get('/api/auth/user')
    return response.data
  } catch (error) {
    console.error('Error obteniendo usuario:', error)
    return null
  }
}

/**
 * Cierra la sesión del usuario.
 */
export const logout = async () => {
  try {
    await authAPI.post('/logout')
  } catch (error) {
    console.error('Error en logout:', error)
  }
}

export default authAPI