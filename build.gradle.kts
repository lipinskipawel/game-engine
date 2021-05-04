plugins {
    java
    `maven-publish`
    signing
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testImplementation("org.assertj:assertj-core:3.15.0")
}

group = "com.github.lipinskipawel"
version = "4.0.1"
description = "game-engine"
java.sourceCompatibility = JavaVersion.VERSION_12

publishing {
    publications.create<MavenPublication>("main") {
        pom {
            name.set("game-engine")
            description.set("This is engine for 2D football game")
            url.set("https://github.com/lipinskipawel/game-engine")
            licenses {
                license {
                    name.set("MIT License")
                    url.set("http://www.opensource.org/licenses/mit-license.php")
                }
            }
            developers {
                developer {
                    name.set("Pawel Lipinski")
                }
            }
            scm {
                connection.set("scm:git:ssh://git@github.com/lipinskipawel/game-engine.git")
                developerConnection.set("scm:git:ssh://git@github.com/lipinskipawel/game-engine.git")
                url.set("git@github.com:lipinskipawel/game-engine")
            }
        }
        from(components["java"])
    }
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = property("OSSRH_USERNAME").toString()
                password = property("OSSRH_PASSWORD").toString()
            }
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

signing {
    useGpgCmd()
    sign(publishing.publications["main"])
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
