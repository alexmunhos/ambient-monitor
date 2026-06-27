import { ref } from 'vue'
import { fetchReadings } from '../services/api'

export function useHistory() {
  const history = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function loadHistory(deviceId, from, to) {
    loading.value = true
    error.value = null
    try {
      history.value = await fetchReadings(deviceId, from, to)
    } catch (e) {
      error.value = 'Erro ao buscar histórico'
      console.error(e)
    } finally {
      loading.value = false
    }
  }

  return { history, loading, error, loadHistory }
}