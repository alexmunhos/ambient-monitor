import { ref, onMounted, onUnmounted } from 'vue'

export function useWebSocket(url) {
  const data = ref([])
  const isConnected = ref(false)
  let socket = null

  function connect() {
    socket = new WebSocket(url)

    socket.onopen = () => {
      isConnected.value = true
      console.log('WebSocket conectado')
    }

    socket.onmessage = (event) => {
      const reading = JSON.parse(event.data)
      data.value.push(reading)

      // mantém só os últimos 50 pontos, pra não crescer infinito
      if (data.value.length > 50) {
        data.value.shift()
      }
    }

    socket.onclose = () => {
      isConnected.value = false
      console.log('WebSocket desconectado, tentando reconectar em 3s...')
      setTimeout(connect, 3000)
    }

    socket.onerror = (error) => {
      console.error('Erro no WebSocket:', error)
    }
  }

  onMounted(() => {
    connect()
  })

  onUnmounted(() => {
    if (socket) socket.close()
  })

  return { data, isConnected }
}