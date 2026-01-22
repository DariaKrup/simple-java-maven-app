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

object GradleConnectionRoot : GitVcsRoot({
    name = "GHConnection Repository"
    url = "https://github.com/DariaKrup/gradle-simple.git"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
    authMethod = token {
        userName = "oauth2"
        tokenId = "tc_token_id:CID_4b4df26346ed38498f51c0d6bee05baa:-1:9aeb4114-69a4-4eba-8c5a-8fafca765e79"
    }

})