// 交易管理模拟数据
export const mockTransactions = [
  {
    id: 1,
    tran_no: 'TXN202501001',
    customer_id: 1001,
    money: 150000.00,
    expected_date: '2025-02-15 00:00:00',
    stage: 2,
    description: '企业级CRM系统开发项目，包含客户管理、销售管理等核心功能模块',
    next_contact_time: '2025-01-20 14:00:00',
    create_time: '2025-01-16 09:30:00',
    create_by: 1,
    edit_time: '2025-01-16 15:20:00',
    edit_by: 1
  },
  {
    id: 2,
    tran_no: 'TXN202501002',
    customer_id: 1002,
    money: 85000.00,
    expected_date: '2025-01-25 00:00:00',
    stage: 3,
    description: '电商平台移动端APP开发，支持iOS和Android双平台',
    next_contact_time: '2025-01-18 10:00:00',
    create_time: '2025-01-15 11:15:00',
    create_by: 2,
    edit_time: '2025-01-16 16:45:00',
    edit_by: 2
  },
  {
    id: 3,
    tran_no: 'TXN202501003',
    customer_id: 1003,
    money: 320000.00,
    expected_date: '2025-03-10 00:00:00',
    stage: 1,
    description: '智能制造管理系统，包含生产计划、质量控制、设备管理等模块',
    next_contact_time: '2025-01-22 09:00:00',
    create_time: '2025-01-14 14:20:00',
    create_by: 1,
    edit_time: '2025-01-16 10:30:00',
    edit_by: 1
  },
  {
    id: 4,
    tran_no: 'TXN202501004',
    customer_id: 1004,
    money: 45000.00,
    expected_date: '2025-01-30 00:00:00',
    stage: 4,
    description: '企业官网重构项目，响应式设计，SEO优化',
    next_contact_time: '2025-01-19 16:30:00',
    create_time: '2025-01-13 16:45:00',
    create_by: 3,
    edit_time: '2025-01-16 11:20:00',
    edit_by: 3
  },
  {
    id: 5,
    tran_no: 'TXN202501005',
    customer_id: 1005,
    money: 280000.00,
    expected_date: '2025-04-20 00:00:00',
    stage: 5,
    description: '数据分析平台开发，支持大数据处理和可视化展示',
    next_contact_time: null,
    create_time: '2025-01-12 10:00:00',
    create_by: 2,
    edit_time: '2025-01-16 14:15:00',
    edit_by: 2
  }
]

export const mockHistory = [
  {
    id: 1,
    tran_id: 1,
    stage: 1,
    money: 150000.00,
    expected_date: '2025-02-15 00:00:00',
    create_time: '2025-01-16 09:30:00',
    create_by: 1
  },
  {
    id: 2,
    tran_id: 1,
    stage: 2,
    money: 150000.00,
    expected_date: '2025-02-15 00:00:00',
    create_time: '2025-01-16 15:20:00',
    create_by: 1
  }
]

export const mockRemarks = [
  {
    id: 1,
    tran_id: 1,
    note_way: 1,
    note_content: '与客户进行了初步沟通，了解了项目需求和预算范围，客户对我们的技术方案比较满意',
    create_time: '2025-01-16 10:30:00',
    create_by: 1,
    edit_time: '2025-01-16 10:30:00',
    edit_by: 1,
    deleted: 0
  },
  {
    id: 2,
    tran_id: 1,
    note_way: 4,
    note_content: '现场演示了类似项目案例，客户对功能模块设计很认可，准备进入合同洽谈阶段',
    create_time: '2025-01-16 15:45:00',
    create_by: 1,
    edit_time: '2025-01-16 15:45:00',
    edit_by: 1,
    deleted: 0
  },
  {
    id: 3,
    tran_id: 2,
    note_way: 2,
    note_content: '发送了详细的技术方案和报价单，等待客户反馈',
    create_time: '2025-01-15 16:20:00',
    create_by: 2,
    edit_time: '2025-01-15 16:20:00',
    edit_by: 2,
    deleted: 0
  }
]

export const mockStatistics = {
  total: 5,
  completed: 1,
  pending: 3,
  overdue: 1
}
