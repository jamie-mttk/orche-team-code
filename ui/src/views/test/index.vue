<template>


    <Icon name="mdiAccessPointNetwork" size="small" color="#409eff" />
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import Icon from '@/components/mdiIicon/index.vue'
// do not use same name with ref
const form = reactive({
    name: '',
    attachments: [{ "id": "1", "name": "1.txt", size: 25, "description": "这是我的描述" }]
})

</script>

<style scoped></style>
