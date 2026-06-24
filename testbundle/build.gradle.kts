plugins {
    java
}

val extractCodeModelTests by tasks.registering(Copy::class) {
    val sourceDir = layout.projectDirectory.dir("../test/jdk")
    val targetDir = layout.buildDirectory.dir("extractedCodeModel")
    from(sourceDir) {
        include("jdk/incubator/code/**")
        exclude("jdk/incubator/code/*Test*.java")
        exclude("jdk/incubator/code/parser/Test*.java")
        exclude("jdk/incubator/code/bytecode/Test*.java")
        exclude("jdk/incubator/code/bytecode/lift/*.java")
        exclude("jdk/incubator/code/writer/Test*.java")
        exclude("jdk/incubator/code/interpreter/Test*.java")
        exclude("jdk/incubator/code/type/Test*.java")
        exclude("jdk/incubator/code/ad/Test*.java")
        exclude("jdk/incubator/code/linq/TestLinq.java")
        exclude("jdk/incubator/code/stream/TestStream.java")
    }
    into(targetDir)
    doFirst {
        targetDir.get().asFile.deleteRecursively()
    }
    doLast {
        targetDir.get().asFile
            .walk()
            .filter { it.isFile && it.extension == "java" }
            .forEach {
                val packageName = it.parentFile.relativeTo(targetDir.get().asFile).toString()
                    .replace('/', '.')
                it.writeText("package dev.jfronny.wrapped.$packageName;\n\n${it.readText()}")
            }
    }
}

sourceSets {
    main {
        java.srcDir(extractCodeModelTests)
    }
}

tasks.compileJava {
    options.compilerArgs.addAll(listOf("--add-modules", "jdk.incubator.code"))
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    compileOnly("org.junit.jupiter:junit-jupiter-params:5.10.0")
}
