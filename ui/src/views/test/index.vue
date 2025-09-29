<template>

    <el-tooltip effect="light" :disabled="disabledFlag" :raw-content="true" :content="tooltip" :show-after="1000"
        placement="bottom">
        <el-button type="primary" @click="handleClick">TEST - {{ disabledFlag }}</el-button>
    </el-tooltip>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'

// do not use same name with ref
const form = reactive({
    name: '',
    attachments: [{ "id": "1", "name": "1.txt", size: 25, "description": "这是我的描述" }]
})
const disabledFlag = ref(false)
const tooltip = ref('This is tooltip')

function handleClick() {
    disabledFlag.value = !disabledFlag.value
}
</script>

<style scoped></style>
