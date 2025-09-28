<template>
  <div class="logo-icon" :style="{ width: size + 'px', height: size + 'px' }">
    <img src="/agent-icon.svg" alt="Agent Icon" class="icon-image" />
  </div>
</template>

<script setup lang="ts">
interface Props {
  size?: number
}

withDefaults(defineProps<Props>(), {
  size: 32
})
</script>

<style scoped>
.logo-icon {
  display: inline-block;
  vertical-align: middle;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.3));
}

.icon-image {
  width: 100%;
  height: 100%;
  transition: all 0.3s ease;
}

.logo-icon:hover .icon-image {
  transform: scale(1.05);
  filter: drop-shadow(0 4px 16px rgba(139, 92, 246, 0.4));
}
</style>
