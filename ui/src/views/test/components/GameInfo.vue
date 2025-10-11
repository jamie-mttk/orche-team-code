<template>
    <div class="game-info">
        <div class="info-item">
            <div class="info-label">关卡</div>
            <div class="info-value level">{{ level }}</div>
        </div>
        <div class="info-item">
            <div class="info-label">分数</div>
            <div class="info-value score">{{ score }}</div>
        </div>
        <div class="info-item">
            <div class="info-label">已消除</div>
            <div class="info-value">{{ linesCleared }} 行</div>
        </div>
        <div class="info-item">
            <div class="info-label">目标</div>
            <div class="info-value target">{{ requiredLines }} 行</div>
        </div>
        <div class="progress-bar">
            <div class="progress-fill" :style="{ width: `${progress}%` }" />
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
    level: number
    score: number
    linesCleared: number
    requiredLines: number
}

const props = defineProps<Props>()

const progress = computed(() => {
    return Math.min((props.linesCleared / props.requiredLines) * 100, 100)
})
</script>

<style scoped>
.game-info {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
}

.info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
}

.info-item:last-child {
    margin-bottom: 12px;
}

.info-label {
    font-size: 14px;
    color: #7f8c8d;
    font-weight: 500;
}

.info-value {
    font-size: 20px;
    font-weight: 700;
    color: #2c3e50;
}

.info-value.level {
    color: #e74c3c;
    font-size: 24px;
}

.info-value.score {
    color: #3498db;
    font-size: 24px;
}

.info-value.target {
    color: #27ae60;
}

.progress-bar {
    width: 100%;
    height: 10px;
    background: #ecf0f1;
    border-radius: 5px;
    overflow: hidden;
    margin-top: 8px;
}

.progress-fill {
    height: 100%;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
    transition: width 0.3s ease;
    border-radius: 5px;
}
</style>

