export const menuRule = [
    {
        admin: [
            {
                index: '1',
                item: '市场活动',
                icon: 'DocumentCopy',
                children: [
                    { index: '1-1', item: '活动统计', icon: 'TrendCharts', route: '/market' },
                    { index: '1-2', item: '活动管理', icon: 'Tickets', route: '/market/campaigns' }
                ]
            },
            {
                index: '2',
                item: '线索管理',
                icon: 'Aim',
                children: [
                    { index: '2-1', item: '线索列表', icon: 'List', route: '/thread' },
                    { index: '2-2', item: '跟进记录', icon: 'ChatLineRound', route: '/thread/follow' }
                ]
            },
            {
                index: '3',
                item: '客户管理',
                icon: 'UserFilled',
                children: [
                    { index: '3-1', item: '客户列表', icon: 'User', route: '/clientMana' }
                ]
            },
            {
                index: '4',
                item: '交易管理',
                icon: 'Collection',
                children: [
                    { index: '4-1', item: '交易记录', icon: 'Money', route: '/transaction' }
                ]
            },
            {
                index: '5',
                item: '产品管理',
                icon: 'List',
                children: [
                    { index: '5-1', item: '产品列表', icon: 'Box', route: '/product' }
                ]
            },
            {
                index: '6',
                item: '字典管理',
                icon: 'Grid',
                children: [
                    { index: '6-1', item: '数据字典', icon: 'Document', route: '/dictionary' },
                    { index: '6-2', item: '系统配置', icon: 'Tools', route: '/dictionary/config' }
                ]
            },
            {
                index: '7',
                item: '用户管理',
                icon: 'Avatar',
                children: [
                    { index: '7-1', item: '用户列表', icon: 'UserFilled', route: '/users' },
                    { index: '7-2', item: '角色管理', icon: 'Key', route: '/users/roles' }
                ]
            },
            {
                index: '8',
                item: '系统管理',
                icon: 'Setting',
                children: [
                    { index: '8-1', item: '操作日志', icon: 'DocumentCopy', route: '/system/logs' }
                ]
            }

        ],
        saler: [
            {
                index: '1',
                item: '线索管理',
                icon: 'Aim',
                children: [
                    { index: '2-1', item: '线索列表', icon: 'List', route: '/thread' },
                    { index: '2-2', item: '跟进记录', icon: 'ChatLineRound', route: '/thread/follow' }
                ]
            },
            {
                index: '2',
                item: '客户管理',
                icon: 'UserFilled',
                children: [
                    { index: '3-1', item: '客户列表', icon: 'User', route: '/clientMana' }
                ]
            },
            {
                index: '3',
                item: '交易管理',
                icon: 'Collection',
                children: [
                    { index: '1-1', item: '交易记录', icon: 'Money', route: '/transaction' }
                ]
            },

        ],
        manager: [
            {
                index: '1',
                item: '市场活动',
                icon: 'DocumentCopy',
                children: [
                    { index: '1-1', item: '活动统计', icon: 'TrendCharts', route: '/market' },
                    { index: '1-2', item: '活动管理', icon: 'Tickets', route: '/market/campaigns' }
                ]
            },
            {
                index: '2',
                item: '线索管理',
                icon: 'Aim',
                children: [
                    { index: '2-1', item: '线索列表', icon: 'List', route: '/thread' },
                    { index: '2-2', item: '跟进记录', icon: 'ChatLineRound', route: '/thread/follow' }
                ]
            },
            {
                index: '3',
                item: '客户管理',
                icon: 'UserFilled',
                children: [
                    { index: '3-1', item: '客户列表', icon: 'User', route: '/clientMana' }
                ]
            },

            {
                index: '4',
                item: '产品管理',
                icon: 'List',
                children: [
                    { index: '5-1', item: '产品列表', icon: 'Box', route: '/product' }
                ]
            },
            {
                index: '5',
                item: '用户管理',
                icon: 'Avatar',
                children: [
                    { index: '6-1', item: '用户列表', icon: 'UserFilled', route: '/users' },
                    { index: '6-2', item: '角色管理', icon: 'Key', route: '/users/roles' }
                ]
            }

        ],
        marketer: [
            {
                index: '1',
                item: '市场活动',
                icon: 'DocumentCopy',
                children: [
                    { index: '1-1', item: '活动统计', icon: 'TrendCharts', route: '/market' },
                    { index: '1-2', item: '活动管理', icon: 'Tickets', route: '/market/campaigns' }
                ]
            },
            {
                index: '2',
                item: '线索管理',
                icon: 'Aim',
                children: [
                    { index: '2-1', item: '线索列表', icon: 'List', route: '/thread' },
                    { index: '2-2', item: '跟进记录', icon: 'ChatLineRound', route: '/thread/follow' }
                ]
            },



        ],
        accountant: [

            {
                index: '1',
                item: '交易管理',
                icon: 'Collection',
                children: [
                    { index: '1-1', item: '交易记录', icon: 'Money', route: '/transaction' }
                ]
            },
            {
                index: '1',
                item: '市场活动',
                icon: 'DocumentCopy',
                children: [
                    { index: '1-1', item: '活动统计', icon: 'TrendCharts', route: '/market' },
                    { index: '1-2', item: '活动管理', icon: 'Tickets', route: '/market/campaigns' }
                ]
            },



        ]

    }
]