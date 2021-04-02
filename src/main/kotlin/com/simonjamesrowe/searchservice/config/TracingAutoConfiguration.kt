package com.simonjamesrowe.searchservice.config

import brave.Span
import brave.propagation.TraceContext
import brave.propagation.TraceContextOrSamplingFlags
import io.jaegertracing.internal.JaegerTracer
import io.jaegertracing.internal.metrics.Metrics
import io.jaegertracing.internal.metrics.NoopMetricsFactory
import io.jaegertracing.internal.reporters.CompositeReporter
import io.jaegertracing.internal.reporters.LoggingReporter
import io.jaegertracing.internal.reporters.RemoteReporter
import io.jaegertracing.internal.samplers.*
import io.jaegertracing.spi.MetricsFactory
import io.jaegertracing.spi.Reporter
import io.jaegertracing.spi.Sampler
import io.jaegertracing.spi.Sender
import io.jaegertracing.thrift.internal.senders.HttpSender
import io.jaegertracing.thrift.internal.senders.UdpSender
import io.opentracing.Tracer
import io.opentracing.contrib.java.spring.jaeger.starter.JaegerConfigurationProperties
import io.opentracing.contrib.java.spring.jaeger.starter.ReporterAppender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.StringUtils
import java.util.*

@Configuration
@EnableConfigurationProperties(JaegerConfigurationProperties::class)
class TracingAutoConfiguration {

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
      .withTags(properties.determineTags())
      .withMetrics(metrics)

    return builder.build()
  }

  @Bean
  fun jaegerSampler(
    metrics: Metrics,
    properties: JaegerConfigurationProperties
  ): Sampler {
    if (properties.constSampler.decision != null) {
      return ConstSampler(properties.constSampler.decision)
    }

    if (properties.probabilisticSampler.samplingRate != null) {
      return ProbabilisticSampler(properties.probabilisticSampler.samplingRate)
    }

    if (properties.rateLimitingSampler.maxTracesPerSecond != null) {
      return RateLimitingSampler(properties.rateLimitingSampler.maxTracesPerSecond)
    }

    if (!StringUtils.isEmpty(properties.remoteControlledSampler.hostPort)) {
      val samplerProperties = properties.remoteControlledSampler
      var hostPort = samplerProperties.hostPort
      if (samplerProperties.host != null && !samplerProperties.host.isEmpty()) {
        hostPort = samplerProperties.host + ":" + samplerProperties.port
      }
      return RemoteControlledSampler.Builder(properties.serviceName)
        .withSamplingManager(HttpSamplingManager(hostPort))
        .withInitialSampler(
          ProbabilisticSampler(samplerProperties.samplingRate)
        )
        .withMetrics(metrics)
        .build()
    }

    //fallback to sampling every trace

    //fallback to sampling every trace
    return ConstSampler(true)
  }

  @ConditionalOnMissingBean
  @Bean
  fun reporter(
    properties: JaegerConfigurationProperties,
    metrics: Metrics,
    @Autowired(required = false) reporterAppender: ReporterAppender?
  ): Reporter? {
    val reporters: MutableList<Reporter> = LinkedList()
    val remoteReporter = properties.remoteReporter
    val httpSender = properties.httpSender
    if (!StringUtils.isEmpty(httpSender.url)) {
      reporters.add(getHttpReporter(metrics, remoteReporter, httpSender))
    } else {
      reporters.add(getUdpReporter(metrics, remoteReporter, properties.udpSender))
    }
    if (properties.isLogSpans) {
      reporters.add(LoggingReporter())
    }
    reporterAppender?.append(reporters)
    return CompositeReporter(*reporters.toTypedArray())
  }

  private fun getUdpReporter(
    metrics: Metrics,
    remoteReporter: JaegerConfigurationProperties.RemoteReporter,
    udpSenderProperties: JaegerConfigurationProperties.UdpSender
  ): Reporter {
    val udpSender = UdpSender(
      udpSenderProperties.host, udpSenderProperties.port,
      udpSenderProperties.maxPacketSize
    )
    return createReporter(metrics, remoteReporter, udpSender)
  }

  private fun getHttpReporter(
    metrics: Metrics,
    remoteReporter: JaegerConfigurationProperties.RemoteReporter,
    httpSenderProperties: JaegerConfigurationProperties.HttpSender
  ): Reporter {
    var builder = HttpSender.Builder(httpSenderProperties.url)
    if (httpSenderProperties.maxPayload != null) {
      builder = builder.withMaxPacketSize(httpSenderProperties.maxPayload)
    }
    if (!StringUtils.isEmpty(httpSenderProperties.username)
      && !StringUtils.isEmpty(httpSenderProperties.password)
    ) {
      builder.withAuth(httpSenderProperties.username, httpSenderProperties.password)
    } else if (!StringUtils.isEmpty(httpSenderProperties.authToken)) {
      builder.withAuth(httpSenderProperties.authToken)
    }
    return createReporter(metrics, remoteReporter, builder.build())
  }

  private fun createReporter(
    metrics: Metrics,
    remoteReporter: JaegerConfigurationProperties.RemoteReporter, udpSender: Sender
  ): Reporter {
    val builder = RemoteReporter.Builder()
      .withSender(udpSender)
      .withMetrics(metrics)
    if (remoteReporter.flushInterval != null) {
      builder.withFlushInterval(remoteReporter.flushInterval)
    }
    if (remoteReporter.maxQueueSize != null) {
      builder.withMaxQueueSize(remoteReporter.maxQueueSize)
    }
    return builder.build()
  }

  @Bean
  fun metrics(metricsFactory: MetricsFactory): Metrics {
    return Metrics(metricsFactory)
  }

  @Bean
  fun metricsFactory(): MetricsFactory {
    return NoopMetricsFactory()
  }

}

inline fun runInSpan(
  tracer: brave.Tracer,
  name: String,
  traceId: Long? = null,
  block: (span: Span) -> Unit
) {
  var builder = TraceContext.newBuilder().traceId(traceId ?: Random().nextLong()).spanId(Random().nextLong())
  val context = builder.build()
  val span = tracer.nextSpan(TraceContextOrSamplingFlags.create(context)).name(name).start()
  tracer.withSpanInScope(span)
  block(span)
  span.finish()
}
