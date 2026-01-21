package SubProject.vcsRoots

import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

object MavenUnbalancedRoot : GitVcsRoot({
    name = "MavenMessages repository"
    url = "git@github.com:DariaKrup/maven_unbalanced_messages.git"
    branch = "refs/heads/master"
    checkoutPolicy = AgentCheckoutPolicy.USE_MIRRORS
    authMethod = uploadedKey {
        uploadedKey = "id_rsa"
    }
})