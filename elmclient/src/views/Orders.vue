<template>
	<div class="wrapper">
		<!-- header部分 -->
		<header>
			<p>确认订单</p>
		</header>

		<div v-if="loading" class="loading">
			<p>加载中...</p>
		</div>

		<template v-else>
			<!-- 订单信息部分 -->
			<div class="order-info">
				<h5>订单配送至：</h5>
				<div class="order-info-address" @click="toUserAddress">
					<p>{{ deliveryaddress ? deliveryaddress.address : '请选择送货地址' }}</p>
					<i class="fa fa-angle-right"></i>
				</div>
				<p v-if="deliver.contactName">{{ deliver.contactName }} {{ deliver.contactTel }}</p>
			</div>

			<h3>{{ business.businessName }}</h3>

			<!-- 订单明细部分 -->
			<ul class="order-detailed">
				<li v-for="item in cartArr" :key="item.cartId">
					<div class="order-detailed-left">
						<img :src="getFoodImg(item)">
						<p>{{ getFoodName(item) }} x {{ item.quantity }}</p>
					</div>
					<p>&#165;{{ (getFoodPrice(item) * item.quantity).toFixed(2) }}</p>
				</li>
			</ul>
			<div class="order-deliveryfee">
				<p>配送费</p>
				<p>&#165;{{ business.deliveryPrice }}</p>
			</div>

			<!-- 合计部分 -->
			<div class="total">
				<div class="total-left">
					&#165;{{ totalPrice.toFixed(2) }}
				</div>
				<div class="total-right" @click="toPayment">
					去支付
				</div>
			</div>
		</template>
	</div>
</template>

<script>
import { ref, computed, onMounted, defineComponent } from 'vue';
import Footer from '../components/Footer.vue';
import axios from 'axios';
import { useRouter, useRoute } from 'vue-router';
import { toast } from '../utils/toast';

export default {
	name: 'Orders',
	setup() {
		const route = useRoute();
		const businessId = ref(route.query.businessId);
		const business = ref({});
		const user = ref(null);
		const cartArr = ref([]);
		const foodList = ref([]); // 存储食品数据
		const deliveryaddress = ref(null);
		const deliver = ref({});
		const router = useRouter();
		const loading = ref(true);

		onMounted(async () => {
			try {
				user.value = sessionStorage.getItem('user') ? JSON.parse(sessionStorage.getItem('user')) : null;

				if (!user.value) {
					toast.warning('用户未登录，请先登录！');
					router.push({ path: '/login' });
					return;
				}

				// 获取配送地址
				const savedAddress = localStorage.getItem(user.value.userId);
				if (savedAddress) {
					deliveryaddress.value = JSON.parse(savedAddress);
					try {
						const response = await axios.get(`DeliveryAddressController/getDeliveryAddressById/${deliveryaddress.value.daId}`);
						if (response.data) {
							deliver.value = response.data;
						} else {
							deliveryaddress.value = null;
							localStorage.removeItem(user.value.userId);
						}
					} catch (error) {
						console.error('获取配送地址详情失败:', error);
						deliveryaddress.value = null;
						localStorage.removeItem(user.value.userId);
					}
				}

				// 获取商家信息
				if (businessId.value) {
					const businessResponse = await axios.get(`BusinessController/getBusinessById/${businessId.value}`);
					if (businessResponse.data) {
						business.value = businessResponse.data.result || businessResponse.data;
					} else {
						throw new Error('商家信息获取失败');
					}
				} else {
					throw new Error('商家ID无效');
				}

				// 获取食品列表
				try {
					const foodResponse = await axios.get(`FoodController/listFoodByBusinessId/${businessId.value}`);
					const foodData = foodResponse.data.result || foodResponse.data;
					if (Array.isArray(foodData)) {
						foodList.value = foodData;
					}
				} catch (error) {
					console.error('获取食品列表失败:', error);
				}

				// 获取购物车信息
				const cartResponse = await axios.get(`CartController/listCart/${user.value.userId}/${businessId.value}`);
				if (cartResponse.data) {
					const cartData = cartResponse.data.result || cartResponse.data;
					if (Array.isArray(cartData)) {
						cartArr.value = cartData;
					}
				}

			} catch (error) {
				console.error('初始化订单数据失败:', error);
				toast.error('加载订单数据失败，请重试！');
				router.push({ path: '/index' });
			} finally {
				loading.value = false;
			}
		});

		// 根据购物车项获取食品图片
		const getFoodImg = (cartItem) => {
			if (cartItem.food && cartItem.food.foodImg) {
				return cartItem.food.foodImg;
			}
			const food = foodList.value.find(item => item.foodId === cartItem.foodId);
			return food ? food.foodImg : 'https://via.placeholder.com/150';
		};

		// 根据购物车项获取食品名称
		const getFoodName = (cartItem) => {
			if (cartItem.food && cartItem.food.foodName) {
				return cartItem.food.foodName;
			}
			const food = foodList.value.find(item => item.foodId === cartItem.foodId);
			return food ? food.foodName : '未知食品';
		};

		// 根据购物车项获取食品价格
		const getFoodPrice = (cartItem) => {
			if (cartItem.food && cartItem.food.foodPrice) {
				return cartItem.food.foodPrice;
			}
			const food = foodList.value.find(item => item.foodId === cartItem.foodId);
			return food ? food.foodPrice : 0;
		};

		const totalPrice = computed(() => {
			return cartArr.value.reduce((acc, item) => {
				const price = getFoodPrice(item);
				return acc + price * item.quantity;
			}, 0) + (business.value?.deliveryPrice || 0);
		});

		const sexFilter = (value) => value === 1 ? '先生' : '女士';

		const toUserAddress = () => {
			router.push({ path: '/userAddress', query: { businessId: businessId.value } });
		};

		const toPayment = async () => {
			try {
				// 检查用户登录状态
				if (!user.value) {
					toast.warning('请先登录！');
					router.push({ path: '/login' });
					return;
				}

				// 检查配送地址
				if (!deliveryaddress.value || !deliveryaddress.value.daId) {
					toast.warning('请选择配送地址！');
					router.push({ path: '/userAddress', query: { businessId: businessId.value } });
					return;
				}

				// 检查商家信息
				if (!businessId.value) {
					toast.error('商家信息无效，请重试！');
					router.push({ path: '/businessList' });
					return;
				}

				// 检查购物车
				if (!cartArr.value.length) {
					toast.warning('购物车为空，请先选择商品！');
					router.push({ path: '/businessInfo', query: { businessId: businessId.value } });
					return;
				}

				// 准备订单数据
				const orderData = {
					userId: user.value.userId,
					businessId: parseInt(businessId.value),
					daId: parseInt(deliveryaddress.value.daId),
					orderTotal: parseFloat(totalPrice.value.toFixed(2))  // 确保金额为数字类型
				};

				console.log('创建订单数据:', orderData);

				// 创建订单
				const response = await axios.post(
					`OrdersController/createOrders/${user.value.userId}/${businessId.value}/${deliveryaddress.value.daId}/${totalPrice.value.toFixed(2)}`,
					orderData
				);

				console.log('订单创建响应:', response.data);
				
				// 根据返回结构判断订单ID
				let orderId;
				if (response.data && response.data.code === 200) {
					orderId = response.data.result;
					console.log('成功获取订单ID:', orderId);
				} else {
					console.error('返回数据格式异常:', response.data);
					throw new Error('订单创建失败，返回数据格式异常');
				}
				
				if (orderId > 0) {
					// 清除本地购物车数据
					try {
						// 修改为循环删除每个购物车项
						for (const item of cartArr.value) {
							await axios.delete(`CartController/removeCart/${user.value.userId}/${businessId.value}/${item.foodId}`);
						}
					} catch (error) {
						console.error('清除购物车失败:', error);
					}

					toast.success('订单创建成功！');
					router.push({ path: '/payment', query: { orderId, businessId: businessId.value } });
				} else {
					throw new Error('订单创建失败，返回值异常');
				}
			} catch (error) {
				console.error('创建订单失败:', error);
				toast.error('订单创建失败，请重试！');
			}
		};

		return {
			business,
			cartArr,
			deliveryaddress,
			deliver,
			totalPrice,
			sexFilter,
			toUserAddress,
			toPayment,
			loading,
			getFoodImg,
			getFoodName,
			getFoodPrice
		};
	},
	components: {
		Footer
	}
};
</script>

<style scoped>
/****************** 总容器 ******************/
.wrapper {
	width: 100%;
	height: 100%;
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

/****************** 订单信息部分 ******************/
.wrapper .order-info {
	/*注意这里，不设置高，靠内容撑开。因为地址有可能折行*/
	width: 100%;
	margin-top: 12vw;
	background-color: #0097EF;
	box-sizing: border-box;
	padding: 2vw;
	color: #fff;
}

.wrapper .order-info h5 {
	font-size: 3vw;
	font-weight: 300;
}

.wrapper .order-info .order-info-address {
	width: 100%;
	display: flex;
	justify-content: space-between;
	align-items: center;
	font-weight: 700;
	user-select: none;
	cursor: pointer;
	margin: 1vw 0;
}

.wrapper .order-info .order-info-address p {
	width: 90%;
	font-size: 5vw;
}

.wrapper .order-info .order-info-address i {
	font-size: 6vw;
}

.wrapper .order-info p {
	font-size: 3vw;
}

.wrapper h3 {
	box-sizing: border-box;
	padding: 3vw;
	font-size: 4vw;
	color: #666;
	border-bottom: solid 1px #DDD;
}

/****************** 订单明细部分 ******************/
.wrapper .order-detailed {
	width: 100%;
}

.wrapper .order-detailed li {
	width: 100%;
	height: 16vw;
	box-sizing: border-box;
	padding: 3vw;
	color: #666;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.wrapper .order-detailed li .order-detailed-left {
	display: flex;
	align-items: center;
}

.wrapper .order-detailed li .order-detailed-left img {
	width: 10vw;
	height: 10vw;
}

.wrapper .order-detailed li .order-detailed-left p {
	font-size: 3.5vw;
	margin-left: 3vw;
}

.wrapper .order-detailed li p {
	font-size: 3.5vw;
}

.wrapper .order-deliveryfee {
	width: 100%;
	height: 16vw;
	box-sizing: border-box;
	padding: 3vw;
	color: #666;
	display: flex;
	justify-content: space-between;
	align-items: center;
	font-size: 3.5vw;
}

/****************** 订单合计部分 ******************/
.wrapper .total {
	width: 100%;
	height: 14vw;
	position: fixed;
	left: 0;
	bottom: 0;
	display: flex;
}

.wrapper .total .total-left {
	flex: 2;
	background-color: #505051;
	color: #fff;
	font-size: 4.5vw;
	font-weight: 700;
	user-select: none;
	display: flex;
	justify-content: center;
	align-items: center;
}

.wrapper .total .total-right {
	flex: 1;
	background-color: #38CA73;
	color: #fff;
	font-size: 4.5vw;
	font-weight: 700;
	user-select: none;
	cursor: pointer;
	display: flex;
	justify-content: center;
	align-items: center;
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