<template>
    <div class="tetris-board">
        <div class="board-grid" :style="{
            width: `${BOARD_WIDTH * CELL_SIZE}px`,
            height: `${BOARD_HEIGHT * CELL_SIZE}px`
        }">
            <div v-for="(row, y) in displayBoard" :key="y" class="board-row">
                <div v-for="(cell, x) in row" :key="x" class="board-cell" :style="{
                    backgroundColor: cell || '#2c3e50',
                    width: `${CELL_SIZE}px`,
                    height: `${CELL_SIZE}px`
                }" />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { BOARD_WIDTH, BOARD_HEIGHT, CELL_SIZE, TETROMINO_COLORS, type Tetromino } from './types'

interface Props {
    board: string[][]
    currentPiece: Tetromino | null
}

const props = defineProps<Props>()

// 合并棋盘和当前方块的显示
const displayBoard = computed(() => {
    const result = props.board.map(row => [...row])

    if (props.currentPiece) {
        const { shape, x, y, type } = props.currentPiece
        const color = TETROMINO_COLORS[type]

        for (let row = 0; row < shape.length; row++) {
            for (let col = 0; col < shape[row].length; col++) {
                if (shape[row][col]) {
                    const boardY = y + row
                    const boardX = x + col
                    if (boardY >= 0 && boardY < BOARD_HEIGHT && boardX >= 0 && boardX < BOARD_WIDTH) {
                        result[boardY][boardX] = color
                    }
                }
            }
        }
    }

    return result
})
</script>

<style scoped>
.tetris-board {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.board-grid {
    border: 3px solid #34495e;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: inset 0 0 20px rgba(0, 0, 0, 0.5);
}

.board-row {
    display: flex;
}

.board-cell {
    border: 1px solid rgba(255, 255, 255, 0.1);
    transition: background-color 0.1s ease;
    box-sizing: border-box;
}
</style>

