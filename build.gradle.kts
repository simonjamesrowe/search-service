import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  id("org.springframework.boot") version "2.6.2"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  kotlin("jvm") version "1.6.10"
  kotlin("plugin.spring") version "1.6.10"
  id("org.springframework.experimental.aot") version "0.10.5"
  id("maven-publish")
  id("org.sonarqube") version "3.1.1"
  id("jacoco")
}
val gradlePropertiesProp = project.properties

group = "com.simonjamesrowe"
java.sourceCompatibility = JavaVersion.VERSION_16

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
  implementation("com.simonjamesrowe:model:0.0.21")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.springframework.kafka:spring-kafka")
  testImplementation("io.mockk:mockk:1.12.1")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.awaitility:awaitility:4.0.3")
  testImplementation("com.simonjamesrowe:component-test:0.3.1") {
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
    jvmTarget = "16"
  }
}

tasks.register<Delete>("deleteSerializationConfig") {
  delete(files("${project.buildDir}/resources/aot/META-INF/native-image/org.springframework.aot/spring-aot/serialization-config.json"))
}

tasks.withType<Test> {
  useJUnitPlatform()
  minHeapSize = "2g"
  maxHeapSize = "4g"
  //will be aotMain in next version
  jvmArgs("-agentlib:native-image-agent=access-filter-file=src/test/resources/access-filter.json,caller-filter-file=src/test/resources/access-filter.json,config-merge-dir=${project.buildDir}/resources/aot/META-INF/native-image/org.springframework.aot/spring-aot")
  finalizedBy(tasks.jacocoTestReport, tasks.getByName<Delete>("deleteSerializationConfig"))
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
  dependsOn(tasks.test, tasks.getByName<Delete>("deleteSerializationConfig"))
}

//
//tasks.getByName<Jar>("aotMainJar") {
//  dependsOn(tasks.test, tasks.getByName<Delete>("deleteSerializationConfig"))
//}

tasks.getByName<BootBuildImage>("bootBuildImage") {
  builder = "paketobuildpacks/builder:tiny"
  environment = mapOf(
    "BP_NATIVE_IMAGE" to "true",
    "BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to "--allow-incomplete-classpath --initialize-at-build-time=sun.instrument.InstrumentationImpl --initialize-at-run-time=io.netty.internal.tcnative.SSLPrivateKeyMethod -H:+AddAllCharsets --enable-url-protocols=http --verbose"
  )
  buildpacks = listOf("gcr.io/paketo-buildpacks/java-native-image:5.5.0")
  imageName = "harbor.simonjamesrowe.com/simonjamesrowe/${project.name}:${project.version}"
  docker {
    publishRegistry {
      username = gradlePropertiesProp["publishRegistryUsername"] as String? ?: ""
      password = gradlePropertiesProp["publishRegistryPassword"] as String? ?: ""
      url = "https://${gradlePropertiesProp["publishingRegistryUrl"]}"
      email = gradlePropertiesProp["publishingRegistryEmail"] as String?  ?: ""
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

springAot {
  failOnMissingSelectorHint.set(false)
}