package com.payment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) 配置
 * 
 * @author IBM Bob Workshop
 * @version 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("信用卡交易監控系統 API")
                        .version("1.0.0")
                        .description("""
                                信用卡交易監控系統
                                
                                ## 功能模組
                                
                                ### 交易管理 (Transaction Management)
                                - 查詢所有交易記錄
                                - 查詢單筆交易詳情
                                - 依卡片查詢交易歷史
                                - 查詢最近時間範圍內的交易
                                - 查詢高額交易
                                - 取得交易統計資料
                                
                                ### 即將推出 (Coming Soon)
                                - 異常偵測引擎
                                - 警示管理系統
                                """)
                        .contact(new Contact()
                                .name("技術團隊")
                                .email("tech@example.com")
                                .url("https://www.example.com"))
                        .license(new License()
                                .name("IBM Bob Workshop Sample")
                                .url("https://github.com/ibm/bob")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("本地開發環境")
                ));
    }
}

// Made with Bob