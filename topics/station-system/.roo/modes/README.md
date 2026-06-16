# Roo Cline Custom Modes

本目錄包含車站管理系統的自訂模式。

## 可用模式

### 🔒 Transit Security Expert

專門針對捷運/地鐵系統安全性審查的專家模式。

#### 模式特色

**專業領域**
- 🏢 實體安全: 車站門禁、監控系統、緊急出口、人群管理
- 💻 網路安全: API 安全、資料保護、認證授權、OWASP Top 10
- 🚨 營運安全: 事件回應、災難復原、業務持續性
- 📋 法規遵循: GDPR、資料隱私法、交通安全法規
- 🔍 風險評估: 威脅建模、弱點分析、安全稽核

**核心功能**
- 全面的安全稽核
- 漏洞識別與分析
- 風險等級評估 (Critical/High/Medium/Low)
- 具體的修復建議
- 程式碼範例提供
- 安全標準參考 (OWASP, NIST, ISO 27001)

#### 使用方式

1. **切換到此模式**
   ```
   在 Roo Cline 中選擇 "🔒 Transit Security Expert" 模式
   ```

2. **進行安全審查**
   ```
   請對車站管理系統進行全面的安全審查
   ```

3. **特定檢查**
   ```
   檢查 API 端點的認證與授權機制
   檢查是否有 SQL 注入漏洞
   審查資料保護措施
   評估 CORS 設定的安全性
   ```

#### 檢查項目

**認證與授權**
- ✅ 弱認證或缺少認證
- ✅ 不安全的 session 管理
- ✅ 權限提升漏洞
- ✅ 缺少授權檢查

**輸入驗證**
- ✅ SQL 注入漏洞
- ✅ 跨站腳本攻擊 (XSS)
- ✅ 命令注入
- ✅ 路徑遍歷
- ✅ XML/JSON 注入

**資料保護**
- ✅ 敏感資料暴露
- ✅ 不安全的資料儲存
- ✅ 缺少加密
- ✅ 弱加密演算法
- ✅ 硬編碼憑證

**API 安全**
- ✅ 缺少速率限制
- ✅ 不安全的 CORS 設定
- ✅ API 金鑰暴露
- ✅ 日誌記錄不足
- ✅ 錯誤資訊洩漏

**設定檢查**
- ✅ 預設憑證
- ✅ 生產環境的除錯模式
- ✅ 不必要的服務啟用
- ✅ 不安全的相依套件
- ✅ 缺少安全標頭

#### 風險分類

**🔴 CRITICAL (嚴重)**
- 遠端程式碼執行
- 認證繞過
- 具資料存取的 SQL 注入
- 暴露的管理介面
- 生產環境的硬編碼憑證

**🟠 HIGH (高)**
- 權限提升
- 敏感資料暴露
- 關鍵端點缺少認證
- 不安全的直接物件參考
- 跨站腳本攻擊 (XSS)

**🟡 MEDIUM (中)**
- 缺少輸入驗證
- 弱密碼政策
- 不安全的 session 管理
- 資訊洩漏
- 缺少安全標頭

**🟢 LOW (低)**
- 缺少 HTTPS 強制
- 詳細的錯誤訊息
- 過時的相依套件 (無已知漏洞)
- 缺少安全文件
- 程式碼品質問題

#### 捷運系統特定關注點

**乘客安全**
- 緊急警報系統
- 即時事件回報
- 疏散程序
- 無障礙功能

**營運完整性**
- 車站狀態監控
- 服務中斷處理
- 維護排程
- 員工通訊系統

**資料隱私**
- 乘客資訊保護
- CCTV 資料處理
- 支付資訊安全
- 位置追蹤合規

**實體安全整合**
- 門禁系統
- 監控整合
- 警報系統
- 緊急應變協調

#### 審查報告範本

```markdown
## 安全審查報告

### 執行摘要
- 整體安全態勢: [優良/良好/需改進/不足]
- 嚴重發現數量: X 個
- 風險評分: X/10
- 立即需要的行動: [列表]

### 發現事項

#### [發現 1]
- **風險等級**: 🔴 Critical
- **類別**: Authentication
- **描述**: [詳細說明]
- **影響**: [可能發生的情況]
- **受影響元件**: [檔案/端點]
- **概念驗證**: [如何重現]
- **修復方案**: [逐步修復]
- **參考資料**: [OWASP, CVE 等]

### 建議事項
- 快速勝利 (容易修復、高影響)
- 長期改進
- 建議實作的安全工具
- 培訓需求

### 合規狀態
- OWASP Top 10 覆蓋率
- 資料保護合規性
- 產業標準遵循度
```

#### 工具限制

此模式為**唯讀審查模式**,僅允許使用:
- ✅ `read_file` - 讀取檔案
- ✅ `list_files` - 列出檔案
- ✅ `search_files` - 搜尋檔案
- ✅ `list_code_definition_names` - 列出程式碼定義
- ✅ `execute_command` - 執行命令 (僅用於檢查)

不允許使用:
- ❌ `write_to_file` - 寫入檔案
- ❌ `apply_diff` - 套用差異
- ❌ `insert_content` - 插入內容

> 💡 **提示**: 審查完成後,切換回 Code 模式以實作修復建議

#### 使用範例

**範例 1: 全面安全審查**
```
使用者: 請對整個車站管理系統進行全面的安全審查

Transit Security Expert 會:
1. 檢查所有 Java 原始碼
2. 審查 API 端點
3. 分析設定檔
4. 檢查前端程式碼
5. 評估資料庫互動
6. 提供詳細的審查報告
```

**範例 2: 特定漏洞檢查**
```
使用者: 檢查系統是否有 SQL 注入漏洞

Transit Security Expert 會:
1. 分析所有資料庫查詢
2. 檢查參數化查詢的使用
3. 識別潛在的注入點
4. 提供修復建議
```

**範例 3: API 安全評估**
```
使用者: 評估 REST API 的安全性

Transit Security Expert 會:
1. 檢查認證機制
2. 審查授權邏輯
3. 評估輸入驗證
4. 檢查錯誤處理
5. 審查 CORS 設定
6. 評估速率限制
```

#### 最佳實踐

1. **定期審查**: 每次重大更新後進行安全審查
2. **優先修復**: 先處理 Critical 和 High 風險項目
3. **文件記錄**: 保存所有審查報告
4. **持續改進**: 實作建議的安全工具和流程
5. **培訓團隊**: 根據發現進行安全培訓

#### 相關資源

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP API Security Top 10](https://owasp.org/www-project-api-security/)
- [NIST Cybersecurity Framework](https://www.nist.gov/cyberframework)
- [ISO 27001](https://www.iso.org/isoiec-27001-information-security.html)
- [CWE Top 25](https://cwe.mitre.org/top25/)

## 建立新的 Custom Mode

1. 在 `.roo/modes/` 目錄下建立 JSON 設定檔
2. 定義模式的角色、指令和工具限制
3. 在 Roo Cline 中重新載入模式
4. 測試模式功能

### 模式設定範本

```json
{
  "slug": "mode-slug",
  "name": "🎯 Mode Name",
  "roleDefinition": "詳細的角色定義...",
  "groups": [
    {
      "name": "Group Name",
      "items": [
        {
          "type": "instruction",
          "text": "指令內容..."
        }
      ]
    }
  ],
  "tools": {
    "allowedTools": ["read_file", "list_files"],
    "disallowedTools": ["write_to_file"]
  },
  "metadata": {
    "version": "1.0.0",
    "author": "Author Name",
    "created": "2026-06-16",
    "description": "模式描述",
    "tags": ["tag1", "tag2"]
  }
}
```

## 注意事項

- 模式切換後會改變 AI 的行為和可用工具
- 某些模式可能限制檔案修改功能
- 審查模式建議在獨立分支進行
- 保存審查報告以供日後參考

## 相關文件

- [Slash Commands](../commands/README.md)
- [API 測試](../../API_TEST.sh)
- [測試結果](../../TEST_RESULTS.md)