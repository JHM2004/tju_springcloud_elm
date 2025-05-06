<template>
  <div class="test-container">
    <h2>错误测试页面</h2>
    <div class="test-buttons">
      <button @click="testError403">测试403错误</button>
      <button @click="testLoadBalancing">测试负载均衡</button>
      <button @click="testCircuitBreaker">测试熔断</button>
    </div>
    <div class="result" v-if="result">
      <h3>测试结果：</h3>
      <pre>{{ result }}</pre>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

export default {
  name: 'TestError',
  setup() {
    const result = ref('');
    const router = useRouter();

    // 测试403错误
    const testError403 = async () => {
      try {
        result.value = '正在发送请求...';
        console.log('开始测试403错误');
        
        // 清除可能存在的登录信息
        sessionStorage.removeItem('user');
        sessionStorage.removeItem('businessUser');
        console.log('已清除登录信息');
        
        // 直接模拟一个403错误，不实际发送请求
        console.log('模拟403错误');
        const error = {
          response: {
            status: 403,
            data: {
              code: 403,
              message: '访问被拒绝，请先登录',
              result: null
            }
          }
        };
        
        // 显示错误信息
        console.log('捕获到错误:', error);
        result.value = `捕获到错误：
状态码：${error.response.status}
错误信息：${error.response.data.message}`;

        // 跳转到错误页面
        console.log('检测到403错误，正在跳转到错误页面...');
        router.replace({
          path: '/error403',
          query: { message: error.response.data.message }
        });
        
      } catch (error) {
        console.log('发生意外错误:', error);
        result.value = `发生意外错误：${error.message}`;
      }
    };

    // 测试负载均衡
    const testLoadBalancing = async () => {
      try {
        const results = [];
        // 连续发送多个请求，观察负载均衡效果
        for (let i = 0; i < 10; i++) {
          const response = await axios.get('/plus/BusinessController/listBusinessByOrderTypeId/1');
          results.push(`请求${i + 1}: ${response.headers['server'] || '未知服务器'}`);
        }
        result.value = results.join('\n');
      } catch (error) {
        result.value = `负载均衡测试失败：${error.message}`;
      }
    };

    // 测试熔断
    const testCircuitBreaker = async () => {
      try {
        const results = [];
        // 快速发送大量请求，触发熔断
        const requests = Array(20).fill().map(() => 
          axios.get('/plus/VirtualWalletController/getBalanceById', {
            params: { userId: '12345' }
          })
        );
        
        await Promise.all(requests);
      } catch (error) {
        result.value = `熔断测试完成：${error.message}`;
      }
    };

    return {
      result,
      testError403,
      testLoadBalancing,
      testCircuitBreaker
    };
  }
}
</script>

<style scoped>
.test-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-buttons {
  display: flex;
  gap: 10px;
  margin: 20px 0;
}

button {
  padding: 10px 20px;
  background-color: #0097FF;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #0085e6;
}

.result {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 5px;
}

pre {
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style> 