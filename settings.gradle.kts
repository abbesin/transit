pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
            maven {
                url = uri( "https://api.mapbox.com/downloads/v2/releases/maven")
                authentication {
                    create<BasicAuthentication>("basic")
                }
                credentials {
                    // Do not change the username below.
                    // This should always be `mapbox` (not your username).
                    username = "mapbox"
                    // Use the secret token you stored in gradle.properties as the password
                    password = "sk.eyJ1IjoidzA0NzI4MjYiLCJhIjoiY2xwMDMwbGp6MDN3ZTJscHFyaGx4a2IwYyJ9.rGKe6xLKFmiiPVnvxGp3ZQ"
                }
            }
    }
}

rootProject.name = "TransitApp"
include(":app")
 