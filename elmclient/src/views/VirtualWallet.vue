<template>
  <div class="virtual-wallet">
    <el-card class="wallet-card">
      <template #header>
        <div class="card-header">
          <span>我的钱包</span>
          <el-button
            :type="walletStatus === 'normal' ? 'warning' : 'success'"
            @click="toggleWalletStatus"
          >
            {{ walletStatus === 'normal' ? '冻结钱包' : '解冻钱包' }}
          </el-button>
        </div>
      </template>
      
      <div class="balance-section">
        <div class="balance-amount">
          <span class="label">当前余额</span>
          <span class="amount">¥{{ balance }}</span>
        </div>
        <div class="wallet-status" :class="walletStatus">
          {{ walletStatus === 'normal' ? '正常' : '已冻结' }}
        </div>
      </div>

      <div class="action-buttons">
        <el-button type="primary" @click="showRechargeDialog" :disabled="walletStatus === 'frozen'">
          充值
        </el-button>
        <el-button type="warning" @click="showWithdrawDialog" :disabled="walletStatus === 'frozen'">
          提现
        </el-button>
      </div>
    </el-card>

    <el-card class="transaction-card">
      <template #header>
        <div class="card-header">
          <span>交易记录</span>
        </div>
      </template>
      
      <transaction-list
        :transactions="transactionList"
        :loading="loading"
      />
    </el-card>

    <!-- 充值对话框 -->
    <el-dialog
      v-model="rechargeDialogVisible"
      title="钱包充值"
      :width="isMobile ? '90%' : '30%'"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="recharge-dialog"
    >
      <el-form :model="rechargeForm" :label-width="isMobile ? '5em' : '6em'" class="recharge-form">
        <el-form-item label="充值金额" class="amount-item">
          <div class="amount-input-wrapper">
            <span class="currency-symbol">¥</span>
            <el-input-number
              v-model="rechargeForm.amount"
              :min="1"
              :precision="2"
              :step="100"
              :controls="true"
              :size="isMobile ? 'default' : 'large'"
              class="amount-input"
            />
          </div>
          <div class="amount-tips">
            <p>最小充值金额：¥1.00</p>
            <p>建议金额：¥100、¥200、¥500、¥1000</p>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button 
            @click="rechargeDialogVisible = false" 
            :size="isMobile ? 'default' : 'large'"
          >取消</el-button>
          <el-button 
            type="primary" 
            @click="handleRecharge" 
            :size="isMobile ? 'default' : 'large'"
          >
            确认充值
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 提现对话框 -->
    <el-dialog
      v-model="withdrawDialogVisible"
      title="钱包提现"
      :width="isMobile ? '90%' : '30%'"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="recharge-dialog"
    >
      <el-form :model="withdrawForm" :label-width="isMobile ? '5em' : '6em'" class="recharge-form">
        <el-form-item label="提现金额" class="amount-item">
          <div class="amount-input-wrapper">
            <span class="currency-symbol">¥</span>
            <el-input-number
              v-model="withdrawForm.amount"
              :min="1"
              :max="balance"
              :precision="2"
              :step="100"
              :controls="true"
              :size="isMobile ? 'default' : 'large'"
              class="amount-input"
            />
          </div>
          <div class="amount-tips">
            <p>最小提现金额：¥1.00</p>
            <p>最大提现金额：¥{{ balance }}</p>
            <p>建议金额：¥100、¥200、¥500、¥1000</p>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button 
            @click="withdrawDialogVisible = false"
            :size="isMobile ? 'default' : 'large'"
          >取消</el-button>
          <el-button 
            type="primary" 
            @click="handleWithdraw"
            :size="isMobile ? 'default' : 'large'"
          >
            确认提现
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import TransactionList from '../components/TransactionList.vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

export default {
  name: 'VirtualWallet',
  components: {
    TransactionList
  },
  setup() {
    const router = useRouter()
    const balance = ref(0)
    const walletStatus = ref('normal')
    const transactionList = ref([])
    const loading = ref(false)
    const mounted = ref(false)
    
    // 充值相关
    const rechargeDialogVisible = ref(false)
    const rechargeForm = ref({
      amount: 100
    })

    // 提现相关
    const withdrawDialogVisible = ref(false)
    const withdrawForm = ref({
      amount: 100
    })

    // 响应式布局相关
    const windowWidth = ref(window.innerWidth)
    const isMobile = computed(() => windowWidth.value <= 768)

    // 监听窗口大小变化
    const handleResize = () => {
      windowWidth.value = window.innerWidth
    }

    // 初始化钱包数据
    const initWallet = async () => {
      if (!mounted.value) return

      const user = JSON.parse(sessionStorage.getItem('user'))
      if (!user) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }

      try {
        loading.value = true
        transactionList.value = [] // 清空现有数据

        // 设置请求头
        const headers = {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('token')}` || ''
        }

        // 检查钱包是否存在
        console.log('正在检查钱包是否存在...')
        try {
          const checkWalletResponse = await axios.get(`/plus/VirtualWalletController/getWhetherEnabledById?userId=${user.userId}`, { 
            headers,
            withCredentials: true 
          })
          console.log('检查钱包响应:', checkWalletResponse.data)
          
          if (checkWalletResponse.data.code === 200) {
            if (checkWalletResponse.data.data === 0) {
              console.log('钱包不存在，正在创建钱包...')
              try {
                // 钱包不存在，创建钱包
                const createWalletResponse = await axios.get(`/plus/VirtualWalletController/addWalletById?userId=${user.userId}`, { 
                  headers,
                  withCredentials: true 
                })
                console.log('创建钱包响应:', createWalletResponse.data)
                if (createWalletResponse.data.code !== 200) {
                  console.error('创建钱包失败，响应码:', createWalletResponse.data.code)
                  console.error('失败原因:', createWalletResponse.data.message)
                  ElMessage.error(`创建钱包失败: ${createWalletResponse.data.message || '未知错误'}`)
                  return
                }
                console.log('钱包创建成功，正在验证...')
                // 验证钱包是否真的创建成功
                const verifyResponse = await axios.get(`/plus/VirtualWalletController/getWhetherEnabledById?userId=${user.userId}`, { 
                  headers,
                  withCredentials: true 
                })
                console.log('验证钱包创建响应:', verifyResponse.data)
                if (verifyResponse.data.code === 200 && verifyResponse.data.data === 1) {
                  console.log('钱包验证成功')
                  ElMessage.success('钱包创建成功')
                } else {
                  console.error('钱包验证失败')
                  ElMessage.error('钱包创建可能未成功，请刷新页面重试')
                  return
                }
              } catch (createError) {
                console.error('创建钱包时发生错误:', createError)
                if (createError.response) {
                  console.error('创建钱包错误响应:', createError.response.data)
                  ElMessage.error(`创建钱包失败: ${createError.response.data.message || '未知错误'}`)
                } else {
                  ElMessage.error('创建钱包失败，请检查网络连接')
                }
                return
              }
            } else {
              console.log('钱包已存在')
            }
          } else {
            console.error('检查钱包状态失败，响应码:', checkWalletResponse.data.code)
            console.error('失败原因:', checkWalletResponse.data.message)
            ElMessage.error(`检查钱包状态失败: ${checkWalletResponse.data.message || '未知错误'}`)
            return
          }
        } catch (checkError) {
          console.error('检查钱包状态时发生错误:', checkError)
          if (checkError.response) {
            console.error('检查钱包状态错误响应:', checkError.response.data)
            ElMessage.error(`检查钱包状态失败: ${checkError.response.data.message || '未知错误'}`)
          } else {
            ElMessage.error('检查钱包状态失败，请检查网络连接')
          }
          return
        }

        // 获取钱包信息
        await getWalletInfo(user.userId, headers)

      } catch (error) {
        console.error('初始化钱包失败:', error)
        if (error.response) {
          console.error('错误响应:', error.response.data)
          ElMessage.error(`初始化钱包失败: ${error.response.data.message || '未知错误'}`)
        } else {
          ElMessage.error('初始化钱包失败，请检查网络连接')
        }
      } finally {
        loading.value = false
      }
    }

    // 获取钱包信息
    const getWalletInfo = async (userId, headers) => {
      try {
        // 获取钱包余额
        console.log('正在获取钱包余额...')
        const walletResponse = await axios.get(`/plus/VirtualWalletController/getBalanceById?userId=${userId}`, { 
          headers,
          withCredentials: true 
        })
        console.log('完整钱包响应:', walletResponse)
        console.log('钱包响应数据结构:', {
          data: walletResponse.data,
          nestedData: walletResponse.data.data,
          dataType: typeof walletResponse.data.data
        })
        
        if (walletResponse.data.code === 200) {
          const rawBalance = walletResponse.data.result
          console.log('原始余额值:', rawBalance, '类型:', typeof rawBalance)
          
          const parsedBalance = parseFloat(rawBalance || 0)
          console.log('解析后余额:', parsedBalance)
          
          balance.value = parsedBalance.toFixed(2)
          console.log('最终设置的余额:', balance.value)
        } else {
          console.error('获取钱包信息失败:', walletResponse.data.message)
          ElMessage.error(walletResponse.data.message || '获取钱包信息失败')
        }

        // 获取钱包状态
        const freezeResponse = await axios.get(`/plus/VirtualWalletController/getFreeze?userId=${userId}`, { 
          headers,
          withCredentials: true 
        })
        console.log('获取钱包冻结状态完整响应:', freezeResponse)
        console.log('获取钱包冻结状态响应数据:', freezeResponse.data)
        
        if (freezeResponse.data.code === 200) {
          // 确保正确解析冻结状态
          const freezeStatus = freezeResponse.data.result
          console.log('钱包冻结状态原始值:', freezeStatus, '类型:', typeof freezeStatus)
          walletStatus.value = freezeStatus === '1' || freezeStatus === 1 ? 'frozen' : 'normal'
          console.log('设置后的钱包状态:', walletStatus.value)
        } else {
          console.error('获取钱包状态失败:', freezeResponse.data.message)
          ElMessage.error('获取钱包状态失败')
        }

        // 获取交易记录
        console.log('开始获取交易记录...')
        const transactionResponse = await axios.get(`/plus/VirtualWalletController/listWalletLogById?userId=${userId}`, { 
          headers,
          withCredentials: true 
        })
        console.log('获取交易记录响应:', transactionResponse.data)
        
        if (transactionResponse.data.code === 200) {
          // 确保返回的数据是数组
          const records = transactionResponse.data.result || []
          console.log('处理前的交易记录:', records)
          
          // 处理交易记录数据
          transactionList.value = records.map(record => {
            const isIncome = record.targetType === 1 || 
              (record.targetType === 3 && record.targetId === userId)
            
            return {
              createTime: record.createTime,
              targetType: record.targetType,
              fee: record.fee,
              balance: record.balance || 0,
              userId: record.userId,
              targetId: record.targetId,
              isIncome: isIncome
            }
          })
          console.log('处理后的交易记录:', transactionList.value)
        } else {
          console.error('获取交易记录失败:', transactionResponse.data.message)
          ElMessage.error('获取交易记录失败')
        }
      } catch (error) {
        console.error('获取钱包信息失败:', error)
        if (error.response) {
          console.error('错误响应:', error.response.data)
          ElMessage.error(`获取钱包信息失败: ${error.response.data.msg || '未知错误'}`)
        } else {
          ElMessage.error('获取钱包信息失败，请检查网络连接')
        }
      }
    }

    // 获取交易备注
    const getTransactionRemark = (record) => {
      const types = {
        1: '充值',
        2: '提现',
        3: '转账'
      }
      const type = types[record.targetType] || '未知交易'
      
      if (record.targetType === 3) {
        const user = JSON.parse(sessionStorage.getItem('user'))
        if (record.userId === user.userId) {
          return `转账给用户${record.targetId}`
        } else {
          return `收到用户${record.userId}的转账`
        }
      }
      
      return type
    }

    // 显示充值对话框
    const showRechargeDialog = () => {
      if (!mounted.value) return
      if (walletStatus.value === 'frozen') {
        ElMessage.warning('钱包已冻结，无法进行充值操作')
        return
      }
      rechargeDialogVisible.value = true
    }

    // 处理充值
    const handleRecharge = async () => {
      if (!rechargeForm.value.amount || rechargeForm.value.amount <= 0) {
        ElMessage.warning('请输入有效的充值金额')
        return
      }

      if (walletStatus.value === 'frozen') {
        ElMessage.warning('钱包已冻结，无法进行充值操作')
        return
      }

      const user = JSON.parse(sessionStorage.getItem('user'))
      if (!user) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }

      try {
        loading.value = true
        console.log('开始充值处理...')

        const headers = {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('token')}` || ''
        }
        
        // 确保钱包存在
        const checkWalletResponse = await axios.get(`/plus/VirtualWalletController/getWhetherEnabledById?userId=${user.userId}`, { 
          headers,
          withCredentials: true 
        })
        if (checkWalletResponse.data.code === 200) {
          if (checkWalletResponse.data.data === 0) {
            // 钱包不存在，创建钱包
            const createWalletResponse = await axios.get(`/plus/VirtualWalletController/addWalletById?userId=${user.userId}`, { 
              headers,
              withCredentials: true 
            })
            if (createWalletResponse.data.code !== 200) {
              ElMessage.error('创建钱包失败')
              return
            }
          }
        } else {
          ElMessage.error('检查钱包状态失败')
          return
        }
        
        // 确保金额格式正确
        const amount = parseFloat(rechargeForm.value.amount).toFixed(2)
        console.log('格式化后的充值金额:', amount)

        const response = await axios.post('/plus/VirtualWalletController/rechargeById', {
          userId: user.userId,
          amount: amount,
          targetId: null
        }, { 
          headers,
          withCredentials: true 
        })
        console.log('充值响应:', response.data)

        if (response.data.code === 200) {
          ElMessage.success('充值成功')
          // 重新获取钱包信息
          await getWalletInfo(user.userId, headers)
          rechargeForm.value.amount = null
          rechargeDialogVisible.value = false
        } else {
          ElMessage.error('充值失败')
        }
      } catch (error) {
        console.error('充值失败:', error)
        if (error.response) {
          console.error('错误响应:', error.response.data)
          ElMessage.error(`充值失败: ${error.response.data.msg || '未知错误'}`)
        } else {
          ElMessage.error('充值失败，请检查网络连接')
        }
      } finally {
        loading.value = false
      }
    }

    // 显示提现对话框
    const showWithdrawDialog = () => {
      if (!mounted.value) return
      if (walletStatus.value === 'frozen') {
        ElMessage.warning('钱包已冻结，无法进行提现操作')
        return
      }
      withdrawDialogVisible.value = true
    }

    // 处理提现
    const handleWithdraw = async () => {
      if (!mounted.value) return

      if (walletStatus.value === 'frozen') {
        ElMessage.warning('钱包已冻结，无法进行提现操作')
        return
      }

      if (withdrawForm.value.amount > parseFloat(balance.value)) {
        ElMessage.warning('提现金额不能大于余额')
        return
      }

      const user = JSON.parse(sessionStorage.getItem('user'))
      if (!user) {
        ElMessage.warning('请先登录')
        return
      }

      try {
        const amount = parseFloat(withdrawForm.value.amount).toFixed(2)
        const headers = {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('token')}` || ''
        }
        const response = await axios.post('/plus/VirtualWalletController/withdrawById', {
          userId: user.userId,
          amount: amount
        }, { 
          headers,
          withCredentials: true 
        })
        
        if (response.data.code === 200) {
          ElMessage.success('提现成功')
          withdrawDialogVisible.value = false
          withdrawForm.value.amount = null
          if (mounted.value) {
            await initWallet()
          }
        } else {
          ElMessage.error(response.data.message || '提现失败')
        }
      } catch (error) {
        console.error('提现失败:', error)
        ElMessage.error('提现失败，请检查网络连接')
      }
    }

    // 切换钱包状态
    const toggleWalletStatus = async () => {
      if (!mounted.value) return

      const user = JSON.parse(sessionStorage.getItem('user'))
      if (!user) {
        ElMessage.warning('请先登录')
        return
      }

      try {
        const headers = {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('token')}` || ''
        }
        const action = walletStatus.value === 'normal' ? 'freeze' : 'unFreeze'
        console.log('执行操作:', action)
        
        const response = await axios.post(`/plus/VirtualWalletController/${action}?userId=${user.userId}`, null, { 
          headers,
          withCredentials: true 
        })
        
        console.log('钱包状态切换完整响应:', response)
        console.log('钱包状态切换响应数据:', response.data)
        
        if (response.data.code === 200) {
          // 获取最新的钱包状态
          const freezeResponse = await axios.get(`/plus/VirtualWalletController/getFreeze?userId=${user.userId}`, { 
            headers,
            withCredentials: true 
          })
          
          console.log('获取最新钱包状态完整响应:', freezeResponse)
          console.log('获取最新钱包状态响应数据:', freezeResponse.data)
          
          if (freezeResponse.data.code === 200) {
            const freezeStatus = freezeResponse.data.result
            console.log('最新钱包冻结状态原始值:', freezeStatus, '类型:', typeof freezeStatus)
            walletStatus.value = freezeStatus === '1' || freezeStatus === 1 ? 'frozen' : 'normal'
            console.log('更新后的钱包状态:', walletStatus.value)
            ElMessage.success(walletStatus.value === 'normal' ? '钱包已解冻' : '钱包已冻结')
          } else {
            console.error('获取最新钱包状态失败:', freezeResponse.data.message)
            ElMessage.error('获取钱包状态失败')
          }
        } else {
          console.error('钱包状态切换失败:', response.data.message)
          ElMessage.error(response.data.message || '操作失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
        if (error.response) {
          console.error('错误响应:', error.response.data)
          ElMessage.error(`操作失败: ${error.response.data.message || '未知错误'}`)
        } else {
          ElMessage.error('操作失败，请检查网络连接')
        }
      }
    }

    // 处理积分兑换
    const handleCredit2Money = async (credit) => {
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
        console.log('积分数量:', credit)
        console.log('请求头:', headers)

        const response = await axios.post(
          `/plus/CreditController/credit2money`,
          null,
          { 
            headers,
            params: {
              userId: user.userId,
              credit: credit
            },
            withCredentials: true 
          }
        )

        console.log('积分兑换响应:', response.data)

        if (response.data.code === 200) {
          ElMessage.success('积分兑换成功')
          // 重新获取钱包信息
          await initWallet()
        } else {
          ElMessage.error(response.data.message || '积分兑换失败')
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

    onMounted(() => {
      mounted.value = true
      initWallet()
      window.addEventListener('resize', handleResize)
    })

    onUnmounted(() => {
      mounted.value = false
      window.removeEventListener('resize', handleResize)
    })

    return {
      balance,
      walletStatus,
      transactionList,
      loading,
      rechargeDialogVisible,
      rechargeForm,
      withdrawDialogVisible,
      withdrawForm,
      showRechargeDialog,
      handleRecharge,
      showWithdrawDialog,
      handleWithdraw,
      toggleWalletStatus,
      isMobile,
      getTransactionRemark,
      handleCredit2Money
    }
  }
}
</script>

<style scoped>
.virtual-wallet {
  padding: 20px;
}

.wallet-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.balance-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.balance-amount {
  display: flex;
  flex-direction: column;
}

.label {
  font-size: 14px;
  color: #909399;
}

.amount {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-top: 8px;
}

.wallet-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
}

.wallet-status.normal {
  background-color: #f0f9eb;
  color: #67c23a;
}

.wallet-status.frozen {
  background-color: #fef0f0;
  color: #f56c6c;
}

.action-buttons {
  display: flex;
  gap: 16px;
}

.transaction-card {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 充值对话框样式 */
:deep(.recharge-dialog) {
  border-radius: max(8px, 1vw);
}

:deep(.recharge-dialog .el-dialog__header) {
  padding: clamp(15px, 2vw, 25px);
  margin-right: 0;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.recharge-dialog .el-dialog__title) {
  font-size: clamp(16px, 1.2vw, 20px);
  font-weight: 600;
  color: #303133;
}

:deep(.recharge-dialog .el-dialog__body) {
  padding: clamp(20px, 3vw, 35px) clamp(15px, 2vw, 25px);
}

.recharge-form {
  width: 100%;
}

.amount-item {
  margin-bottom: 0;
}

.amount-input-wrapper {
  display: flex;
  align-items: center;
  gap: max(4px, 0.5vw);
  width: 100%;
}

.currency-symbol {
  font-size: clamp(16px, 1.2vw, 20px);
  color: #606266;
  margin-right: max(2px, 0.3vw);
}

:deep(.amount-input) {
  width: 100%;
  max-width: min(300px, 80vw);
}

:deep(.amount-input .el-input-number__decrease),
:deep(.amount-input .el-input-number__increase) {
  border-color: #DCDFE6;
  background-color: #F5F7FA;
}

:deep(.amount-input .el-input__inner) {
  font-size: clamp(14px, 1vw, 18px);
  height: clamp(32px, 5vh, 40px);
  text-align: left;
  padding-left: max(10px, 1vw);
}

.amount-tips {
  margin-top: max(8px, 1vh);
  padding: max(6px, 0.8vh) 0;
}

.amount-tips p {
  margin: max(2px, 0.3vh) 0;
  color: #909399;
  font-size: clamp(12px, 0.9vw, 14px);
}

:deep(.dialog-footer) {
  padding: clamp(15px, 2vw, 25px);
  text-align: right;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: max(8px, 1vw);
}

:deep(.dialog-footer .el-button) {
  min-width: clamp(80px, 10vw, 100px);
  font-size: clamp(14px, 0.9vw, 16px);
}

@media screen and (max-width: 768px) {
  :deep(.dialog-footer) {
    flex-direction: column;
  }

  :deep(.dialog-footer .el-button) {
    width: 100%;
    margin: 0;
  }

  .amount-input-wrapper {
    flex-direction: column;
    align-items: stretch;
  }

  .currency-symbol {
    text-align: left;
    margin-bottom: 4px;
  }
}
</style> 