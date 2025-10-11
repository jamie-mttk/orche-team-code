<template>
    <div class="game-controls">
        <el-button v-if="gameStatus === 'ready' || gameStatus === 'gameOver'" type="primary" size="large"
            @click="onStart" :icon="VideoPlay" style="width: 100%">
            {{ gameStatus === 'ready' ? '开始游戏' : '重新开始' }}
        </el-button>

        <template v-if="gameStatus === 'playing' || gameStatus === 'paused'">
            <el-button type="warning" size="large" @click="onPause"
                :icon="gameStatus === 'playing' ? VideoPause : VideoPlay" style="width: 100%; margin-bottom: 12px">
                {{ gameStatus === 'playing' ? '暂停' : '继续' }}
            </el-button>

            <el-button type="danger" size="large" @click="onRestart" :icon="RefreshRight" style="width: 100%">
                重新开始
            </el-button>
        </template>

        <el-button v-if="gameStatus === 'victory'" type="success" size="large" @click="onRestart" :icon="RefreshRight"
            style="width: 100%">
            再玩一次
        </el-button>
    </div>
</template>

<script setup lang="ts">
import { VideoPlay, VideoPause, RefreshRight } from '@element-plus/icons-vue'
import type { GameStatus } from './types'

interface Props {
    gameStatus: GameStatus
}

interface Emits {
    (e: 'start'): void
    (e: 'pause'): void
    (e: 'restart'): void
}

defineProps<Props>()
const emit = defineEmits<Emits>()

const onStart = () => emit('start')
const onPause = () => emit('pause')
const onRestart = () => emit('restart')
</script>

<style scoped>
.game-controls {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
}
</style>

