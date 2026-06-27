<script setup>
import { computed, ref } from 'vue'
import VChart from 'vue-echarts'
import { useHistory } from '../composables/useHistory'

const { history, loading, error, loadHistory } = useHistory()

const deviceId = ref('esp_1')
const fromDate = ref('')
const toDate = ref('')

function search() {
  const from = new Date(fromDate.value).toISOString()
  const to = new Date(toDate.value).toISOString()
  loadHistory(deviceId.value, from, to)
}

const chartOption = computed(() => ({
  title: { text: 'Histórico' },
  tooltip: { trigger: 'axis' },
  legend: { data: ['Temperatura', 'Umidade'] },
  xAxis: {
    type: 'category',
    data: history.value.map(d => new Date(d.receivedAt).toLocaleString())
  },
  yAxis: { type: 'value' },
  series: [
    {
      name: 'Temperatura',
      type: 'line',
      data: history.value.map(d => d.temperature)
    },
    {
      name: 'Umidade',
      type: 'line',
      data: history.value.map(d => d.humidity)
    }
  ]
}))
</script>

<template>
  <div>
    <div>
      <label>De: <input type="datetime-local" v-model="fromDate" /></label>
      <label>Até: <input type="datetime-local" v-model="toDate" /></label>
      <button @click="search">Buscar</button>
    </div>

    <p v-if="loading">Carregando...</p>
    <p v-if="error">{{ error }}</p>

    <v-chart v-if="!loading && history.length" :option="chartOption" style="height: 400px" autoresize />
  </div>
</template>