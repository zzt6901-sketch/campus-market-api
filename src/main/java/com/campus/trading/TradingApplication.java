package com.campus.trading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TradingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradingApplication.class, args);
        System.out.println("========================================");
        System.out.println("  校园二手交易平台启动成功！");
        System.out.println("  API 文档: http://localhost:8080/doc.html");
        System.out.println("========================================");
    }
}
