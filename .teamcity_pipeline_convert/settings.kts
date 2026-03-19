import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.pipelines.*
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2025.11"

project {

    pipeline(DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding)
}


object DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding : Pipeline({
    id("PipelineAppBuilding")
    name = "Pipeline: App building"

    repositories {
        repository(DslContext.settingsRoot)
    }

    triggers {
        vcs {
            branchFilter = """
                -pr:*
                +:*
            """.trimIndent()
        }
    }

    job(DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Job1)
    job(DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Job1_2)
    job(DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Job1_3)
    job(DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Job1_4)
    job(DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Job1_5)
    job(DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Report)
})

object DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Job1 : Job({
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

object DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Job1_2 : Job({
    id("Job1_2")
    name = "Package"
    allowReuse = false
    enableDependencyCacheOptimization = false

    dependency(DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Job1)

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

object DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Job1_3 : Job({
    id("Job1_3")
    name = "Install app"
    allowReuse = true
    enableDependencyCacheOptimization = false

    dependency(DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Job1_2)

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

object DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Job1_4 : Job({
    id("Job1_4")
    name = "Successful report"
    allowReuse = true
    enableDependencyCacheOptimization = false

    dependency(DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Job1_3)

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

object DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Job1_5 : Job({
    id("Job1_5")
    name = "Reporting"
    allowReuse = true
    enableDependencyCacheOptimization = false

    steps {
        script {
            name = "CMD"
            scriptContent = """echo "Cool Job""""
        }
    }
})

object DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Report : Job({
    id("Report")
    name = "Report"
    allowReuse = true
    enableDependencyCacheOptimization = false

    dependency(DSLMilestone3WithConvertation_PipelineWithMultipleJobs_PipelineAppBuilding_Job1)

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
