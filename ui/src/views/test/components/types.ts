// 游戏状态
export const GameStatus = {
    READY: 'ready',
    PLAYING: 'playing',
    PAUSED: 'paused',
    GAME_OVER: 'gameOver',
    VICTORY: 'victory'
} as const

export type GameStatus = typeof GameStatus[keyof typeof GameStatus]

// 方块类型
export const TetrominoType = {
    I: 'I',
    O: 'O',
    T: 'T',
    S: 'S',
    Z: 'Z',
    L: 'L',
    J: 'J'
} as const

export type TetrominoType = typeof TetrominoType[keyof typeof TetrominoType]

// 方块颜色映射
export const TETROMINO_COLORS: Record<TetrominoType, string> = {
    [TetrominoType.I]: '#00f0f0',
    [TetrominoType.O]: '#f0f000',
    [TetrominoType.T]: '#a000f0',
    [TetrominoType.S]: '#00f000',
    [TetrominoType.Z]: '#f00000',
    [TetrominoType.L]: '#f0a000',
    [TetrominoType.J]: '#0000f0'
}

// 方块形状定义（每个方块有4个旋转状态）
export const TETROMINO_SHAPES: Record<TetrominoType, number[][][]> = {
    [TetrominoType.I]: [
        [[0, 0, 0, 0], [1, 1, 1, 1], [0, 0, 0, 0], [0, 0, 0, 0]],
        [[0, 0, 1, 0], [0, 0, 1, 0], [0, 0, 1, 0], [0, 0, 1, 0]],
        [[0, 0, 0, 0], [0, 0, 0, 0], [1, 1, 1, 1], [0, 0, 0, 0]],
        [[0, 1, 0, 0], [0, 1, 0, 0], [0, 1, 0, 0], [0, 1, 0, 0]]
    ],
    [TetrominoType.O]: [
        [[1, 1], [1, 1]],
        [[1, 1], [1, 1]],
        [[1, 1], [1, 1]],
        [[1, 1], [1, 1]]
    ],
    [TetrominoType.T]: [
        [[0, 1, 0], [1, 1, 1], [0, 0, 0]],
        [[0, 1, 0], [0, 1, 1], [0, 1, 0]],
        [[0, 0, 0], [1, 1, 1], [0, 1, 0]],
        [[0, 1, 0], [1, 1, 0], [0, 1, 0]]
    ],
    [TetrominoType.S]: [
        [[0, 1, 1], [1, 1, 0], [0, 0, 0]],
        [[0, 1, 0], [0, 1, 1], [0, 0, 1]],
        [[0, 0, 0], [0, 1, 1], [1, 1, 0]],
        [[1, 0, 0], [1, 1, 0], [0, 1, 0]]
    ],
    [TetrominoType.Z]: [
        [[1, 1, 0], [0, 1, 1], [0, 0, 0]],
        [[0, 0, 1], [0, 1, 1], [0, 1, 0]],
        [[0, 0, 0], [1, 1, 0], [0, 1, 1]],
        [[0, 1, 0], [1, 1, 0], [1, 0, 0]]
    ],
    [TetrominoType.L]: [
        [[0, 0, 1], [1, 1, 1], [0, 0, 0]],
        [[0, 1, 0], [0, 1, 0], [0, 1, 1]],
        [[0, 0, 0], [1, 1, 1], [1, 0, 0]],
        [[1, 1, 0], [0, 1, 0], [0, 1, 0]]
    ],
    [TetrominoType.J]: [
        [[1, 0, 0], [1, 1, 1], [0, 0, 0]],
        [[0, 1, 1], [0, 1, 0], [0, 1, 0]],
        [[0, 0, 0], [1, 1, 1], [0, 0, 1]],
        [[0, 1, 0], [0, 1, 0], [1, 1, 0]]
    ]
}

// 关卡配置
export interface LevelConfig {
    level: number
    speed: number // 下落间隔（毫秒）
    requiredLines: number // 需要消除的行数
}

export const LEVEL_CONFIGS: LevelConfig[] = [
    { level: 1, speed: 1000, requiredLines: 10 },
    { level: 2, speed: 900, requiredLines: 12 },
    { level: 3, speed: 800, requiredLines: 14 },
    { level: 4, speed: 700, requiredLines: 16 },
    { level: 5, speed: 600, requiredLines: 18 },
    { level: 6, speed: 500, requiredLines: 20 },
    { level: 7, speed: 400, requiredLines: 22 },
    { level: 8, speed: 350, requiredLines: 24 },
    { level: 9, speed: 300, requiredLines: 26 },
    { level: 10, speed: 250, requiredLines: 28 }
]

// 方块接口
export interface Tetromino {
    type: TetrominoType
    shape: number[][]
    x: number
    y: number
    rotation: number
}

// 游戏配置常量
export const BOARD_WIDTH = 10
export const BOARD_HEIGHT = 20
export const CELL_SIZE = 30

// 分数配置
export const SCORE_CONFIG = {
    1: 100,  // 消除1行
    2: 300,  // 消除2行
    3: 600,  // 消除3行
    4: 1000  // 消除4行
}

