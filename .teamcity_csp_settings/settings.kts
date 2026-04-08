import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetTest
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

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
    description = "Check update and use of the old Context property"

    vcsRoot(HttpsGithubComDariaKrupNUnitTestsGitRefsHeadsMain)
    vcsRoot(HttpsGithubComDariaKrupRecipesFinderGitRefsHeadsMaster)

    buildType(BuildFromTemplateTestsOnly)
    buildType(BuildMavenAppGitHubContextPropertyAndStatusCheck)
    buildType(TestsNUnit)
    buildType(BuildRecipesFinder)
    buildType(BuildAndPackageFromTemplate)
    buildType(BuildMavenAppGitHubContextProperty)

    template(TemplateForMavenWithCsp)
}

object BuildAndPackageFromTemplate : BuildType({
    templates(TemplateForMavenWithCsp)
    name = "Build and package (from template)"

    steps {
        maven {
            name = "Package"
            id = "Package"
            goals = "clean package"
        }
    }

    triggers {
        vcs {
            id = "TRIGGER_5"
        }
    }
})

object BuildFromTemplateTestsOnly : BuildType({
    templates(TemplateForMavenWithCsp)
    name = "Build: from template (tests only)"

    triggers {
        vcs {
            id = "TRIGGER_4"
        }
    }
})

object BuildMavenAppGitHubContextProperty : BuildType({
    name = "Build: Maven App (GitHubContext property)"

    params {
        param("teamcity.commitStatusPublisher.githubContext", "Build: %build.number%")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            id = "Maven2"
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            mavenVersion = custom {
                path = "%teamcity.tool.maven.3.9.10%"
            }
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
        commitStatusPublisher {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = vcsRoot()
            }
        }
    }
})

object BuildMavenAppGitHubContextPropertyAndStatusCheck : BuildType({
    name = "Build: Maven App (GitHubContext property and status check)"

    params {
        text("parameter", "", display = ParameterDisplay.PROMPT, allowEmpty = true)
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            id = "Maven2"
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            mavenVersion = custom {
                path = "%teamcity.tool.maven.3.9.10%"
            }
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
        commitStatusPublisher {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            publisher = github {
                statusCheckName = "New property from %system.agent.name% and %parameter%"
                githubUrl = "https://api.github.com"
                authType = vcsRoot()
            }
        }
    }
})

object BuildRecipesFinder : BuildType({
    name = "Build: RecipesFinder"

    vcs {
        root(HttpsGithubComDariaKrupRecipesFinderGitRefsHeadsMaster)
    }

    steps {
        gradle {
            id = "gradle_runner"
            tasks = "clean build"
            gradleWrapperPath = ""
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        commitStatusPublisher {
            vcsRootExtId = "${HttpsGithubComDariaKrupRecipesFinderGitRefsHeadsMaster.id}"
            publisher = github {
                statusCheckName = "Build: RecipesFinder"
                githubUrl = "https://api.github.com"
                authType = vcsRoot()
            }
        }
    }
})

object TestsNUnit : BuildType({
    name = "Tests: NUnit"

    vcs {
        root(HttpsGithubComDariaKrupNUnitTestsGitRefsHeadsMain)
    }

    steps {
        dotnetTest {
            id = "dotnet"
            projects = "NUnitTests.sln"
            sdk = "8"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
        commitStatusPublisher {
            vcsRootExtId = "${HttpsGithubComDariaKrupNUnitTestsGitRefsHeadsMain.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = vcsRoot()
            }
        }
    }
})

object TemplateForMavenWithCsp : Template({
    name = "Template for Maven with CSP"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "Clean and test"
            id = "Clean_and_test"
            goals = "clean test"
        }
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_4"
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            publisher = github {
                statusCheckName = "Build: %system.teamcity.buildType.id%; #%build.number%"
                githubUrl = "https://api.github.com"
                authType = vcsRoot()
            }
        }
    }
})

object HttpsGithubComDariaKrupNUnitTestsGitRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/DariaKrup/NUnitTests.git#refs/heads/main"
    url = "https://github.com/DariaKrup/NUnitTests.git"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        password = "credentialsJSON:c5f4d3da-1a1b-427a-9b4e-abb308436af9"
    }
})

object HttpsGithubComDariaKrupRecipesFinderGitRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/DariaKrup/RecipesFinder.git#refs/heads/master"
    url = "https://github.com/DariaKrup/RecipesFinder.git"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
    authMethod = password {
        password = "credentialsJSON:c5f4d3da-1a1b-427a-9b4e-abb308436af9"
    }
})
