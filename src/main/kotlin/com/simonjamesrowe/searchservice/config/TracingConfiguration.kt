package com.simonjamesrowe.searchservice.config

import io.jaegertracing.internal.JaegerTracer
import io.jaegertracing.internal.metrics.Metrics
import io.jaegertracing.internal.metrics.NoopMetricsFactory
import io.jaegertracing.internal.reporters.CompositeReporter
import io.jaegertracing.internal.reporters.RemoteReporter
import io.jaegertracing.internal.samplers.ProbabilisticSampler
import io.jaegertracing.spi.MetricsFactory
import io.jaegertracing.spi.Reporter
import io.jaegertracing.spi.Sampler
import io.jaegertracing.spi.Sender
import io.jaegertracing.thrift.internal.senders.HttpSender
import io.opentracing.Tracer
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
@EnableConfigurationProperties(TracingConfiguration.JaegerConfigurationProperties::class)
class TracingConfiguration {

  @Bean
  fun jaegerTracer(
    sampler: Sampler,
    reporter: Reporter,
    metrics: Metrics,
    properties: JaegerConfigurationProperties
  ): Tracer {
    val builder = JaegerTracer.Builder(properties.serviceName)
      .withReporter(reporter)
      .withSampler(sampler)
      .withMetrics(metrics)
    return builder.build()
  }

  @ConditionalOnMissingBean
  @Bean
  fun reporter(
    properties: JaegerConfigurationProperties,
    metrics: Metrics
  ): Reporter {
    val reporters: MutableList<Reporter> = LinkedList()
    reporters.add(getHttpReporter(metrics, properties.url))
    return CompositeReporter(*reporters.toTypedArray())
  }


  private fun getHttpReporter(
    metrics: Metrics,
    url: String
  ): Reporter {
    var builder = HttpSender.Builder(url)
    return createReporter(metrics, builder.build())
  }

  private fun createReporter(
    metrics: Metrics,
    udpSender: Sender
  ): Reporter {
    val builder = RemoteReporter.Builder()
      .withSender(udpSender)
      .withMetrics(metrics)
    return builder.build()
  }

  @ConditionalOnMissingBean
  @Bean
  fun metrics(metricsFactory: MetricsFactory): Metrics {
    return Metrics(metricsFactory)
  }

  @ConditionalOnMissingBean
  @Bean
  fun metricsFactory(): MetricsFactory {
    return NoopMetricsFactory()
  }

  /**
   * Decide on what Sampler to use based on the various configuration options in
   * JaegerConfigurationProperties Fallback to ConstSampler(true) when no Sampler is configured
   */
  @ConditionalOnMissingBean
  @Bean
  fun sampler(): Sampler {
    return ProbabilisticSampler(0.5)
  }

  @ConstructorBinding
  @ConfigurationProperties("jaeger")
  data class JaegerConfigurationProperties(
    val serviceName: String,
    val url: String
  )

}
