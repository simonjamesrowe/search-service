import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  id("org.springframework.boot") version "2.4.4"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id("maven-publish")
  id("org.sonarqube") version "3.1.1"
  id("jacoco")
  id("org.springframework.experimental.aot") version "0.9.2-SNAPSHOT"
  kotlin("jvm") version "1.4.31"
  kotlin("plugin.spring") version "1.4.31"
}
val gradlePropertiesProp = project.properties

group = "com.simonjamesrowe"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
  maven { url = uri("https://nexus-jx.simonjamesrowe.com/repository/maven-group/") }
  mavenCentral()
  maven { url = uri("https://repo.spring.io/milestone") }
  maven { url = uri("https://repo.spring.io/release") }
  maven { url = uri("https://repo.spring.io/snapshot") }
}

extra["springCloudVersion"] = "2020.0.2"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("io.projectreactor.netty:reactor-netty")
  implementation("org.springframework.experimental:spring-native:0.9.2-SNAPSHOT")
  //implementation("de.qaware.tools.openapi-generator-for-spring:openapi-generator-for-spring-webflux:1.0.1")
  implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
  implementation("com.simonjamesrowe:model:0.0.19")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.springframework.kafka:spring-kafka")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("com.ninja-squad:springmockk:3.0.1")
  testImplementation("org.awaitility:awaitility:4.0.3")
  testImplementation("com.simonjamesrowe:component-test:0.0.11")
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
    jvmTarget = "11"
  }
}
tasks.withType<Test> {
  useJUnitPlatform()
  minHeapSize = "2g"
  maxHeapSize = "4g"
  jvmArgs("-agentlib:native-image-agent=access-filter-file=src/test/resources/access-filter.json,config-output-dir=build/classes/kotlin/main/META-INF/native-image")
  finalizedBy(tasks.jacocoTestReport)
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
  dependsOn(tasks.test)
}

tasks.getByName<BootBuildImage>("bootBuildImage") {
  builder = "paketobuildpacks/builder:tiny"
  environment = mapOf(
    "BP_NATIVE_IMAGE" to "true"
  )
  imageName = "harbor.simonjamesrowe.com/simonjamesrowe/${project.name}:${project.version}"
}

sonarqube {
  properties {
    property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/reports/jacoco/test/jacocoTestReport.xml")
    property("sonar.host.url", gradlePropertiesProp["sonar.host.url"] ?: "")
    property("sonar.login", gradlePropertiesProp["sonar.login"] ?: "")
  }
}
