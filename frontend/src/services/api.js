import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
})

export async function fetchReadings(deviceId, from, to) {
  const response = await api.get('/readings', {
    params: { deviceId, from, to }
  })
  return response.data
}