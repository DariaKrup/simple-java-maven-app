package _Self.pipelines

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.pipelines.*
import jetbrains.buildServer.configs.kotlin.triggers.vcs

object SimpleJavaMavenAppPipeline : Pipeline({
    name = "Simple Java Maven App: pipeline"

    repositories {
        repository(DslContext.settingsRoot)
    }

    triggers {
        vcs {
            branchFilter = """
                +:*
                +pr:*
            """.trimIndent()
        }
    }

    job(SimpleJavaMavenAppPipeline_Job1)
    job(SimpleJavaMavenAppPipeline_Job1_2)
    job(SimpleJavaMavenAppPipeline_Job1_3)
    job(SimpleJavaMavenAppPipeline_Job1_4)
    job(SimpleJavaMavenAppPipeline_Report)
})

object SimpleJavaMavenAppPipeline_Job1 : Job({
    id("Job1")
    name = "Tests"
    allowReuse = true
    enableDependencyCacheOptimization = false

    steps {
        maven {
            name = "Tests"
            goals = "clean test"
        }
    }

    requirements {
        doesNotEqual("teamcity.agent.jbHosted", "true")
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})

object SimpleJavaMavenAppPipeline_Job1_2 : Job({
    id("Job1_2")
    name = "Package"
    allowReuse = false
    enableDependencyCacheOptimization = false

    dependency(SimpleJavaMavenAppPipeline_Job1)

    steps {
        maven {
            name = "clean package"
            goals = "clean package"
        }
    }

    requirements {
        doesNotEqual("teamcity.agent.jbHosted", "true")
        equals("teamcity.agent.jvm.os.name", "Linux")
    }

    outputFiles {
        sharedWithJobs("./my-app-1.0-SNAPSHOT.jar")
        pipelineArtifacts("./my-app-1.0-SNAPSHOT.jar")
    }
})

object SimpleJavaMavenAppPipeline_Job1_3 : Job({
    id("Job1_3")
    name = "Install app"
    allowReuse = true
    enableDependencyCacheOptimization = false

    dependency(SimpleJavaMavenAppPipeline_Job1_2)

    steps {
        maven {
            name = "App installation"
            goals = "install:install-file"
            runnerArgs = "-Dfile=./my-app-1.0-SNAPSHOT.jar -DgroupId=com.mycompany.app -DartifactId=my-app -Dversion=1.0-SNAPSHOT -Dpackaging=jar"
        }
    }

    requirements {
        doesNotEqual("teamcity.agent.jbHosted", "true")
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})

object SimpleJavaMavenAppPipeline_Job1_4 : Job({
    id("Job1_4")
    name = "Successful report"
    allowReuse = true
    enableDependencyCacheOptimization = false

    dependency(SimpleJavaMavenAppPipeline_Job1_3)

    steps {
        script {
            name = "Report"
            scriptContent = """echo "Successful lifecycle""""
        }
    }

    requirements {
        doesNotEqual("teamcity.agent.jbHosted", "true")
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})

object SimpleJavaMavenAppPipeline_Report : Job({
    id("Report")
    name = "Report"
    allowReuse = true
    enableDependencyCacheOptimization = false

    dependency(SimpleJavaMavenAppPipeline_Job1)

    steps {
        script {
            name = "Success report"
            scriptContent = "echo 'Successful tests'"
        }
    }

    requirements {
        doesNotEqual("teamcity.agent.jbHosted", "true")
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
