import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version GradlePluginVersions.SPRING_BOOT
    id("io.spring.dependency-management") version GradlePluginVersions.SPRING_DEPENDENCY_MANAGEMENT
    kotlin("jvm") version GradlePluginVersions.KOTLIN
    kotlin("plugin.spring") version GradlePluginVersions.KOTLIN
}

group = "com.github.matusewicz"
version = "0.1.0"
java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    implementation("org.javamoney:moneta:${DependencyVersions.JAVA_MONEY}")
    implementation("org.zalando:jackson-datatype-money:${DependencyVersions.JACKSON_MONEY}")
    implementation("javax.validation:validation-api:${DependencyVersions.VALIDATION_API}")

    implementation("org.zalando:problem-spring-web:${DependencyVersions.PROBLEM_VIOLATIONS_JSON}")

    testImplementation("org.assertj:assertj-core:${TestDependencyVersions.ASSERT_J}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${TestDependencyVersions.MOCKITO}")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}
tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed")
    }
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<AbstractArchiveTask> {
    setProperty("archiveFileName", "${project.name}.jar")
}