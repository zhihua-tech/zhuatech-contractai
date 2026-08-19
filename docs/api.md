# ContractAI API
Copyright 2026 上海如静知华信息科技有限公司。除登录和健康检查外均需 `Bearer` JWT。

| 方法 | 地址 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录 |
| POST | `/api/ai/contract/analyze` | 合同条款与履约风险审查 |
| GET | `/api/admin/dashboard` | 合同风险管理看板 |
| GET | `/api/admin/work-orders` | 合同审查任务 |
| GET | `/api/workspace/dashboard` | 经办人工作台 |

审查输入包括合同编号、交易对手、金额、缺失条款、无限责任、自动续约、付款偏离和到期日；输出风险分数、等级、审查路由、命中原因和法务审批要求。
