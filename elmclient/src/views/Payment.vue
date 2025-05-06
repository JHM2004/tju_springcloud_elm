<template>
	<div class="wrapper">
	  <!-- header部分 -->
	  <header>
		<p>在线支付</p>
	  </header>
  
	  <!-- 加载中提示 -->
	  <div v-if="loading" class="loading">
		<p>加载中...</p>
	  </div>
  
	  <template v-else>
		<div class="content">
		  <!-- 订单信息部分 -->
		  <div class="section order-section" v-if="orders">
			<div class="section-header">
			  <h3>订单信息</h3>
			  <span class="total-amount">&#165;{{ orders.orderTotal || '0.00' }}</span>
			</div>
			<div class="merchant-info" @click="detailetShow">
			  <img :src="orders.business?.businessImg" :alt="orders.business?.businessName" class="merchant-logo">
			  <div class="merchant-name">
				{{ orders.business?.businessName || '未知商家' }}
				<i class="fa fa-angle-down" :class="{ 'rotate': isShowDetailet }"></i>
			  </div>
			</div>
  
			<!-- 订单明细部分 -->
			<div class="order-details" v-show="isShowDetailet">
			  <template v-if="orderDetails.length > 0">
				<div class="detail-item" v-for="item in orderDetails" :key="item.odId">
				  <span class="item-name">{{ item.foodName || '未知商品' }} × {{ item.quantity || 0 }}</span>
				  <span class="item-price">&#165;{{ (item.priceAtThatTime * item.quantity).toFixed(2) }}</span>
				</div>
			  </template>
			  <div class="detail-item delivery-fee">
				<span>配送费</span>
				<span>&#165;{{ orders.business?.deliveryPrice || '0.00' }}</span>
			  </div>
			</div>
		  </div>
  
		  <!-- 支付方式部分 -->
		  <div class="section payment-section">
			<h3>选择支付方式</h3>
			<div class="payment-options">
			  <div class="payment-option" :class="{ active: paymentMethod === 'alipay' }" @click="paymentMethod = 'alipay'">
				<img src="../assets/alipay.png" alt="支付宝支付">
				<i class="fas fa-check-circle"></i>
			  </div>
			  <div class="payment-option" :class="{ active: paymentMethod === 'wechat' }" @click="paymentMethod = 'wechat'">
				<img src="../assets/wechat.png" alt="微信支付">
				<i class="fas fa-check-circle"></i>
			  </div>
			  <div class="payment-option" :class="{ active: paymentMethod === 'wallet' }" @click="paymentMethod = 'wallet'">
				<i class="fas fa-wallet payment-icon"></i>
				<i class="fas fa-check-circle"></i>
				<span class="wallet-balance">余额: ¥{{ walletBalance }}</span>
			  </div>
			</div>
		  </div>
  
		  <!-- 支付按钮 -->
		  <div class="payment-action">
			<button 
			  class="pay-button" 
			  @click="handlePayment" 
			  :disabled="!isOrderValid">
			  确认支付 &#165;{{ orders?.orderTotal || '0.00' }}
			</button>
		  </div>
		</div>
	  </template>
  
	  <!-- 底部菜单部分 -->

	</div>
  </template>
  
  <script>
import { ref, onBeforeMount, onUnmounted, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Footer from '../components/Footer.vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
  
  export default {
	name: 'Payment',
	setup() {
	
	  const orders = ref(null);
	  const orderDetails = ref([]);
	  const isShowDetailet = ref(false);
	  const route = useRoute();
	  const router = useRouter();
	  const orderId = ref(route.query.orderId);
	  const loading = ref(true);
	  const paymentMethod = ref('alipay');
	  const walletBalance = ref(0);
	  
	  // 计算属性：检查订单是否有效
	  const isOrderValid = computed(() => {
		return !loading.value && orders.value && orders.value.orderId;
	  });
  
	  // 获取订单详情
	  const fetchOrderDetails = async () => {
		try {
		  const orderParam = {
			orderId: orderId.value
		  };
		  const response = await axios.post('OrdersController/listOrderDetailetByOrderId', orderParam);
		  if (response.data && response.data.result) {
			orderDetails.value = response.data.result;
		  } else if (response.data && Array.isArray(response.data)) {
			orderDetails.value = response.data;
		  } else {
			orderDetails.value = [];
		  }
		} catch (error) {
		  console.error('获取订单详情失败:', error);
		  orderDetails.value = [];
		}
	  };
  
	  // 初始化订单数据
	  const initializeOrder = async () => {
		if (!orderId.value) {
		  alert('订单信息有误，请重试！');
		  router.push({ path: '/index' });
		  return;
		}
  
		loading.value = true;
		try {
		  const response = await axios.get(`OrdersController/getOrdersById/${orderId.value}`);
  
		  // 检查返回数据结构并正确提取orders数据
		  if (response.data) {
			// 如果数据在result字段中
			if (response.data.result) {
			  orders.value = response.data.result;
			} 
			// 如果数据直接在response.data中
			else if (response.data.orderId) {
			  orders.value = response.data;
			} 
			else {
			  throw new Error('未获取到订单数据');
			}
			
			// 获取商家信息
			if (orders.value && orders.value.businessId) {
			  try {
				// 确保businessId是数字并且有效
				const businessId = parseInt(orders.value.businessId);
				if (!isNaN(businessId) && businessId > 0) {
				  const businessResponse = await axios.get(`BusinessController/getBusinessById/${businessId}`);
				  if (businessResponse.data && businessResponse.data.result) {
					orders.value.business = businessResponse.data.result;
				  } else if (businessResponse.data) {
					orders.value.business = businessResponse.data;
				  }
				} else {
				  console.warn('订单中的商家ID无效:', orders.value.businessId);
				  // 设置默认商家信息
				  orders.value.business = {
					businessName: '未知商家',
					businessImg: 'https://via.placeholder.com/50',
					deliveryPrice: 0
				  };
				}
			  } catch (error) {
				console.error('获取商家信息失败:', error);
				// 设置默认商家信息
				orders.value.business = {
				  businessName: '未知商家',
				  businessImg: 'https://via.placeholder.com/50',
				  deliveryPrice: 0
				};
			  }
			} else {
			  console.warn('订单中没有商家ID');
			  // 设置默认商家信息
			  orders.value.business = {
				businessName: '未知商家',
				businessImg: 'https://via.placeholder.com/50',
				deliveryPrice: 0
			  };
			}
			
			// 获取订单详情
			await fetchOrderDetails();
		  } else {
			throw new Error('未获取到订单数据');
		  }
		} catch (error) {
		  console.error('获取订单信息失败:', error);
		  alert('获取订单信息失败，请重试！');
		  router.push({ path: '/index' });
		} finally {
		  loading.value = false;
		}
	  };
  
	  onBeforeMount(async () => {
		await initializeOrder();
		history.pushState(null, null, document.URL);
		window.onpopstate = () => router.push({ path: '/index' });
		await getWalletBalance();
	  });
  
	  const detailetShow = () => {
		isShowDetailet.value = !isShowDetailet.value;
	  };
  
	  const getWalletBalance = async () => {
		try {
		  const userInfo = JSON.parse(sessionStorage.getItem('user'));
		  if (!userInfo || !userInfo.userId) {
			console.error('未找到用户信息');
			return;
		  }
		  console.log('当前用户ID:', userInfo.userId);
		  
		  const headers = {
			'Content-Type': 'application/json',
			'Authorization': `Bearer ${sessionStorage.getItem('token')}` || ''
		  };
		  
		  const response = await axios.get(`/plus/VirtualWalletController/getBalanceById?userId=${userInfo.userId}`, {
			headers,
			withCredentials: true
		  });
		  console.log('钱包余额响应:', response.data);
		  walletBalance.value = response.data.result;
		} catch (error) {
		  console.error('获取钱包余额失败:', error);
		}
	  };
  
	  const handlePayment = async () => {
		if (!isOrderValid.value) {
		  alert('订单信息有误，请重试！');
		  return;
		}
  
		try {
		  if (paymentMethod.value === 'wallet') {
			const userInfo = JSON.parse(sessionStorage.getItem('user'));
			if (!userInfo || !userInfo.userId) {
			  console.error('未找到用户信息');
			  return;
			}
  
			const headers = {
				'Content-Type': 'application/json',
				'Authorization': `Bearer ${sessionStorage.getItem('token')}` || ''
			};
  
			// 检查钱包状态
			try {
			  const freezeResponse = await axios.get(`/plus/VirtualWalletController/getFreeze?userId=${userInfo.userId}`, {
				headers,
				withCredentials: true
			  });
			  
			  if (freezeResponse.data.code === 200 && freezeResponse.data.result === 1) {
				ElMessage.error('钱包已被冻结，无法使用虚拟钱包支付');
				return;
			  }
			} catch (error) {
			  console.error('检查钱包状态失败:', error);
			  ElMessage.error('检查钱包状态失败，请稍后重试');
			  return;
			}
  
			if (walletBalance.value < orders.value.orderTotal) {
			  ElMessage.warning('余额不足，请选择其他支付方式');
			  return;
			}
			
			// 确保商家钱包存在
			const businessId = orders.value.business.businessId.toString();
			try {
			await axios.get(`/plus/VirtualWalletController/addWalletById?userId=${businessId}`, {
				headers,
				withCredentials: true
			});
			} catch (error) {
			  console.error('确保商家钱包存在时发生错误:', error);
			  ElMessage.error('支付失败，请稍后重试');
			  return;
			}
			
			// 执行支付
			try {
			  const payResponse = await axios.post('/plus/VirtualWalletController/pay', {
			  userId: userInfo.userId,
			  amount: orders.value.orderTotal,
				targetId: businessId
			}, { headers, withCredentials: true });
  
			  if (payResponse.data.code !== 200 || payResponse.data.result === 0) {
				ElMessage.error('支付失败，请检查钱包状态或选择其他支付方式');
				return;
			  }

			// 更新订单状态
			const payData = {
			  orderId: Number(orderId.value)
			};
			await axios.post('OrdersController/payOk', payData);
  
			// 更新钱包余额
			await getWalletBalance();
  
			  // 支付成功后跳转
			router.push({ 
				path: '/successfulPayment', 
				query: { orderId: orderId.value }
			});
			} catch (error) {
			  console.error('支付过程中发生错误:', error);
			  ElMessage.error('支付失败，请稍后重试');
			}
		  } else {
			// 其他支付方式
		  const payData = {
			orderId: Number(orderId.value)
		  };
		  await axios.post('OrdersController/payOk', payData);
			router.push({ 
				path: '/successfulPayment', 
				query: { orderId: orderId.value }
			});
		  }
		} catch (error) {
		  console.error('支付失败:', error);
		  ElMessage.error('支付失败，请重试！');
		}
	  };
  
	  onUnmounted(() => {
		window.onpopstate = null;
	  });
  
	  return {
		orderId,
		orders,
		orderDetails,
		isShowDetailet,
		detailetShow,
		handlePayment,
		isOrderValid,
		loading,
		paymentMethod,
		walletBalance,
		getWalletBalance
	  };
	},
	components: {
	  Footer
	}
  }
  </script>
  
  <style scoped>
		 /****************** 总容器 ******************/
		 .wrapper {
		 	min-height: 100vh;
		 	background-color: #f5f7fa;
		 }
	
		 /****************** header部分 ******************/
		 .wrapper header {
		 	width: 100%;
		 	height: 12vw;
		 	background-color: #0097FF;
		 	color: #fff;
		 	font-size: 4.8vw;
		 	position: fixed;
		 	left: 0;
		 	top: 0;
		 	z-index: 1000;
		 	display: flex;
		 	justify-content: center;
		 	align-items: center;
		 }
	
		 .content {
		 	padding-top: 14vw;
		 	padding-bottom: 32vw;
		 }
	
		 .section {
		 	background: white;
		 	border-radius: 3vw;
		 	margin: 3vw;
		 	padding: 4vw;
		 	box-shadow: 0 0.2vw 1vw rgba(0, 0, 0, 0.05);
		 }
	
		 .section-header {
		 	display: flex;
		 	justify-content: space-between;
		 	align-items: center;
		 	margin-bottom: 4vw;
		 }
	
		 .section-header h3 {
		 	font-size: 4.2vw;
		 	color: #333;
		 	font-weight: 500;
		 	margin: 0;
		 }
	
		 .total-amount {
		 	font-size: 5vw;
		 	color: #ff6b00;
		 	font-weight: bold;
		 }
	
		 .merchant-info {
		 	display: flex;
		 	align-items: center;
		 	padding: 3vw 0;
		 	cursor: pointer;
		 }
	
		 .merchant-logo {
		 	width: 12vw;
		 	height: 12vw;
		 	border-radius: 2vw;
		 	object-fit: cover;
		 	margin-right: 3vw;
		 }
	
		 .merchant-name {
		 	flex: 1;
		 	font-size: 4vw;
		 	color: #333;
		 	display: flex;
		 	align-items: center;
		 	gap: 2vw;
		 }
	
		 .fa-angle-down {
		 	transition: transform 0.3s ease;
		 }
	
		 .fa-angle-down.rotate {
		 	transform: rotate(180deg);
		 }
	
		 .order-details {
		 	margin-top: 3vw;
		 	padding-top: 3vw;
		 	border-top: 0.2vw solid #f5f7fa;
		 }
	
		 .detail-item {
		 	display: flex;
		 	justify-content: space-between;
		 	align-items: center;
		 	padding: 2vw 0;
		 	font-size: 3.6vw;
		 	color: #666;
		 }
	
		 .delivery-fee {
		 	border-top: 0.2vw dashed #eee;
		 	margin-top: 2vw;
		 	padding-top: 2vw;
		 	color: #333;
		 }
	
		 .payment-options {
		 	display: flex;
		 	flex-wrap: wrap;
		 	gap: 2vw;
		 	margin-top: 4vw;
		 }
	
		 .payment-option {
		 	flex: 1 1 28%;
		 	min-width: 28%;
		 	max-width: calc(33.33% - 2vw);
		 	padding: 3vw;
		 	border: 0.2vw solid #eee;
		 	border-radius: 2vw;
		 	display: flex;
		 	align-items: center;
		 	position: relative;
		 	cursor: pointer;
		 	transition: all 0.3s ease;
		 	background: #f9f9f9;
		 }
	
		 .payment-option img,
		 .payment-icon {
		 	height: 6vw;
		 	width: auto;
		 	min-width: 6vw;
		 	object-fit: contain;
		 	margin-right: 2vw;
		 }
	
		 .payment-icon {
		 	font-size: 6vw;
		 	color: #666;
		 }
	
		 .wallet-balance {
		 	position: absolute;
		 	bottom: 1vw;
		 	left: 3vw;
		 	right: 3vw;
		 	font-size: 2.8vw;
		 	color: #666;
		 	text-align: center;
		 	white-space: nowrap;
		 	overflow: hidden;
		 	text-overflow: ellipsis;
		 }
	
		 .payment-option .fa-check-circle {
		 	position: absolute;
		 	top: 3vw;
		 	right: 3vw;
		 	font-size: 4vw;
		 	color: #ddd;
		 	transition: all 0.3s ease;
		 }
	
		 .payment-option.active {
		 	border-color: #38CA73;
		 	background: #f0fff5;
		 	padding-bottom: 7vw;
		 }
	
		 .payment-option.active .fa-check-circle {
		 	color: #38CA73;
		 }
	
		 .payment-option.active .payment-icon {
		 	color: #38CA73;
		 }
	
		 .payment-action {
		 	position: fixed;
		 	bottom: 0;
		 	left: 0;
		 	right: 0;
		 	padding: 4vw;
		 	background: white;
		 	box-shadow: 0 -0.2vw 1vw rgba(0, 0, 0, 0.05);
		 }
	
		 .pay-button {
		 	width: 100%;
		 	height: 12vw;
		 	border: none;
		 	border-radius: 6vw;
		 	background: linear-gradient(to right, #38CA73, #2EAF62);
		 	color: white;
		 	font-size: 4.2vw;
		 	font-weight: bold;
		 	display: flex;
		 	align-items: center;
		 	justify-content: center;
		 	cursor: pointer;
		 	transition: all 0.3s ease;
		 }
	
		 .pay-button:disabled {
		 	background: #ccc;
		 	cursor: not-allowed;
		 }
	
		 .pay-button:not(:disabled):active {
		 	transform: scale(0.98);
		 }
  
		 .loading {
		 	width: 100%;
		 	height: 100vh;
		 	display: flex;
		 	justify-content: center;
		 	align-items: center;
		 	font-size: 4vw;
		 	color: #666;
		 }
  
</style>