import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
	id("org.springframework.boot") version "2.4.2"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	id("maven-publish")
	kotlin("jvm") version "1.4.21"
	kotlin("plugin.spring") version "1.4.21"
}

group = "com.simonjamesrowe"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	maven { url = uri("https://nexus-jx.simonjamesrowe.com/repository/maven-group/") }
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

extra["springCloudVersion"] = "2020.0.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("de.qaware.tools.openapi-generator-for-spring:openapi-generator-for-spring-starter:1.0.1")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
	implementation("io.github.openfeign:feign-jackson:11.0")
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
	implementation("com.simonjamesrowe:model:0.0.8")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.ninja-squad:springmockk:3.0.1")
	testImplementation("org.awaitility:awaitility:4.0.3")
	testImplementation("com.simonjamesrowe:component-test:0.0.8")
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
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])
		}
	}
}

tasks.getByName<BootBuildImage>("bootBuildImage") {
	imageName = "harbor.simonjamesrowe.com/simonjamesrowe/${project.name}:${project.version}"
}
