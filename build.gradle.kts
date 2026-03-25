import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.changelog.Changelog
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.13.1"
    id("org.jetbrains.changelog") version "2.1.2"
}

group = "com.kinglozzer"
version = "1.1.1"

sourceSets {
    main {
        java.srcDirs("src/main/java", "src/main/gen")
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.3")
        testFramework(TestFrameworkType.Platform)
    }
    testImplementation("junit:junit:4.13.2")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            untilBuild.set(provider { null })
        }

        changeNotes.set(provider {
            changelog.renderItem(
                changelog
                    .getLatest()
                    .withHeader(false)
                    .withEmptySections(false),
                Changelog.OutputType.HTML
            )
        })
    }
}

tasks {
    test {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
}

changelog {
    version.set("1.1.1")
    path.set(file("CHANGELOG.md").canonicalPath)
    header.set(provider { "${version.get()}" })
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed"))
}
