<template>
  <div class="container">
    <!-- 顶部导航栏 -->
    <header>
      <div class="back-button" @click="goBack">
        <i class="fa fa-arrow-left"></i>
      </div>
      <h1>支付成功</h1>
    </header>

    <!-- 主要内容区域 -->
    <div class="content">
      <!-- 成功图标 -->
      <div class="success-icon">
        <img :src="myimage" alt="支付成功">
      </div>

      <!-- 商家信息 -->
      <div class="merchant-info">
        <img :src="paymentDetails.merchantLogo" :alt="paymentDetails.merchantName" class="merchant-logo">
        <h2>{{ paymentDetails.merchantName }}</h2>
      </div>

      <!-- 支付详情 -->
      <div class="payment-details">
        <div class="amount">
          <span class="label">支付金额</span>
          <span class="value">¥{{ paymentDetails.amount }}</span>
        </div>
        <div class="time">
          <span class="label">支付时间</span>
          <span class="value">{{ paymentDetails.paymentTime }}</span>
    </div>
    </div>

      <!-- 底部按钮 -->
      <div class="action-buttons">
        <button class="primary-btn" @click="goBack">返回首页</button>
        <button class="secondary-btn" @click="viewOrder">查看订单</button>
    </div>
    </div>
  </div>
</template>

<script>
import { onBeforeMount, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import myimage from '/src/assets/R-C.png';
export default {
  setup() {
    const route = useRoute();
    const orderId = ref(route.query.orderId); // 获取路由参数中的 orderId
    const router = useRouter();
    const paymentDetails = ref({});
    const orders = ref({});
    const business = ref({});
    const goBack = () => {
      router.push('/index'); // 返回首页
    };

    const viewOrder = () => {
      router.push({
        path: '/orderList',
        query: { orderId: orderId.value }
      });
    };

    onBeforeMount(async () => {
      try {
        // 获取订单信息
        const orderResponse = await axios.get(`OrdersController/getOrdersById/${orderId.value}`);
        
        // 处理可能的数据结构
        if (orderResponse.data && orderResponse.data.result) {
          orders.value = orderResponse.data.result;
        } else if (orderResponse.data && orderResponse.data.orderId) {
          orders.value = orderResponse.data;
        } else {
          throw new Error('未获取到订单数据');
        }
        
        console.log('订单信息:', orders.value);
        
        // 获取商家信息
        if (orders.value && orders.value.businessId) {
          try {
            // 确保businessId是数字并且有效
            const businessId = parseInt(orders.value.businessId);
            if (!isNaN(businessId) && businessId > 0) {
              const businessResponse = await axios.get(`BusinessController/getBusinessById/${businessId}`);
              
              // 处理可能的数据结构
              if (businessResponse.data && businessResponse.data.result) {
                business.value = businessResponse.data.result;
              } else if (businessResponse.data && businessResponse.data.businessId) {
                business.value = businessResponse.data;
              } else {
                throw new Error('未获取到商家数据');
              }
            } else {
              console.warn('订单中的商家ID无效:', orders.value.businessId);
              business.value = { 
                businessName: '未知商家',
                businessImg: 'https://via.placeholder.com/50'
              };
            }
            
            console.log('商家信息:', business.value);
          } catch (error) {
            console.error('获取商家信息失败:', error);
            business.value = { 
              businessName: '未知商家',
              businessImg: 'https://via.placeholder.com/50'
            };
          }
        } else {
          console.warn('订单中没有商家ID');
          business.value = { 
            businessName: '未知商家',
            businessImg: 'https://via.placeholder.com/50'
          };
        }

        // 准备支付详情数据
        paymentDetails.value = {
          merchantLogo: business.value.businessImg || 'https://via.placeholder.com/50',
          amount: orders.value.orderTotal || '0.00',
          paymentTime: orders.value.orderDate || new Date().toLocaleString(),
          merchantName: business.value.businessName || '未知商家'
        };

      } catch (error) {
        console.error('获取订单信息失败:', error);
        // 设置默认支付详情
        paymentDetails.value = {
          merchantLogo: 'https://via.placeholder.com/50',
          amount: '0.00',
          paymentTime: new Date().toLocaleString(),
          merchantName: '未知商家'
        };
      }
    });


    return {
      paymentDetails,
      myimage,
      goBack,
      viewOrder
    };
  }
};


</script>

<style scoped>
.container {
  min-height: 100vh;
  background-color: #f8f9fa;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 12vw;
  background: linear-gradient(135deg, #42b983 0%, #38ca73 100%);
  color: white;
  display: flex;
  align-items: center;
  padding: 0 4vw;
  z-index: 100;
  box-shadow: 0 0.5vw 2vw rgba(0, 0, 0, 0.1);
}

.back-button {
  font-size: 5vw;
  padding: 2vw;
  cursor: pointer;
}

header h1 {
  flex: 1;
  text-align: center;
  font-size: 4.5vw;
  margin-right: 7vw;
  font-weight: 500;
}

/* 主要内容区域 */
.content {
  margin-top: 12vw;
  padding: 6vw 4vw;
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

/* 成功图标 */
.success-icon {
  width: 20vw;
  height: 20vw;
  margin-bottom: 6vw;
  animation: bounce-in 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

@keyframes bounce-in {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}

.success-icon img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

/* 商家信息 */
.merchant-info {
  text-align: center;
  margin-bottom: 6vw;
}

.merchant-logo {
  width: 16vw;
  height: 16vw;
  border-radius: 2vw;
  margin-bottom: 2vw;
  box-shadow: 0 0.5vw 2vw rgba(0, 0, 0, 0.1);
  object-fit: cover;
}

.merchant-info h2 {
  font-size: 4.5vw;
  color: #333;
  margin: 0;
  font-weight: 500;
}

/* 支付详情 */
.payment-details {
  width: 100%;
  background-color: white;
  border-radius: 3vw;
  padding: 4vw;
  margin-bottom: 6vw;
  box-shadow: 0 0.5vw 2vw rgba(0, 0, 0, 0.05);
}

.payment-details .amount,
.payment-details .time {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2vw 0;
}

.payment-details .amount {
  border-bottom: 0.2vw solid #f5f5f5;
}

.payment-details .label {
  color: #666;
  font-size: 3.8vw;
}

.payment-details .value {
  color: #333;
  font-size: 4vw;
  font-weight: 500;
}

.payment-details .amount .value {
  color: #42b983;
  font-size: 6vw;
  font-weight: bold;
}

/* 按钮区域 */
.action-buttons {
  width: 100%;
  display: flex;
  gap: 4vw;
  padding: 0 4vw;
}

.action-buttons button {
  flex: 1;
  height: 11vw;
  border: none;
  border-radius: 1.5vw;
  font-size: 4vw;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.primary-btn {
  background: linear-gradient(135deg, #42b983 0%, #38ca73 100%);
  color: white;
}

.secondary-btn {
  background-color: #f5f5f5;
  color: #666;
  border: 0.2vw solid #ddd !important;
}

.action-buttons button:active {
  transform: scale(0.98);
  opacity: 0.9;
}
</style>