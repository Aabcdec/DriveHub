<script>
// 在 FollowManagement 组件中添加以下代码
export default {
    props: {
        currentFollowThread: {
            required: true
        },
        followRecords: {
            required: true
        },
        showFollowDialog: {
            required: true
        },
        ButtonList: {
            type: Array,
            default: () => []
        },
        followLoading: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            showAddFollowForm: false,
            followSaving: false,
            editingFollow: null,
            // 跟进表单
            followForm: {
                type: '',
                content: '',
                result: '',
                nextFollowTime: '',
                createTime: '',
                createBy: ''
            },
            // 跟进表单验证规则
            followRules: {
                type: [{ required: true, message: '请选择跟进方式', trigger: 'change' }],
                content: [
                    { required: true, message: '请输入跟进内容', trigger: 'blur' },
                    { max: 500, message: '跟进内容不能超过500个字符', trigger: 'blur' }
                ],
                result: [{ required: true, message: '请选择跟进结果', trigger: 'change' }],
                nextFollowTime: [{ validator: this.validateNextFollowTime, trigger: 'change' }]
            }
        }
    },
    watch: {
        // 监听对话框显示状态变化
        showFollowDialog(newVal) {
            if (newVal) {
                // 对话框打开时重置表单状态
                this.resetComponentState();
            } else {
                // 对话框关闭时清理状态
                this.cleanupState();
            }
        },
        // 监听当前线索变化
        currentFollowThread: {
            handler(newVal) {
                if (newVal && newVal.id) {
                    // 线索变化时重置编辑状态
                    this.editingFollow = null;
                    this.showAddFollowForm = false;
                }
            },
            deep: true
        }
    },
    methods: {
        // 重置组件状态
        resetComponentState() {
            this.showAddFollowForm = false;
            this.editingFollow = null;
            this.followSaving = false;
            this.resetFollowForm();
        },
        
        // 清理状态
        cleanupState() {
            // 可以在这里添加清理逻辑
        },
        
        // 处理关闭对话框事件
        handleCloseDialog(val) {
            this.$emit('update:showFollowDialog', val);
            if (!val) {
                this.$emit('dialog-closed');
            }
        },
        
        // 其他现有方法保持不变...
        
        // 刷新跟进记录 - 通过emit让父组件重新加载
        refreshFollowRecords() {
            console.log('请求父组件刷新跟进记录')
            this.$emit('refresh-records');
        },
        
        // 加载跟进记录 - 通过emit让父组件加载
        loadFollowRecords(threadId) {
            console.log('请求父组件加载线索跟进记录, threadId:', threadId)
            this.$emit('load-records', threadId);
        },
        
        // 重置跟进表单
        resetFollowForm() {
            this.followForm = {
                type: '',
                content: '',
                result: '',
                nextFollowTime: '',
                createTime: '',
                createBy: ''
            };
            
            // 重置表单验证状态
            this.$nextTick(() => {
                if (this.$refs.followFormRef) {
                    this.$refs.followFormRef.clearValidate();
                }
            });
        }
    }
}
</script>
<script>
// 在 FollowManagement 组件中添加以下代码
export default {
    props: {
        currentFollowThread: {
            required: true
        },
        followRecords: {
            required: true
        },
        showFollowDialog: {
            required: true
        },
        ButtonList: {
            type: Array,
            default: () => []
        },
        followLoading: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            showAddFollowForm: false,
            followSaving: false,
            editingFollow: null,
            // 跟进表单
            followForm: {
                type: '',
                content: '',
                result: '',
                nextFollowTime: '',
                createTime: '',
                createBy: ''
            },
            // 跟进表单验证规则
            followRules: {
                type: [{ required: true, message: '请选择跟进方式', trigger: 'change' }],
                content: [
                    { required: true, message: '请输入跟进内容', trigger: 'blur' },
                    { max: 500, message: '跟进内容不能超过500个字符', trigger: 'blur' }
                ],
                result: [{ required: true, message: '请选择跟进结果', trigger: 'change' }],
                nextFollowTime: [{ validator: this.validateNextFollowTime, trigger: 'change' }]
            }
        }
    },
    watch: {
        // 监听对话框显示状态变化
        showFollowDialog(newVal) {
            if (newVal) {
                // 对话框打开时重置表单状态
                this.resetComponentState();
            } else {
                // 对话框关闭时清理状态
                this.cleanupState();
            }
        },
        // 监听当前线索变化
        currentFollowThread: {
            handler(newVal) {
                if (newVal && newVal.id) {
                    // 线索变化时重置编辑状态
                    this.editingFollow = null;
                    this.showAddFollowForm = false;
                }
            },
            deep: true
        }
    },
    methods: {
        // 重置组件状态
        resetComponentState() {
            this.showAddFollowForm = false;
            this.editingFollow = null;
            this.followSaving = false;
            this.resetFollowForm();
        },
        
        // 清理状态
        cleanupState() {
            // 可以在这里添加清理逻辑
        },
        
        // 处理关闭对话框事件
        handleCloseDialog(val) {
            this.$emit('update:showFollowDialog', val);
            if (!val) {
                this.$emit('dialog-closed');
            }
        },
        
        // 其他现有方法保持不变...
        
        // 刷新跟进记录 - 通过emit让父组件重新加载
        refreshFollowRecords() {
            console.log('请求父组件刷新跟进记录')
            this.$emit('refresh-records');
        },
        
        // 加载跟进记录 - 通过emit让父组件加载
        loadFollowRecords(threadId) {
            console.log('请求父组件加载线索跟进记录, threadId:', threadId)
            this.$emit('load-records', threadId);
        },
        
        // 重置跟进表单
        resetFollowForm() {
            this.followForm = {
                type: '',
                content: '',
                result: '',
                nextFollowTime: '',
                createTime: '',
                createBy: ''
            };
            
            // 重置表单验证状态
            this.$nextTick(() => {
                if (this.$refs.followFormRef) {
                    this.$refs.followFormRef.clearValidate();
                }
            });
        }
    }
}
</script>
<script>
// 在 FollowManagement 组件中添加以下代码
export default {
    props: {
        currentFollowThread: {
            required: true
        },
        followRecords: {
            required: true
        },
        showFollowDialog: {
            required: true
        },
        ButtonList: {
            type: Array,
            default: () => []
        },
        followLoading: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            showAddFollowForm: false,
            followSaving: false,
            editingFollow: null,
            // 跟进表单
            followForm: {
                type: '',
                content: '',
                result: '',
                nextFollowTime: '',
                createTime: '',
                createBy: ''
            },
            // 跟进表单验证规则
            followRules: {
                type: [{ required: true, message: '请选择跟进方式', trigger: 'change' }],
                content: [
                    { required: true, message: '请输入跟进内容', trigger: 'blur' },
                    { max: 500, message: '跟进内容不能超过500个字符', trigger: 'blur' }
                ],
                result: [{ required: true, message: '请选择跟进结果', trigger: 'change' }],
                nextFollowTime: [{ validator: this.validateNextFollowTime, trigger: 'change' }]
            }
        }
    },
    watch: {
        // 监听对话框显示状态变化
        showFollowDialog(newVal) {
            if (newVal) {
                // 对话框打开时重置表单状态
                this.resetComponentState();
            } else {
                // 对话框关闭时清理状态
                this.cleanupState();
            }
        },
        // 监听当前线索变化
        currentFollowThread: {
            handler(newVal) {
                if (newVal && newVal.id) {
                    // 线索变化时重置编辑状态
                    this.editingFollow = null;
                    this.showAddFollowForm = false;
                }
            },
            deep: true
        }
    },
    methods: {
        // 重置组件状态
        resetComponentState() {
            this.showAddFollowForm = false;
            this.editingFollow = null;
            this.followSaving = false;
            this.resetFollowForm();
        },
        
        // 清理状态
        cleanupState() {
            // 可以在这里添加清理逻辑
        },
        
        // 处理关闭对话框事件
        handleCloseDialog(val) {
            this.$emit('update:showFollowDialog', val);
            if (!val) {
                this.$emit('dialog-closed');
            }
        },
        
        // 其他现有方法保持不变...
        
        // 刷新跟进记录 - 通过emit让父组件重新加载
        refreshFollowRecords() {
            console.log('请求父组件刷新跟进记录')
            this.$emit('refresh-records');
        },
        
        // 加载跟进记录 - 通过emit让父组件加载
        loadFollowRecords(threadId) {
            console.log('请求父组件加载线索跟进记录, threadId:', threadId)
            this.$emit('load-records', threadId);
        },
        
        // 重置跟进表单
        resetFollowForm() {
            this.followForm = {
                type: '',
                content: '',
                result: '',
                nextFollowTime: '',
                createTime: '',
                createBy: ''
            };
            
            // 重置表单验证状态
            this.$nextTick(() => {
                if (this.$refs.followFormRef) {
                    this.$refs.followFormRef.clearValidate();
                }
            });
        }
    }
}
</script>
# per_project

## Project setup
```
pnpm install
```

### Compiles and hot-reloads for development
```
pnpm run serve
```

### Compiles and minifies for production
```
pnpm run build
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
