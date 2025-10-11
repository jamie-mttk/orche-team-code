<template>
    <div class="next-preview">
        <h3>下一个</h3>
        <div class="preview-container">
            <div v-if="nextPiece" class="preview-grid" :style="{
                width: `${previewSize * CELL_SIZE}px`,
                height: `${previewSize * CELL_SIZE}px`
            }">
                <div v-for="(row, y) in previewGrid" :key="y" class="preview-row">
                    <div v-for="(cell, x) in row" :key="x" class="preview-cell" :style="{
                        backgroundColor: cell || '#34495e',
                        width: `${CELL_SIZE}px`,
                        height: `${CELL_SIZE}px`
                    }" />
                </div>
            </div>
            <div v-else class="no-preview">
                <span>暂无</span>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { CELL_SIZE, TETROMINO_COLORS, type Tetromino } from './types'

interface Props {
    nextPiece: Tetromino | null
}

const props = defineProps<Props>()

const previewSize = 4

const previewGrid = computed(() => {
    if (!props.nextPiece) {
        return []
    }

    const grid: string[][] = Array(previewSize).fill(null).map(() => Array(previewSize).fill(''))
    const { shape, type } = props.nextPiece
    const color = TETROMINO_COLORS[type]

    // 计算居中位置
    const offsetY = Math.floor((previewSize - shape.length) / 2)
    const offsetX = Math.floor((previewSize - shape[0].length) / 2)

    for (let row = 0; row < shape.length; row++) {
        for (let col = 0; col < shape[row].length; col++) {
            if (shape[row][col]) {
                grid[offsetY + row][offsetX + col] = color
            }
        }
    }

    return grid
})
</script>

<style scoped>
.next-preview {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
}

.next-preview h3 {
    margin: 0 0 16px 0;
    font-size: 18px;
    color: #2c3e50;
    text-align: center;
    font-weight: 600;
}

.preview-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 120px;
}

.preview-grid {
    border: 2px solid #ecf0f1;
    border-radius: 8px;
    overflow: hidden;
}

.preview-row {
    display: flex;
}

.preview-cell {
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-sizing: border-box;
}

.no-preview {
    display: flex;
    justify-content: center;
    align-items: center;
    color: #95a5a6;
    font-size: 14px;
}
</style>

