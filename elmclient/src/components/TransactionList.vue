<template>
  <div class="transaction-list">
    <el-table
      :data="safeTransactions"
      style="width: 100%"
      v-loading="loading"
    >
      <el-table-column
        prop="createTime"
        label="交易时间"
        width="180"
      >
        <template #default="scope">
          {{ formatDate(scope?.row?.createTime) }}
        </template>
      </el-table-column>

      <el-table-column
        prop="type"
        label="交易类型"
        width="120"
      >
        <template #default="scope">
          <el-tag :type="getTransactionTypeTag(scope?.row?.type)">
            {{ getTransactionTypeText(scope?.row?.type) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column
        prop="amount"
        label="交易金额"
        width="120"
      >
        <template #default="scope">
          <span :class="{ 'income': scope.row.isIncome, 'outcome': !scope.row.isIncome }">
            {{ scope.row.isIncome ? '+' : '-' }}¥{{ scope.row.amount || 0 }}
          </span>
        </template>
      </el-table-column>

      <el-table-column
        prop="balance"
        label="账户余额"
        width="120"
      >
        <template #default="scope">
          ¥{{ scope.row.balance || 0 }}
        </template>
      </el-table-column>

      <el-table-column
        prop="remark"
        label="备注"
      >
        <template #default="scope">
          {{ scope.row.remark || '-' }}
        </template>
      </el-table-column>
    </el-table>

    <div v-if="!loading && (!safeTransactions || safeTransactions.length === 0)" class="empty-state">
      <el-empty description="暂无交易记录" />
    </div>

    <div class="pagination" v-if="safeTransactions && safeTransactions.length > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import dayjs from 'dayjs'

export default {
  name: 'TransactionList',
  props: {
    transactions: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const mounted = ref(false)

    // 安全的交易记录数据
    const safeTransactions = computed(() => {
      if (!mounted.value) return []
      if (!Array.isArray(props.transactions)) return []
      
      console.log('处理交易记录数据:', props.transactions)
      
      return props.transactions.map(transaction => {
        if (!transaction) {
          console.log('空交易记录，使用默认值')
          return {
            createTime: new Date(),
            type: 1,
            amount: 0,
            balance: 0,
            remark: '-',
            isIncome: true
          }
        }
        
        console.log('处理单条交易记录:', transaction)
        const type = Number(transaction.targetType) || 1
        const amount = Number(transaction.fee) || 0
        const isIncome = transaction.isIncome !== undefined ? transaction.isIncome : (type === 1)
        
        return {
          createTime: transaction.createTime || new Date(),
          type: type,
          amount: amount,
          balance: Number(transaction.balance) || 0,
          remark: transaction.remark || getTransactionTypeText(type),
          isIncome: isIncome
        }
      })
    })

    // 监听交易数据变化
    watch(() => props.transactions, (newVal) => {
      if (mounted.value && Array.isArray(newVal)) {
        total.value = newVal.length
      }
    }, { immediate: true })

    // 格式化日期
    const formatDate = (date) => {
      if (!date) return '-'
      try {
        return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
      } catch (error) {
        console.error('Date formatting error:', error)
        return '-'
      }
    }

    // 获取交易类型文本
    const getTransactionTypeText = (type) => {
      const types = {
        1: '充值',
        2: '提现',
        3: '转账'
      }
      return types[Number(type)] || '未知'
    }

    // 获取交易类型标签样式
    const getTransactionTypeTag = (type) => {
      const types = {
        1: 'success',  // 充值
        2: 'warning',  // 提现
        3: 'info'      // 转账
      }
      return types[Number(type)] || 'info'
    }

    // 处理页码变化
    const handleCurrentChange = (val) => {
      if (mounted.value) {
        currentPage.value = val
      }
    }

    // 处理每页显示数量变化
    const handleSizeChange = (val) => {
      if (mounted.value) {
        pageSize.value = val
        currentPage.value = 1
      }
    }

    onMounted(() => {
      mounted.value = true
      if (Array.isArray(props.transactions)) {
        total.value = props.transactions.length
      }
    })

    onUnmounted(() => {
      mounted.value = false
    })

    return {
      currentPage,
      pageSize,
      total,
      safeTransactions,
      formatDate,
      getTransactionTypeTag,
      getTransactionTypeText,
      handleCurrentChange,
      handleSizeChange
    }
  }
}
</script>

<style scoped>
.transaction-list {
  width: 100%;
  min-height: 200px;
  position: relative;
}

.income {
  color: #67C23A;
  font-weight: bold;
}

.outcome {
  color: #F56C6C;
  font-weight: bold;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.empty-state {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  text-align: center;
}
</style> 