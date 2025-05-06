<template>
  <div class="wrapper">
    <!-- 页面顶部导航栏 -->
    <header>
      <!-- 返回按钮 -->
      <div class="back-button" @click="$router.go(-1)">
        <i class="fa fa-arrow-left"></i>
      </div>
      <p>我的积分</p>
    </header>

    <!-- 积分概览区域：显示总积分和操作按钮 -->
    <div class="credit-overview">
      <!-- 积分数量显示 -->
      <div class="credit-amount">
        <h2>{{ totalCredit }}</h2>
        <p>当前积分</p>
      </div>
      <!-- 签到和兑换按钮 -->
      <div class="credit-actions">
        <button class="sign-in-btn" @click="signIn" :disabled="hasSignedIn">
          <i class="fa fa-calendar-check"></i>
          {{ hasSignedIn ? '今日已签到' : '签到' }}
        </button>
        <button class="exchange-btn" @click="showExchangeDialog">
          <i class="fa fa-exchange-alt"></i>
          兑换
        </button>
      </div>
    </div>

    <!-- 积分明细标签页区域 -->
    <div class="credit-tabs">
      <!-- 标签页头部 -->
      <div class="tab-header">
        <div 
          v-for="tab in tabs" 
          :key="tab.id"
          :class="['tab-item', { active: currentTab === tab.id }]"
          @click="currentTab = tab.id"
        >
          {{ tab.name }}
        </div>
      </div>
      
      <!-- 标签页内容区域 -->
      <div class="tab-content">
        <!-- 加载状态显示 -->
        <div v-if="loading" class="loading">
          <p>加载中...</p>
        </div>
        
        <!-- 空数据状态显示 -->
        <div v-else-if="currentList.length === 0" class="empty-list">
          <p>暂无记录</p>
        </div>
        
        <!-- 积分记录列表 -->
        <div v-else class="credit-list">
          <div v-for="(item, index) in currentList" :key="index" class="credit-item">
            <div class="credit-info">
              <h3>{{ getCreditTitle(item) }}</h3>
              <p class="credit-time">{{ formatDate(item.createTime) }}</p>
            </div>
            <div class="credit-value" :class="{ positive: item.credit > 0 }">
              {{ item.credit > 0 ? '+' : '' }}{{ item.credit }}
            </div>
          </div>
        </div>

        <!-- 加载更多按钮 -->
        <div v-if="hasMore" class="load-more" @click="loadMore">
          加载更多
        </div>
      </div>
    </div>

    <!-- 积分兑换弹窗 -->
    <div v-if="showExchange" class="exchange-dialog">
      <div class="dialog-content">
        <h3>积分兑换</h3>
        <p class="exchange-rate">兑换比例：100积分 = 1元</p>
        <div class="exchange-input">
          <input 
            type="number" 
            v-model="exchangeAmount" 
            placeholder="请输入要兑换的积分"
            min="100"
            step="100"
          >
        </div>
        <div class="exchange-buttons">
          <button @click="showExchange = false">取消</button>
          <button @click="exchangeCredit" :disabled="!canExchange">确认兑换</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';

export default {
  name: 'Credit',
  setup() {
    // 用户信息和基础数据
    const user = ref(JSON.parse(sessionStorage.getItem('user'))); // 从sessionStorage获取用户信息
    const totalCredit = ref(0);                                   // 总积分
    const hasSignedIn = ref(false);                              // 今日是否已签到
    const loading = ref(false);                                  // 加载状态
    const currentTab = ref('all');                               // 当前选中的标签页
    const currentPage = ref(1);                                  // 当前页码
    const pageSize = ref(10);                                    // 每页显示数量
    const hasMore = ref(true);                                   // 是否有更多数据
    const showExchange = ref(false);                             // 是否显示兑换弹窗
    const exchangeAmount = ref('');                              // 兑换积分数量

    // 标签页配置：定义不同类型的积分记录及其对应的API
    const tabs = [
      { id: 'all', name: '全部', api: 'listEfficientCreditById' },
      { id: 'get', name: '获取记录', api: 'listEfficientCreditById' },
      { id: 'exchange', name: '兑换记录', api: 'listEfficientCreditById' },
      { id: 'expired', name: '已过期', api: 'listExpiredCreditById' }
    ];

    // 各标签页的数据存储
    const creditLists = ref({
      all: [],
      get: [],
      exchange: [],
      expired: []
    });

    // 计算属性：获取当前标签页的数据列表
    const currentList = computed(() => {
      const list = creditLists.value[currentTab.value] || [];
      console.log('当前标签页:', currentTab.value);
      console.log('标签页原始数据:', list);
      
      // 根据标签类型筛选数据
      let filteredList;
      if (currentTab.value === 'get') {
        filteredList = list.filter(item => item.credit > 0);
      } else if (currentTab.value === 'exchange') {
        filteredList = list.filter(item => item.credit < 0);
      } else {
        filteredList = list;
      }
      
      console.log('筛选后的数据:', filteredList);
      return filteredList;
    });

    // 计算属性：判断是否可以进行积分兑换
    const canExchange = computed(() => {
      const amount = Number(exchangeAmount.value);
      return amount >= 100 && amount <= totalCredit.value && amount % 100 === 0;
    });

    // 获取用户总积分
    const fetchTotalCredit = async () => {
      try {
        console.log('正在获取用户积分...');
        const response = await axios.get(`plus/CreditController/getCreditById?userId=${user.value.userId}`);
        console.log('获取积分响应:', response.data);
        
        if (response.data.code === 200) {
          const creditValue = response.data.result;
          console.log('原始积分值:', creditValue, '类型:', typeof creditValue);
          
          // 确保积分值是数字
          totalCredit.value = creditValue != null ? Number(creditValue) : 0;
          console.log('设置后的积分值:', totalCredit.value);
        } else {
          console.error('获取积分失败:', response.data.message);
          ElMessage.error('获取积分失败');
          totalCredit.value = 0;
        }
      } catch (error) {
        console.error('获取积分时发生错误:', error);
        if (error.response) {
          console.error('错误响应:', error.response.data);
          ElMessage.error(`获取积分失败: ${error.response.data.message || '未知错误'}`);
        } else {
          ElMessage.error('获取积分失败，请检查网络连接');
        }
        totalCredit.value = 0;
      }
    };

    // 检查用户今日是否已签到
    const checkSignIn = async () => {
      try {
        const response = await axios.get(`plus/CreditController/signInOrNot?userId=${user.value.userId}`);
        hasSignedIn.value = response.data.result === 1;
      } catch (error) {
        console.error('检查签到状态失败:', error);
      }
    };

    // 执行签到操作
    const signIn = async () => {
      if (hasSignedIn.value) return;
      
      try {
        const response = await axios.post(`plus/CreditController/addCreditByDateById?userId=${user.value.userId}`);
        if (response.data.code === 200) {
          hasSignedIn.value = true;
          totalCredit.value = response.data.result;
          // 刷新积分记录
          await loadCreditList('get', true);
          await loadCreditList('all', true);
        }
      } catch (error) {
        console.error('签到失败:', error);
      }
    };

    // 加载积分记录列表
    const loadCreditList = async (tab, refresh = false) => {
      if (loading.value) return;
      
      const currentTab = tabs.find(t => t.id === tab);
      if (!currentTab) return;

      // 刷新时重置列表数据
      if (refresh) {
        creditLists.value[tab] = [];
        currentPage.value = 1;
        hasMore.value = true;
      }

      loading.value = true;
      try {
        // 确保从sessionStorage获取最新的用户信息
        const currentUser = JSON.parse(sessionStorage.getItem('user'));
        if (!currentUser || !currentUser.userId) {
          console.error('No user information found');
          return;
        }

        console.log('Requesting with userId:', currentUser.userId); // 调试日志

        const response = await axios.get(`plus/CreditController/${currentTab.api}`, {
          params: {
            userId: currentUser.userId,
            pageNum: currentPage.value,
            pageSize: pageSize.value
          }
        });

        console.log('API Response:', response.data);

        if (response.data.code === 200) {
          const newList = response.data.result || [];
          console.log('Credit list data:', newList);
          console.log('First item in list:', newList[0]);
          
          creditLists.value[tab] = refresh 
            ? newList 
            : [...creditLists.value[tab], ...newList];
          
          // 判断是否还有更多数据
          hasMore.value = newList.length === pageSize.value;
          if (hasMore.value) {
            currentPage.value++;
          }
        }
      } catch (error) {
        console.error('加载积分列表失败:', error);
      } finally {
        loading.value = false;
      }
    };

    // 加载更多数据
    const loadMore = () => {
      if (hasMore.value && !loading.value) {
        loadCreditList(currentTab.value);
      }
    };

    // 执行积分兑换
    const exchangeCredit = async () => {
      if (!canExchange.value) return;
      
      try {
        const user = JSON.parse(sessionStorage.getItem('user'))
        if (!user) {
          ElMessage.warning('请先登录')
          router.push('/login')
          return
        }

        const headers = {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('token')}` || ''
        }

        console.log('开始积分兑换...')
        console.log('用户ID:', user.userId)
        console.log('积分数量:', exchangeAmount.value)
        console.log('请求头:', headers)

        const response = await axios.post(
          '/plus/CreditController/credit2money',
          null,
          {
            headers,
          params: {
              userId: user.userId,
              credit: exchangeAmount.value
            },
            withCredentials: true
          }
        )

        console.log('积分兑换响应:', response.data)

        if (response.data.code === 200) {
          totalCredit.value = response.data.result
          showExchange.value = false
          exchangeAmount.value = ''
          ElMessage.success('兑换成功')
          // 刷新积分记录
          await loadCreditList('exchange', true)
          await loadCreditList('all', true)
        } else {
          console.error('兑换失败:', response.data.message)
          ElMessage.error(response.data.message || '兑换失败')
        }
      } catch (error) {
        console.error('兑换时发生错误:', error)
        if (error.response) {
          console.error('错误响应:', error.response.data)
          ElMessage.error(`兑换失败: ${error.response.data.message || '未知错误'}`)
        } else {
          ElMessage.error('兑换失败，请检查网络连接')
        }
      }
    }

    // 日期格式化工具函数
    const formatDate = (dateString) => {
      // 添加调试日志
      console.log('formatDate input:', dateString, typeof dateString);
      
      if (!dateString) {
        console.warn('Received null or empty date');
        return '';
      }

      try {
        // 尝试解析不同格式的日期字符串
        let date;
        if (typeof dateString === 'string') {
          // 如果是ISO格式的字符串（带T的）
          if (dateString.includes('T')) {
            date = new Date(dateString);
          } else {
            // 如果是普通的日期时间字符串
            const [datePart, timePart] = dateString.split(' ');
            if (timePart) {
              date = new Date(`${datePart}T${timePart}`);
            } else {
              date = new Date(dateString);
            }
          }
        } else {
          date = new Date(dateString);
        }

        // 验证日期是否有效
        if (isNaN(date.getTime())) {
          console.warn('Invalid date:', dateString);
          return '';
        }

        // 格式化日期
        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        
        return `${year}-${month}-${day} ${hours}:${minutes}`;
      } catch (error) {
        console.error('Date formatting error:', error);
        return '';
      }
    };

    // 获取积分项目标题
    const getCreditTitle = (item) => {
      // 详细的调试输出
      console.log('Credit item complete object:', JSON.stringify(item, null, 2));
      
      // 根据credit的正负值判断类型
      if (item.credit > 0) {
        return '积分获得';
      } else if (item.credit < 0) {
        return '积分兑换';
      }
      return '其他';
    };

    // 显示兑换弹窗
    const showExchangeDialog = () => {
      showExchange.value = true;
      exchangeAmount.value = '';
    };

    // 监听标签页切换
    watch(currentTab, (newTab) => {
      console.log('标签页切换到:', newTab);
      // 如果该标签页没有数据，则加载数据
      if (!creditLists.value[newTab] || creditLists.value[newTab].length === 0) {
        console.log('加载新标签页数据');
        loadCreditList(newTab, true);
      }
    });

    // 组件挂载时执行的初始化操作
    onMounted(async () => {
      if (!user.value) {
        alert('请先登录');
        router.push('/login');
        return;
      }

      await fetchTotalCredit();    // 获取总积分
      await checkSignIn();         // 检查签到状态
      await loadCreditList('all', true); // 加载积分记录
      
      // 初始化时加载当前标签页的数据
      if (currentTab.value !== 'all') {
        await loadCreditList(currentTab.value, true);
      }
    });

    // 返回模板中需要使用的数据和方法
    return {
      totalCredit,
      hasSignedIn,
      loading,
      currentTab,
      tabs,
      currentList,
      hasMore,
      showExchange,
      exchangeAmount,
      canExchange,
      signIn,
      loadMore,
      formatDate,
      getCreditTitle,
      showExchangeDialog,
      exchangeCredit
    };
  }
};
</script>

<style scoped>
/* 页面整体样式 */
.wrapper {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-top: 12vw;
}

/* 顶部导航栏样式 */
header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 12vw;
  background-color: #0097FF;
  color: white;
  display: flex;
  align-items: center;
  padding: 0 4vw;
  z-index: 1000;
}

/* 返回按钮样式 */
.back-button {
  font-size: 5vw;
  padding: 2vw;
  cursor: pointer;
}

/* 标题样式 */
header p {
  flex: 1;
  text-align: center;
  font-size: 4.5vw;
  margin-right: 7vw;
}

/* 积分概览区域样式 */
.credit-overview {
  background-color: white;
  padding: 6vw 4vw;
  margin-bottom: 3vw;
}

/* 积分数量显示样式 */
.credit-amount {
  text-align: center;
  margin-bottom: 4vw;
}

.credit-amount h2 {
  font-size: 8vw;
  color: #0097FF;
  margin: 0;
}

.credit-amount p {
  font-size: 3.5vw;
  color: #666;
  margin: 2vw 0 0;
}

/* 操作按钮区域样式 */
.credit-actions {
  display: flex;
  justify-content: center;
  gap: 4vw;
}

.credit-actions button {
  padding: 2vw 6vw;
  border: none;
  border-radius: 1vw;
  font-size: 3.5vw;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 1vw;
}

/* 签到按钮样式 */
.sign-in-btn {
  background-color: #0097FF;
  color: white;
}

.sign-in-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 兑换按钮样式 */
.exchange-btn {
  background-color: #ff6b00;
  color: white;
}

/* 标签页区域样式 */
.credit-tabs {
  background-color: white;
}

/* 标签页头部样式 */
.tab-header {
  display: flex;
  border-bottom: 1px solid #eee;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 3vw 0;
  font-size: 3.5vw;
  color: #666;
  cursor: pointer;
}

.tab-item.active {
  color: #0097FF;
  border-bottom: 2px solid #0097FF;
}

/* 积分记录列表样式 */
.credit-list {
  padding: 0 4vw;
}

/* 积分记录项样式 */
.credit-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 3vw 0;
  border-bottom: 1px solid #f5f5f5;
}

.credit-info h3 {
  font-size: 3.8vw;
  color: #333;
  margin: 0;
}

.credit-time {
  font-size: 3vw;
  color: #999;
  margin: 1vw 0 0;
}

/* 积分数值样式 */
.credit-value {
  font-size: 4vw;
  color: #ff6b00;
}

.credit-value.positive {
  color: #0097FF;
}

/* 加载更多按钮样式 */
.load-more {
  text-align: center;
  padding: 3vw;
  color: #666;
  font-size: 3.5vw;
  cursor: pointer;
}

/* 兑换弹窗样式 */
.exchange-dialog {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1001;
}

/* 弹窗内容样式 */
.dialog-content {
  width: 80%;
  background-color: white;
  border-radius: 2vw;
  padding: 4vw;
}

.dialog-content h3 {
  text-align: center;
  font-size: 4.5vw;
  margin: 0 0 4vw;
}

/* 兑换比例说明样式 */
.exchange-rate {
  text-align: center;
  font-size: 3.5vw;
  color: #666;
  margin-bottom: 4vw;
}

/* 兑换输入框样式 */
.exchange-input {
  margin-bottom: 4vw;
}

.exchange-input input {
  width: 100%;
  padding: 2vw;
  font-size: 4vw;
  border: 1px solid #ddd;
  border-radius: 1vw;
  text-align: center;
}

/* 兑换弹窗按钮样式 */
.exchange-buttons {
  display: flex;
  gap: 3vw;
}

.exchange-buttons button {
  flex: 1;
  padding: 2vw;
  border: none;
  border-radius: 1vw;
  font-size: 3.8vw;
  cursor: pointer;
}

.exchange-buttons button:first-child {
  background-color: #f5f5f5;
  color: #666;
}

.exchange-buttons button:last-child {
  background-color: #0097FF;
  color: white;
}

.exchange-buttons button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 加载和空状态样式 */
.loading, .empty-list {
  text-align: center;
  padding: 6vw;
  color: #666;
  font-size: 3.5vw;
}
</style> 