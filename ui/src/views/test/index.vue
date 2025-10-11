<template>
    <div class="tetris-game">
        <div class="game-container">
            <!-- 左侧：游戏画布 -->
            <div class="game-board-section">
                <TetrisBoard :board="board" :current-piece="currentPiece" />

                <!-- 游戏状态提示 -->
                <div v-if="gameStatus === 'ready'" class="game-overlay">
                    <div class="overlay-content">
                        <h1>🎮 俄罗斯方块</h1>
                        <p>准备开始游戏</p>
                    </div>
                </div>

                <div v-if="gameStatus === 'paused'" class="game-overlay">
                    <div class="overlay-content">
                        <h1>⏸️ 游戏暂停</h1>
                        <p>按 P 或点击继续按钮继续游戏</p>
                    </div>
                </div>

                <div v-if="gameStatus === 'gameOver'" class="game-overlay game-over">
                    <div class="overlay-content">
                        <h1>💀 游戏结束</h1>
                        <p>最终分数: {{ score }}</p>
                        <p>关卡: {{ currentLevel }}</p>
                    </div>
                </div>

                <div v-if="gameStatus === 'victory'" class="game-overlay victory">
                    <div class="overlay-content">
                        <h1>🎉 恭喜通关！</h1>
                        <p>你已经完成了所有10关！</p>
                        <p>最终分数: {{ score }}</p>
                    </div>
                </div>
            </div>

            <!-- 右侧：信息面板 -->
            <div class="game-info-section">
                <NextPreview :next-piece="nextPiece" />
                <GameInfo :level="currentLevel" :score="score" :lines-cleared="linesCleared"
                    :required-lines="currentLevelConfig.requiredLines" />
                <GameControls :game-status="gameStatus" @start="startGame" @pause="togglePause"
                    @restart="restartGame" />
                <KeyboardHint />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import TetrisBoard from './components/TetrisBoard.vue'
import NextPreview from './components/NextPreview.vue'
import GameInfo from './components/GameInfo.vue'
import GameControls from './components/GameControls.vue'
import KeyboardHint from './components/KeyboardHint.vue'
import {
    GameStatus,
    TetrominoType,
    TETROMINO_SHAPES,
    TETROMINO_COLORS,
    BOARD_WIDTH,
    BOARD_HEIGHT,
    LEVEL_CONFIGS,
    SCORE_CONFIG,
    type Tetromino,
    type LevelConfig
} from './components/types'

// 游戏状态
const gameStatus = ref<GameStatus>(GameStatus.READY)
const board = ref<string[][]>([])
const currentPiece = ref<Tetromino | null>(null)
const nextPiece = ref<Tetromino | null>(null)
const score = ref(0)
const linesCleared = ref(0)
const currentLevel = ref(1)
const gameTimer = ref<number | null>(null)

// 当前关卡配置
const currentLevelConfig = computed<LevelConfig>(() => {
    return LEVEL_CONFIGS[currentLevel.value - 1] || LEVEL_CONFIGS[0]
})

// 初始化游戏板
const initBoard = () => {
    board.value = Array(BOARD_HEIGHT).fill(null).map(() => Array(BOARD_WIDTH).fill(''))
}

// 生成随机方块
const createRandomPiece = (): Tetromino => {
    const types = Object.values(TetrominoType)
    const randomType = types[Math.floor(Math.random() * types.length)]
    const shapes = TETROMINO_SHAPES[randomType]

    return {
        type: randomType,
        shape: shapes[0],
        x: Math.floor((BOARD_WIDTH - shapes[0][0].length) / 2),
        y: 0,
        rotation: 0
    }
}

// 检查碰撞
const checkCollision = (piece: Tetromino, offsetX = 0, offsetY = 0): boolean => {
    const { shape, x, y } = piece

    for (let row = 0; row < shape.length; row++) {
        for (let col = 0; col < shape[row].length; col++) {
            if (shape[row][col]) {
                const newX = x + col + offsetX
                const newY = y + row + offsetY

                // 检查边界
                if (newX < 0 || newX >= BOARD_WIDTH || newY >= BOARD_HEIGHT) {
                    return true
                }

                // 检查是否与已固定的方块重叠
                if (newY >= 0 && board.value[newY][newX]) {
                    return true
                }
            }
        }
    }

    return false
}

// 移动方块
const movePiece = (direction: 'left' | 'right' | 'down') => {
    if (!currentPiece.value || gameStatus.value !== GameStatus.PLAYING) return

    const offset = direction === 'left' ? -1 : direction === 'right' ? 1 : 0
    const offsetY = direction === 'down' ? 1 : 0

    if (!checkCollision(currentPiece.value, offset, offsetY)) {
        currentPiece.value.x += offset
        currentPiece.value.y += offsetY
    } else if (direction === 'down') {
        // 方块触底，固定到棋盘
        lockPiece()
    }
}

// 旋转方块
const rotatePiece = () => {
    if (!currentPiece.value || gameStatus.value !== GameStatus.PLAYING) return

    const { type, rotation } = currentPiece.value
    const shapes = TETROMINO_SHAPES[type]
    const nextRotation = (rotation + 1) % shapes.length
    const rotatedShape = shapes[nextRotation]

    const testPiece: Tetromino = {
        ...currentPiece.value,
        shape: rotatedShape,
        rotation: nextRotation
    }

    // 尝试旋转，如果碰撞则尝试左右偏移
    if (!checkCollision(testPiece)) {
        currentPiece.value.shape = rotatedShape
        currentPiece.value.rotation = nextRotation
    } else if (!checkCollision(testPiece, 1, 0)) {
        currentPiece.value.shape = rotatedShape
        currentPiece.value.rotation = nextRotation
        currentPiece.value.x += 1
    } else if (!checkCollision(testPiece, -1, 0)) {
        currentPiece.value.shape = rotatedShape
        currentPiece.value.rotation = nextRotation
        currentPiece.value.x -= 1
    }
}

// 瞬间落地
const hardDrop = () => {
    if (!currentPiece.value || gameStatus.value !== GameStatus.PLAYING) return

    while (!checkCollision(currentPiece.value, 0, 1)) {
        currentPiece.value.y++
    }

    lockPiece()
}

// 固定方块到棋盘
const lockPiece = () => {
    if (!currentPiece.value) return

    const { shape, x, y, type } = currentPiece.value
    const color = TETROMINO_COLORS[type]

    // 将方块固定到棋盘
    for (let row = 0; row < shape.length; row++) {
        for (let col = 0; col < shape[row].length; col++) {
            if (shape[row][col]) {
                const boardY = y + row
                const boardX = x + col
                if (boardY >= 0 && boardY < BOARD_HEIGHT) {
                    board.value[boardY][boardX] = color
                }
            }
        }
    }

    // 检查并清除完整的行
    clearLines()

    // 生成新方块
    currentPiece.value = nextPiece.value
    nextPiece.value = createRandomPiece()

    // 检查游戏是否结束
    if (currentPiece.value && checkCollision(currentPiece.value)) {
        gameOver()
    }
}

// 清除完整的行
const clearLines = () => {
    let linesRemoved = 0

    for (let y = BOARD_HEIGHT - 1; y >= 0; y--) {
        if (board.value[y].every(cell => cell !== '')) {
            // 删除这一行
            board.value.splice(y, 1)
            // 在顶部添加空行
            board.value.unshift(Array(BOARD_WIDTH).fill(''))
            linesRemoved++
            y++ // 重新检查当前行
        }
    }

    if (linesRemoved > 0) {
        // 更新分数
        score.value += SCORE_CONFIG[linesRemoved as keyof typeof SCORE_CONFIG] || 0
        linesCleared.value += linesRemoved

        // 检查是否完成当前关卡
        if (linesCleared.value >= currentLevelConfig.value.requiredLines) {
            nextLevel()
        }
    }
}

// 下一关
const nextLevel = () => {
    if (currentLevel.value >= 10) {
        victory()
    } else {
        currentLevel.value++
        linesCleared.value = 0
        ElMessage.success(`恭喜进入第 ${currentLevel.value} 关！`)

        // 重置游戏速度
        if (gameTimer.value !== null) {
            clearInterval(gameTimer.value)
            startGameLoop()
        }
    }
}

// 游戏循环
const startGameLoop = () => {
    if (gameTimer.value !== null) {
        clearInterval(gameTimer.value)
    }

    gameTimer.value = window.setInterval(() => {
        if (gameStatus.value === GameStatus.PLAYING) {
            movePiece('down')
        }
    }, currentLevelConfig.value.speed)
}

// 开始游戏
const startGame = () => {
    initBoard()
    currentPiece.value = createRandomPiece()
    nextPiece.value = createRandomPiece()
    score.value = 0
    linesCleared.value = 0
    currentLevel.value = 1
    gameStatus.value = GameStatus.PLAYING
    startGameLoop()
}

// 暂停/继续游戏
const togglePause = () => {
    if (gameStatus.value === GameStatus.PLAYING) {
        gameStatus.value = GameStatus.PAUSED
    } else if (gameStatus.value === GameStatus.PAUSED) {
        gameStatus.value = GameStatus.PLAYING
    }
}

// 重新开始
const restartGame = () => {
    if (gameTimer.value !== null) {
        clearInterval(gameTimer.value)
    }
    startGame()
}

// 游戏结束
const gameOver = () => {
    gameStatus.value = GameStatus.GAME_OVER
    if (gameTimer.value !== null) {
        clearInterval(gameTimer.value)
    }
    ElMessage.error('游戏结束！')
}

// 胜利
const victory = () => {
    gameStatus.value = GameStatus.VICTORY
    if (gameTimer.value !== null) {
        clearInterval(gameTimer.value)
    }
    ElMessage.success('🎉 恭喜通关！')
}

// 键盘事件处理
const handleKeyDown = (event: KeyboardEvent) => {
    if (gameStatus.value !== GameStatus.PLAYING &&
        event.key !== 'p' && event.key !== 'P' &&
        event.key !== 'Escape') {
        return
    }

    switch (event.key) {
        case 'ArrowLeft':
            event.preventDefault()
            movePiece('left')
            break
        case 'ArrowRight':
            event.preventDefault()
            movePiece('right')
            break
        case 'ArrowDown':
            event.preventDefault()
            movePiece('down')
            break
        case 'ArrowUp':
            event.preventDefault()
            rotatePiece()
            break
        case ' ':
            event.preventDefault()
            hardDrop()
            break
        case 'p':
        case 'P':
        case 'Escape':
            event.preventDefault()
            togglePause()
            break
    }
}

// 生命周期
onMounted(() => {
    initBoard()
    window.addEventListener('keydown', handleKeyDown)
})

onUnmounted(() => {
    if (gameTimer.value !== null) {
        clearInterval(gameTimer.value)
    }
    window.removeEventListener('keydown', handleKeyDown)
})
</script>

<style scoped>
.tetris-game {
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20px;
}

.game-container {
    display: flex;
    gap: 30px;
    max-width: 1200px;
    width: 100%;
}

.game-board-section {
    position: relative;
    flex-shrink: 0;
}

.game-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.85);
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 12px;
    z-index: 10;
}

.overlay-content {
    text-align: center;
    color: white;
}

.overlay-content h1 {
    font-size: 48px;
    margin: 0 0 20px 0;
    font-weight: 700;
}

.overlay-content p {
    font-size: 20px;
    margin: 10px 0;
    opacity: 0.9;
}

.game-over .overlay-content {
    color: #e74c3c;
}

.victory .overlay-content {
    color: #2ecc71;
}

.game-info-section {
    display: flex;
    flex-direction: column;
    min-width: 280px;
}

@media (max-width: 768px) {
    .game-container {
        flex-direction: column;
        align-items: center;
    }

    .game-info-section {
        width: 100%;
        max-width: 400px;
    }
}
</style>
