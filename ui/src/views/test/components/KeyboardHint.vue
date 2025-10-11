<template>
    <div class="keyboard-hint">
        <h3>操作说明</h3>
        <div class="hint-list">
            <div class="hint-item">
                <kbd>←</kbd>
                <span>左移</span>
            </div>
            <div class="hint-item">
                <kbd>→</kbd>
                <span>右移</span>
            </div>
            <div class="hint-item">
                <kbd>↑</kbd>
                <span>旋转</span>
            </div>
            <div class="hint-item">
                <kbd>↓</kbd>
                <span>加速</span>
            </div>
            <div class="hint-item">
                <kbd>空格</kbd>
                <span>瞬降</span>
            </div>
            <div class="hint-item">
                <kbd>P</kbd>
                <span>暂停</span>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.keyboard-hint {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.keyboard-hint h3 {
    margin: 0 0 16px 0;
    font-size: 18px;
    color: #2c3e50;
    text-align: center;
    font-weight: 600;
}

.hint-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.hint-item {
    display: flex;
    align-items: center;
    gap: 12px;
}

kbd {
    display: inline-block;
    min-width: 50px;
    padding: 6px 12px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-radius: 6px;
    font-size: 14px;
    font-weight: 600;
    text-align: center;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

.hint-item span {
    font-size: 14px;
    color: #2c3e50;
}
</style>

