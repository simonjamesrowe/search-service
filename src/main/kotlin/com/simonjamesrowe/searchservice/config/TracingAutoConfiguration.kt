package com.simonjamesrowe.searchservice.config

import brave.Span
import brave.propagation.TraceContext
import brave.propagation.TraceContextOrSamplingFlags
import java.util.*

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
