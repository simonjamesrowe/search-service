package com.simonjamesrowe.searchservice.config

import io.opentracing.Tracer
import io.opentracing.contrib.spring.cloud.async.instrument.TracedThreadPoolTaskScheduler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
class SchedulerConfig {

  @Bean
  fun taskScheduler(tracer: Tracer): TaskScheduler = TracedThreadPoolTaskScheduler(tracer, ThreadPoolTaskScheduler())
}
