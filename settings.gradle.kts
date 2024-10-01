pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://devrepo.kakao.com/nexus/repository/kakaomap-releases/")  // 카카오맵 저장소 추가
    }
}


/*
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://devrepo.kakao.com/nexus/repository/kakaomap-releases/")  // 카카오맵 저장소 추가
    }
}
*/

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven (url = "https://devrepo.kakao.com/nexus/content/groups/public/" )
    }
}

rootProject.name = "HaeChorom"
include(":app")