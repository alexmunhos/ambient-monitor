<script setup>
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { useWebSocket } from '../composables/useWebSocket'

const { data, isConnected } = useWebSocket('ws://localhost:8080/ws/readings')

const chartOption = computed(() => ({
  title: { text: 'Temperatura e Umidade — Tempo Real' },
  tooltip: { trigger: 'axis' },
  legend: { data: ['Temperatura', 'Umidade'] },
  xAxis: {
    type: 'category',
    data: data.value.map(d => new Date(d.receivedAt).toLocaleTimeString())
  },
  yAxis: { type: 'value' },
  series: [
    {
      name: 'Temperatura',
      type: 'line',
      data: data.value.map(d => d.temperature)
    },
    {
      name: 'Umidade',
      type: 'line',
      data: data.value.map(d => d.humidity)
    }
  ]
}))
</script>

<template>
  <div>
    <p>Status: {{ isConnected ? 'Conectado' : 'Desconectado' }}</p>
    <v-chart :option="chartOption" style="height: 400px" autoresize />
  </div>
</template>