package com.library.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 应用启动监听器 - 用于在应用成功启动后输出API文档访问信息
 * 通过监听Spring Boot的ApplicationStartedEvent事件实现
 */
@Slf4j
@Component
public class ApiDocumentationListener implements ApplicationListener<ApplicationStartedEvent> {

    // 注入Spring环境配置对象，用于获取配置文件中的参数
    private final Environment environment;

    /**
     * 构造函数：通过依赖注入获取Environment对象
     * @param environment Spring环境配置对象
     */
    public ApiDocumentationListener(Environment environment) {
        this.environment = environment;
    }

    /**
     * 监听应用启动事件的回调方法
     * 当Spring Boot应用启动完成后自动触发
     * @param event 应用启动事件对象
     */
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        // 获取服务器端口配置（默认值为8080）
        String serverPort = environment.getProperty("server.port", "8080");
        // 获取应用上下文路径配置（默认值为空字符串）
        String contextPath = environment.getProperty("server.servlet.context-path", "");

        // 拼接基础API访问URL（格式：http://localhost:端口号/上下文路径）
        String baseUrl = "http://localhost:" + serverPort + contextPath;

        // 打印API启动信息分隔线
        log.info("==========================================================");
        log.info("=                                                        =");
        // 打印系统名称和状态
        log.info("=         Library Management System API Started          =");
        log.info("=                                                        =");
        // 打印基础API地址信息
        log.info("=  Base API URL: " + baseUrl + "                        =");
        log.info("=                                                        =");
        // 打印结束分隔线
        log.info("==========================================================");
    }
}