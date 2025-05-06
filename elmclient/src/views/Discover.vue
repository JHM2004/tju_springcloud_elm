<template>
  <div class="wrapper">
    <!-- 头部 -->
    <header>
      <p>智能搜索</p>
    </header>

    <!-- 自定义提示框 -->
    <div class="toast" v-if="showToast">
      {{ toastMessage }}
    </div>

    <!-- 对话区域 -->
    <div class="chat-container" ref="chatContainer">
      <!-- 欢迎消息 -->
      <div class="message bot-message" v-if="!chatMessages.length">
        <div class="message-content welcome-message">
          <p>你好！我是智能搜索助手，请输入你想找的商家或美食，我会帮你找到相关商家。</p>
          <p>例如：输入"饺子"可以找到所有提供饺子的商家。</p>
        </div>
      </div>
      
      <!-- 对话消息 -->
      <div v-for="(message, index) in chatMessages" :key="index">
        <!-- 用户消息 -->
        <div class="message user-message">
          <div class="message-content">
            {{ message.query }}
          </div>
        </div>
        
        <!-- 机器人回复 -->
        <div class="message bot-message">
          <div class="message-content">
            <!-- 搜索结果卡片 -->
            <div class="search-card">
              <div class="search-results">
                <div v-if="message.results && message.results.length > 0">
                  <div v-for="(item, idx) in currentPageResults(message)" 
                       :key="idx" 
                       class="search-result-item"
                       @click="handleResultClick(item)">
                    <img :src="item.img" v-if="item.img" :alt="item.name">
                    <div class="result-info">
                      <h4>{{ item.name }}</h4>
                      <p v-if="item.type === 'business'">
                        起送价：¥{{ item.starPrice }} | 配送费：¥{{ item.deliveryPrice }}
                      </p>
                      <p v-if="item.type === 'food'">价格：¥{{ item.price }}</p>
                      <p>点击查看详情</p>
                    </div>
                  </div>
                </div>
                <div v-else class="no-results">
                  <p>抱歉，没有找到相关结果</p>
                  <p>请尝试其他关键词搜索</p>
                </div>
              </div>
              
              <!-- 分页控件 -->
              <div class="pagination-controls" v-if="message.results && message.results.length > 0">
                <button class="page-button" 
                        @click="prevPage(message)">
                  上一页
                </button>
                <div class="page-info">
                  <span>{{ message.currentPage }}/{{ totalPages(message) }}</span>
                </div>
                <button class="page-button" 
                        @click="nextPage(message)">
                  下一页
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入框部分 -->
    <div class="input-section">
      <input
        v-model="userQuery"
        type="text"
        placeholder="输入您感兴趣的食品..."
        @keyup.enter="sendMessage"
      />
      <button @click="sendMessage">发送</button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick, onActivated } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';

export default {
  name: 'Discover',
  setup() {
    const router = useRouter();
    const route = useRoute();
    const userQuery = ref('');
    const chatMessages = ref([]);
    const chatContainer = ref(null);
    const pageSize = 3; // 每页显示3条结果
    const showToast = ref(false);
    const toastMessage = ref('');
    
    // 显示提示框
    const showToastMessage = (message) => {
      toastMessage.value = message;
      showToast.value = true;
      
      // 2秒后自动关闭
      setTimeout(() => {
        showToast.value = false;
      }, 2000);
    };
    
    // 获取当前页的结果
    const currentPageResults = (message) => {
      if (!message.results) return [];
      const start = (message.currentPage - 1) * pageSize;
      return message.results.slice(start, start + pageSize);
    };
    
    // 计算总页数
    const totalPages = (message) => {
      if (!message.results) return 1;
      return Math.ceil(message.results.length / pageSize);
    };
    
    // 上一页
    const prevPage = (message) => {
      if (message.currentPage > 1) {
        // 添加过渡效果
        const resultsContainer = document.querySelector('.search-results');
        if (resultsContainer) {
          resultsContainer.style.opacity = '0';
          resultsContainer.style.transform = 'translateY(5px)';
          
          setTimeout(() => {
            message.currentPage--;
            // 保存状态到数据库
            updateCurrentPageInDB(message);
            nextTick(() => {
              resultsContainer.style.opacity = '1';
              resultsContainer.style.transform = 'translateY(0)';
            });
          }, 100);
        } else {
          message.currentPage--;
          // 保存状态到数据库
          updateCurrentPageInDB(message);
        }
      } else {
        // 已经是第一页，显示提示
        showToastMessage('已经是第一页了');
      }
    };
    
    // 下一页
    const nextPage = (message) => {
      if (message.currentPage < totalPages(message)) {
        // 添加过渡效果
        const resultsContainer = document.querySelector('.search-results');
        if (resultsContainer) {
          resultsContainer.style.opacity = '0';
          resultsContainer.style.transform = 'translateY(-5px)';
          
          setTimeout(() => {
            message.currentPage++;
            // 保存状态到数据库
            updateCurrentPageInDB(message);
            nextTick(() => {
              resultsContainer.style.opacity = '1';
              resultsContainer.style.transform = 'translateY(0)';
            });
          }, 100);
        } else {
          message.currentPage++;
          // 保存状态到数据库
          updateCurrentPageInDB(message);
        }
      } else {
        // 已经是最后一页，显示提示
        showToastMessage('已经是最后一页了');
      }
    };

    // 处理搜索结果点击
    const handleResultClick = (item) => {
      console.log('点击商家:', item);
      if (item.type === 'business') {
        // 确保businessId是数字类型
        const businessId = parseInt(item.businessId);
        console.log('跳转到商家页面, businessId:', businessId);
        router.push({
          path: '/businessInfo',
          query: { businessId }
        });
      }
    };

    // 发送消息
    const sendMessage = async () => {
      if (!userQuery.value.trim()) return;

      const query = userQuery.value;
      const userId = sessionStorage.getItem('user') ? JSON.parse(sessionStorage.getItem('user')).userId : '0';
      
      try {
        // 使用BusinessController的listBusinessByOrderTypeId接口（已知这个接口可用）
        const response = await axios.get(`BusinessController/listBusinessByOrderTypeId/1`);
        
        // 在前端进行过滤，查找名称中包含搜索关键词的商家
        const filteredBusinesses = response.data?.result?.filter(business => 
          business.businessName && business.businessName.toLowerCase().includes(query.toLowerCase())
        ) || [];

        const newMessage = {
          query,
          currentPage: 1,
          results: filteredBusinesses.map(business => ({
            type: 'business',
            businessId: business.businessId,
            name: business.businessName,
            img: business.businessImg,
            starPrice: business.starPrice,
            deliveryPrice: business.deliveryPrice
          }))
        };

        chatMessages.value.push(newMessage);
        
        // 直接在本地存储对话
        localStorage.setItem('discover_chat_messages', JSON.stringify(chatMessages.value));

        userQuery.value = '';
        
        await nextTick();
        scrollToBottom();
      } catch (error) {
        console.error('搜索失败:', error);
      }
    };

    // 更新页码（仅更新本地状态，不发送API请求）
    const updateCurrentPageInDB = async (message) => {
      // 仅更新本地存储
      localStorage.setItem('discover_chat_messages', JSON.stringify(chatMessages.value));
    };

    // 从本地存储加载消息
    const loadMessagesFromDB = async () => {
      const userId = sessionStorage.getItem('user') ? JSON.parse(sessionStorage.getItem('user')).userId : '0';
      if (userId === '0') return; // 未登录用户不加载历史
      
      try {
        // 从本地存储加载
        const savedMessages = localStorage.getItem('discover_chat_messages');
        if (savedMessages) {
          chatMessages.value = JSON.parse(savedMessages);
          
          // 加载后滚动到底部
          nextTick(() => {
            scrollToBottom();
          });
        }
      } catch (error) {
        console.error('加载搜索历史失败:', error);
      }
    };

    // 清空聊天记录
    const clearChatMessages = async () => {
      // 仅清空本地存储
      localStorage.removeItem('discover_chat_messages');
      chatMessages.value = [];
    };

    // 组件挂载时加载保存的消息
    onMounted(() => {
      console.log('组件挂载，加载搜索历史');
      loadMessagesFromDB();
      
      // 监听路由变化
      router.afterEach((to, from) => {
        // 如果路由变到了Discover页面
        if (to.name === 'Discover' || to.path === '/discover') {
          console.log('路由返回到Discover页面，重新加载历史');
          loadMessagesFromDB();
        }
      });
      
      // 监听登录状态变化
      window.addEventListener('storage', (event) => {
        if (event.key === 'user' && !event.newValue) {
          // 用户退出登录
          clearChatMessages();
        }
      });
    });
    
    // 使用onActivated生命周期钩子，每次组件被激活时都重新加载
    onActivated(() => {
      console.log('组件被激活，重新加载搜索历史');
      loadMessagesFromDB();
    });

    // 滚动到底部
    const scrollToBottom = () => {
      if (chatContainer.value) {
        chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
      }
    };

    return {
      userQuery,
      chatMessages,
      chatContainer,
      sendMessage,
      handleResultClick,
      currentPageResults,
      prevPage,
      nextPage,
      totalPages,
      pageSize,
      clearChatMessages,
      showToast,
      toastMessage
    };
  }
};
</script>

<style scoped>
.wrapper {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

header {
  width: 100%;
  height: 12vw;
  background-color: #0097FF;
  color: #fff;
  font-size: 4.8vw;
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  padding: 0;
}

header p {
  flex: none;
  text-align: center;
  margin: 0;
  padding: 0;
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 15vw 4vw 25vw 4vw;
  background-color: #f5f5f5;
}

.chat-container > div:last-child {
  margin-bottom: 10vw;
}

.message {
  margin-bottom: 4vw;
  max-width: 80%;
  animation: fadeIn 0.3s ease;
  display: flex;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.user-message {
  justify-content: flex-end;
  margin-left: auto;
  width: 100%;
}

.bot-message {
  justify-content: flex-start;
  margin-right: auto;
  width: 100%;
}

.message-content {
  padding: 3vw;
  border-radius: 4vw;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  word-wrap: break-word;
  word-break: break-all;
  white-space: pre-wrap;
  max-width: 80%;
}

.user-message .message-content {
  background-color: #0097FF;
  color: #fff;
  min-width: 15vw;
  margin-left: auto;
}

.welcome-message {
  color: #666;
  font-size: 3.5vw;
}

.search-card {
  background: #fff;
  border-radius: 2vw;
  margin-bottom: 4vw;
  overflow: hidden;
}

.search-results {
  max-height: 50vh;
  overflow-y: auto;
  padding: 0 2vw;
}

.search-result-item {
  display: flex;
  padding: 3vw;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.3s, opacity 0.3s, transform 0.3s;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(5px); }
  to { opacity: 1; transform: translateY(0); }
}

.search-result-item:last-child {
  border-bottom: none;
}

.search-result-item:active {
  background-color: #f5f5f5;
}

.search-result-item img {
  width: 18vw;
  height: 18vw;
  object-fit: cover;
  border-radius: 2vw;
  margin-right: 3vw;
}

.result-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.result-info h4 {
  margin: 0;
  font-size: 4vw;
  color: #333;
  font-weight: bold;
  margin-bottom: 1.5vw;
}

.result-info p {
  margin: 0 0 1vw 0;
  font-size: 3.2vw;
  color: #666;
}

/* 分页控件样式 */
.pagination-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2vw 4vw 3vw 4vw;
  border-top: 1px solid #eee;
  background-color: #f8f8f8;
}

.page-button {
  padding: 1.5vw 3.5vw;
  border: 1px solid #0097FF;
  border-radius: 3vw;
  background-color: #fff;
  color: #0097FF;
  font-size: 3.2vw;
  display: flex;
  align-items: center;
  gap: 1vw;
  transition: all 0.2s ease;
}

.page-button:active:not(:disabled) {
  background-color: #e6f4ff;
  transform: scale(0.98);
}

.page-button:disabled {
  opacity: 0.5;
  border-color: #ccc;
  color: #ccc;
}

.page-button i {
  font-size: 2.8vw;
}

.page-info {
  flex: 1;
  text-align: center;
  font-size: 3.2vw;
  color: #666;
}

.input-section {
  position: fixed;
  bottom: 17vw;
  left: 0;
  width: 100%;
  padding: 2vw;
  background-color: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 2vw;
  z-index: 999;
}

.input-section input {
  flex: 1;
  padding: 1.5vw 3vw;
  border: 1px solid #ddd;
  border-radius: 4vw;
  font-size: 3.5vw;
  transition: border-color 0.3s;
  height: 9vw;
  line-height: 9vw;
}

.input-section input:focus {
  border-color: #0097FF;
  outline: none;
}

.input-section button {
  margin-top: 1vw;
  margin-right: 3vw;
  padding: 0 4vw;
  border: none;
  border-radius: 4vw;
  background-color: #0097FF;
  color: #fff;
  font-size: 4vw;
  transition: background-color 0.3s;
  height: 10vw;
  line-height: 9vw;
}

.input-section button:active {
  background-color: #0085e5;
}

.no-results {
  padding: 5vw;
  text-align: center;
  color: #999;
}

.no-results p {
  margin: 1vw 0;
  font-size: 3.5vw;
}

.no-results p:first-child {
  font-size: 4vw;
  color: #666;
}

.toast {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 3vw 5vw;
  border-radius: 1.5vw;
  font-size: 3.5vw;
  z-index: 2000;
  animation: fadeInOut 2s ease;
  pointer-events: none;
  text-align: center;
  max-width: 80%;
}

@keyframes fadeInOut {
  0% { opacity: 0; }
  20% { opacity: 1; }
  80% { opacity: 1; }
  100% { opacity: 0; }
}

/* 隐藏任何可能的返回按钮 */
.back-button, 
div[class*="back"], 
button[class*="back"], 
a[class*="back"],
i[class*="arrow-left"],
i[class*="chevron-left"] {
  display: none !important;
}
</style>
