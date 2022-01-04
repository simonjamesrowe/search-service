import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  id("org.springframework.boot") version "2.6.2"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  kotlin("jvm") version "1.6.10"
  kotlin("plugin.spring") version "1.6.10"
  id("maven-publish")
  id("org.sonarqube") version "3.1.1"
  id("jacoco")
}
val gradlePropertiesProp = project.properties

group = "com.simonjamesrowe"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
  maven { url = uri("https://nexus.simonjamesrowe.com/repository/maven-public/") }
  mavenCentral()
  maven { url = uri("https://repo.spring.io/milestone") }
  maven { url = uri("https://repo.spring.io/release") }
  maven { url = uri("https://repo.spring.io/snapshot") }
}

extra["springCloudVersion"] = "2021.0.0"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("io.projectreactor.netty:reactor-netty")
  implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
  implementation("com.simonjamesrowe:model:0.0.20")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.springframework.kafka:spring-kafka")
  testImplementation("io.mockk:mockk:1.12.1")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.awaitility:awaitility:4.0.3")
  testImplementation("com.simonjamesrowe:component-test:0.3.0") {
    exclude(group = "org.hibernate.validator")
  }
  testImplementation("com.tyro.oss:arbitrater:1.0.0")
  testImplementation("org.jeasy:easy-random-core:5.0.0")
}

dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "17"
  }
}

tasks.register<Delete>("deleteSerializationConfig") {
  delete(files("build/classes/kotlin/main/META-INF/native-image/com.simonjamesrowe/serialization-config.json"))
}

tasks.withType<Test> {
  useJUnitPlatform()
  minHeapSize = "2g"
  maxHeapSize = "4g"
  jvmArgs("-agentlib:native-image-agent=access-filter-file=src/test/resources/access-filter.json,caller-filter-file=src/test/resources/access-filter.json,config-output-dir=build/classes/kotlin/main/META-INF/native-image/com.simonjamesrowe")
  finalizedBy(
    tasks.jacocoTestReport,
    tasks.getByName<Delete>("deleteSerializationConfig")
  )
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    xml.isEnabled = true
  }
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
    }
  }
}

tasks.getByName<BootJar>("bootJar") {
  dependsOn(
    tasks.test,
    tasks.getByName<Delete>("deleteSerializationConfig")
  )
}

tasks.getByName<BootBuildImage>("bootBuildImage") {
  builder = "paketobuildpacks/builder:tiny"
  environment = mapOf(
    "BP_NATIVE_IMAGE" to "true",
    "BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to "--allow-incomplete-classpath --initialize-at-build-time=sun.instrument.InstrumentationImpl,org.aopalliance.aop.Advice,org.apache.commons.logging.LogAdapter,org.apache.commons.logging.LogAdapter\$1,org.apache.commons.logging.LogAdapter\$Log4jLog,org.apache.commons.logging.LogAdapter\$Slf4jLocationAwareLog,org.apache.commons.logging.LogFactory,org.slf4j.Logger,org.slf4j.LoggerFactory,org.slf4j.MDC,org.slf4j.event.EventRecodingLogger,org.slf4j.event.SubstituteLoggingEvent,org.slf4j.helpers.FormattingTuple,org.slf4j.helpers.MessageFormatter,org.slf4j.helpers.NOPLogger,org.slf4j.helpers.NOPLoggerFactory,org.slf4j.helpers.SubstituteLogger,org.slf4j.helpers.SubstituteLoggerFactory,org.slf4j.helpers.Util,org.slf4j.impl.StaticLoggerBinder,org.slf4j.spi.LocationAwareLogger,org.springframework.aop.Advisor,org.springframework.aop.Advisor\$1,org.springframework.aop.TargetSource,org.springframework.aop.framework.Advised,org.springframework.aot.StaticSpringFactories,org.springframework.beans.CachedIntrospectionResults,org.springframework.beans.PropertyEditorRegistrySupport,org.springframework.beans.factory.xml.XmlBeanDefinitionReader,org.springframework.boot.BeanDefinitionLoader,org.springframework.boot.logging.LoggingSystem,org.springframework.boot.logging.java.JavaLoggingSystem\$Factory,org.springframework.boot.logging.log4j2.Log4J2LoggingSystem\$Factory,org.springframework.boot.logging.logback.LogbackLoggingSystem,org.springframework.boot.logging.logback.LogbackLoggingSystem\$Factory,org.springframework.context.annotation.CommonAnnotationBeanPostProcessor,org.springframework.context.event.EventListenerMethodProcessor,org.springframework.context.support.AbstractApplicationContext,org.springframework.core.DecoratingProxy,org.springframework.core.DefaultParameterNameDiscoverer,org.springframework.core.KotlinDetector,org.springframework.core.NativeDetector,org.springframework.core.ReactiveAdapterRegistry,org.springframework.core.ResolvableType,org.springframework.core.SpringProperties,org.springframework.core.annotation.AnnotationFilter,org.springframework.core.annotation.AnnotationFilter\$1,org.springframework.core.annotation.AnnotationFilter\$2,org.springframework.core.annotation.AnnotationUtils,org.springframework.core.annotation.PackagesAnnotationFilter,org.springframework.core.annotation.TypeMappedAnnotations,org.springframework.core.io.support.PropertiesLoaderUtils,org.springframework.core.io.support.ResourcePropertiesPersister,org.springframework.core.io.support.SpringFactoriesLoader,org.springframework.format.annotation.DateTimeFormat\$ISO,org.springframework.http.HttpStatus,org.springframework.http.MediaType,org.springframework.http.codec.CodecConfigurerFactory,org.springframework.http.codec.support.BaseDefaultCodecs,org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter,org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter,org.springframework.integration.support.json.Jackson2JsonObjectMapper,org.springframework.jdbc.datasource.ConnectionProxy,org.springframework.jdbc.support.JdbcAccessor,org.springframework.jdbc.support.JdbcTransactionManager,org.springframework.kafka.support.JacksonPresent,org.springframework.kafka.support.JacksonUtils,org.springframework.kafka.support.KafkaUtils,org.springframework.messaging.simp.config.AbstractMessageBrokerConfiguration,org.springframework.nativex.AotModeDetector,org.springframework.nativex.substitutions.boot.NativeSpringBootVersion,org.springframework.transaction.annotation.Isolation,org.springframework.transaction.annotation.Propagation,org.springframework.util.Assert,org.springframework.util.ClassUtils,org.springframework.util.CollectionUtils,org.springframework.util.ConcurrentReferenceHashMap,org.springframework.util.DefaultPropertiesPersister,org.springframework.util.LinkedCaseInsensitiveMap,org.springframework.util.MimeType,org.springframework.util.ReflectionUtils,org.springframework.util.StringUtils,org.springframework.util.unit.DataSize,org.springframework.util.unit.DataUnit,org.springframework.web.client.RestTemplate,org.springframework.web.reactive.function.client.DefaultWebClientBuilder,org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport,org.springframework.web.servlet.function.support.RouterFunctionMapping,org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver,org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter,ch.qos.logback.classic,ch.qos.logback.classic.util,ch.qos.logback.core,org.apache.logging.log4j,org.apache.logging.slf4j,org.jboss.logging,org.springframework.core.env  -H:+AddAllCharsets --verbose",
  )
  imageName = "harbor.simonjamesrowe.com/simonjamesrowe/${project.name}:${project.version}"
  docker {
    publishRegistry {
      username = gradlePropertiesProp["publishRegistryUsername"] as String? ?: ""
      password = gradlePropertiesProp["publishRegistryPassword"] as String? ?: ""
      url = "https://${gradlePropertiesProp["publishingRegistryUrl"]}"
      email = gradlePropertiesProp["publishingRegistryEmail"] as String? ?: ""
    }
  }
}

sonarqube {
  properties {
    property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/reports/jacoco/test/jacocoTestReport.xml")
    property("sonar.host.url", gradlePropertiesProp["sonar.host.url"] ?: "")
    property("sonar.login", gradlePropertiesProp["sonar.login"] ?: "")
  }
}
