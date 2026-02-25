Luxurious Travel Management System
Enterprise-Level Java Desktop Commerce & Order Management Platform

高端旅遊電商與訂單管理整合系統
Java / Swing / MVC / DAO / MySQL / Apache POI
完整會員、商品、訂單、績效與報表後台管理系統

📌 專案介紹

Luxurious Travel Management System 是一套以 企業級分層架構（MVC + Service + DAO） 設計的桌面型旅遊電商平台。

本系統完整模擬：

前台會員下單流程

VIP 專屬訂單中心

後台商品管理

訂單管理與狀態控管

業績統計與圖表報表

員工績效管理

GM 管理權限控制

此專案重點在於：

✔ 系統架構完整
✔ 商業流程邏輯完整
✔ 權限分級清楚
✔ 訂單金額計算精準
✔ 可產出 Excel 報表
✔ 具備實務專案開發思維

🏗 技術架構
技術棧

Java 11

Swing GUI

MVC Architecture

DAO Pattern

Service Layer

MySQL

Apache POI (XLSX報表)

JFreeChart (圖表統計)

Maven

系統分層架構
UI Layer (Swing)
↓
Controller / Action Listener
↓
Service Layer (Business Logic)
↓
DAO Layer (SQL Access)
↓
Database (MySQL)
🔐 登入與權限控管
LoginUI

功能說明

身份選擇：會員 / 員工

帳號密碼驗證

根據角色導向不同系統

權限分級
身份	可使用功能
會員	下單 / 歷史訂單 / 會員中心
員工	訂單管理 / 商品管理
GM總經理	員工績效 / 營業額報表 / 全部訂單控管
👤 會員模組
會員註冊 RegisterCustomerUI

功能

自動產生 customer_no

會員等級預設 BRONZE

照片上傳 / 修改 / 刪除

帳號唯一性驗證

完整資料欄位驗證

會員中心 CustomerUI

顯示：

基本資料

累積消費

會員等級

操作：

修改資料

查看歷史訂單

進入下單中心

修改會員資料 EditCustomerUI

username 不可更改

customer_no 不可更改

密碼可更新

圖片可更新

🛒 前台下單系統
VIP 旅遊下單中心 OrderUI

功能設計

商品卡片展示

出發日選擇

數量選擇

即時計算金額

購物車動態更新

可套用優惠券

指派業務員

商品詳細頁 ProductDetailUI

功能：

多圖輪播展示

可出發日期查詢

剩餘機位顯示

行程完整說明

確認訂單頁 ConfirmUI

列出商品明細

自動計算小計

計算折扣

計算最終金額

訂單完成頁 FinishedUI

顯示 order_no

顯示 final_amount

可列印報表

📦 訂單管理模組
歷史訂單 HistoryOrderUI

功能：

依訂單編號查詢

依日期區間查詢

顯示折扣

顯示最終金額

可匯出 Excel

訂單明細 OrderDetailUI

顯示單筆訂單所有商品

列印報表功能

後台訂單管理 OrderManagerUI

功能：

訂單狀態篩選（PENDING / CONFIRMED / CANCELLED）

指派業務員

更新狀態

刪除訂單

月報查詢

Excel 匯出

👔 員工管理模組
員工註冊 AddEmployeeUI

自動產生 employee_no

角色選擇（STAFF / GM）

照片管理

帳號唯一性驗證

修改員工資料 EditEmployeeUI

角色可更新

username 不可更改

GM 管理員工 EmployeeManagerUI

功能：

日期區間業績查詢

單員工業績統計

折線圖顯示趨勢

匯出報表

📊 業績與報表系統
歷史績效報表 HistoryPerformanceUI

當日 / 當週 / 當月 / 當季 / 當年

JFreeChart 圖表呈現

金額統計

營業額總報表 ReportManagerUI

功能：

日期區間營業額統計

折線圖分析

匯出 Excel

🏬 商品管理系統
商品管理 ProductManagerUI

功能：

新增商品

修改商品

刪除商品

價格與庫存控管

商品圖片管理

最多 5 張圖片

可新增/修改/刪除

商品新增後自動寫入 DB

行程與出發日管理

多筆出發日

機位數量控制

關聯 product_id

💰 商業邏輯亮點
折扣機制

依會員等級計算折扣

計算公式：

final_amount = amount - discount
訂單編號生成規則
O + yyyyMMddHHmmss + 隨機碼

確保唯一性。

交易流程
會員登入
→ 選擇商品
→ 加入購物車
→ 確認訂單
→ 建立 Order
→ 建立 OrderItem
→ 更新庫存
→ 指派業務員
→ 訂單完成
📈 專案設計重點

✔ 清楚的分層架構
✔ 嚴謹的資料驗證
✔ 商業流程完整
✔ 權限角色分離
✔ 可維護性高
✔ 可擴充架構
