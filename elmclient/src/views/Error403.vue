<template>
  <div class="error-container">
    <div class="error-content">
      <div class="error-icon">
        ⚠️
      </div>
      <h1>403</h1>
      <h2>访问被拒绝</h2>
      <p>{{ displayMessage }}</p>
      <div class="error-actions">
        <button @click="goBack" class="back-button">
          ← 返回上一页
        </button>
        <button @click="goHome" class="home-button">
          🏠 返回首页
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: 'Error403',
  props: {
    message: {
      type: String,
      default: '抱歉，您没有权限访问此页面'
    }
  },
  setup(props) {
    const router = useRouter();
    const displayMessage = computed(() => props.message);

    const goBack = () => {
      router.back();
    };

    const goHome = () => {
      router.push('/');
    };

    return {
      displayMessage,
      goBack,
      goHome
    };
  }
}
</script>

<style scoped>
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.error-content {
  text-align: center;
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  max-width: 500px;
  width: 100%;
}

.error-icon {
  font-size: 72px;
  margin-bottom: 20px;
  animation: bounce 2s infinite;
}

h1 {
  font-size: 72px;
  color: #ff4d4f;
  margin: 0;
  line-height: 1;
  font-weight: bold;
}

h2 {
  font-size: 28px;
  color: #333;
  margin: 20px 0;
  font-weight: 600;
}

p {
  color: #666;
  margin-bottom: 30px;
  font-size: 16px;
  line-height: 1.6;
}

.error-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

button {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.back-button {
  background-color: #f0f0f0;
  color: #333;
}

.back-button:hover {
  background-color: #e0e0e0;
  transform: translateY(-1px);
}

.home-button {
  background-color: #1890ff;
  color: white;
}

.home-button:hover {
  background-color: #40a9ff;
  transform: translateY(-1px);
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-20px);
  }
  60% {
    transform: translateY(-10px);
  }
}
</style> 