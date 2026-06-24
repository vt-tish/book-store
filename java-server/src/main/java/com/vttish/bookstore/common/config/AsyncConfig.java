package com.vttish.bookstore.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync(order = Ordered.HIGHEST_PRECEDENCE)
public class AsyncConfig {
}
