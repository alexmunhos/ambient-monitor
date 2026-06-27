<script setup>
import RealtimeChart from './components/RealtimeChart.vue'
import HistoryChart from './components/HistoryChart.vue'
</script>

<template>
  <main>
    <h1>Ambient Monitor</h1>
    <RealtimeChart />
    <hr />
    <HistoryChart />
  </main>
</template>