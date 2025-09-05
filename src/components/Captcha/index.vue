<template>
    <div class="captcha-container">
        <div class="captcha-input-group">
            <input type="text" v-model="userInput" placeholder="请输入验证码" @keyup.enter="verifyCaptcha" @blur="goTOLogin">
            <canvas ref="captchaCanvas" @click="refreshCaptcha" title="点击刷新验证码"></canvas>
            <button @click="refreshCaptcha">
                <i class="fa fa-refresh"></i>
            </button>
        </div>
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
        <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
    </div>
</template>

<script>
// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
export default {
    name: 'Captcha',
    data() {
        return {
            userInput: '',
            captchaText: '',
            errorMessage: '',
            successMessage: '',
            width: 120,
            height: 40,
            fontSize: 24,
            charPool: 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
        
        }
    },
    props:['changeSign'],
   mounted() {
        this.generateCaptcha();
    },
    methods: {
        // 生成随机验证码
        generateRandomText(length) {
            let text = '';
            for (let i = 0; i < length; i++) {
                text += this.charPool.charAt(Math.floor(Math.random() * this.charPool.length));
            }
            return text;
        },

        // 生成干扰线
        drawDisturbLines(ctx) {
            for (let i = 0; i < 5; i++) {
                ctx.beginPath();
                ctx.moveTo(
                    Math.random() * this.width,
                    Math.random() * this.height
                );
                ctx.lineTo(
                    Math.random() * this.width,
                    Math.random() * this.height
                );
                ctx.strokeStyle = this.getRandomColor();
                ctx.lineWidth = 1;
                ctx.stroke();
            }
        },

        // 生成干扰点
        drawDisturbDots(ctx) {
            for (let i = 0; i < 30; i++) {
                ctx.beginPath();
                ctx.arc(
                    Math.random() * this.width,
                    Math.random() * this.height,
                    1,
                    0,
                    2 * Math.PI
                );
                ctx.fillStyle = this.getRandomColor();
                ctx.fill();
            }
        },

        // 获取随机颜色
        getRandomColor() {
            const r = Math.floor(Math.random() * 150);
            const g = Math.floor(Math.random() * 150);
            const b = Math.floor(Math.random() * 150);
            return `rgb(${r}, ${g}, ${b})`;
        },

        // 生成验证码图片
        generateCaptcha() {
            const canvas = this.$refs.captchaCanvas;
            const ctx = canvas.getContext('2d');
            canvas.width = this.width;
            canvas.height = this.height;

            // 清空画布
            ctx.clearRect(0, 0, this.width, this.height);

            // 设置背景色
            ctx.fillStyle = '#f9f9f9';
            ctx.fillRect(0, 0, this.width, this.height);

            // 生成随机验证码文本
            this.captchaText = this.generateRandomText(3);

            // 绘制验证码文本
            ctx.font = `${this.fontSize}px Arial`;
            ctx.textBaseline = 'middle';

            for (let i = 0; i < this.captchaText.length; i++) {
                const char = this.captchaText.charAt(i);
                const x = 20 + i * (this.width - 40) / this.captchaText.length;
                const y = this.height / 2;

                // 设置随机颜色和旋转角度
                ctx.fillStyle = this.getRandomColor();
                ctx.save();
                ctx.translate(x, y);
                ctx.rotate(Math.random() * 0.4 - 0.2); // 随机旋转角度
                ctx.fillText(char, 0, 0);
                ctx.restore();
            }

            // 添加干扰线和点
            this.drawDisturbLines(ctx);
            this.drawDisturbDots(ctx);

            // 重置状态
            this.userInput = '';
            this.errorMessage = '';
            this.successMessage = '';
        },

        // 刷新验证码
        refreshCaptcha() {
            this.generateCaptcha();
        },
       // 验证验证码
        goTOLogin() {
                console.log(this.userInput.toLowerCase());
                console.log(this.captchaText.toLowerCase());
            if (!this.userInput) {
                this.errorMessage = '请输入验证码';
                return;
            }
            
            if (this.userInput.toLowerCase() === this.captchaText.toLowerCase()) {
                this.successMessage = '验证成功';
                this.errorMessage = '';
                this.changeSign();
                
                
                // 验证成功后刷新验证码
                setTimeout(() => {
                    this.generateCaptcha();
                }, 5000);
            } else {
                this.errorMessage = '验证码错误';
                this.successMessage = '';
                this.refreshCaptcha();
            }
        },
        
    }
}
</script>

<style scoped>
.captcha-container {
    display: flex;
    flex-direction: column;
    width: 100%;
    max-width: 320px;
}

.captcha-input-group {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
}

.captcha-input-group input {
    flex: 1;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
    outline: none;
}

.captcha-input-group input:focus {
    border-color: #409EFF;
}

.captcha-input-group canvas {
    height: 40px;
    min-width: 75px;
    border: 1px solid #eee;
    border-radius: 4px;
    cursor: pointer;
}

.captcha-input-group button {
    padding: 8px;
    background-color: #f5f7fa;
    border: 1px solid #ddd;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s;
}

.captcha-input-group button:hover {
    background-color: #e4e7ed;
}

.error-message {
    color: #f56c6c;
    font-size: 12px;
    margin-top: -14px;
    margin-bottom: -18px;
}

.success-message {
    color: #67c23a;
    font-size: 12px;
    margin-top: -14px;
    margin-bottom: -18px;
}
</style>