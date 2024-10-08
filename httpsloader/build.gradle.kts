plugins {
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            version = "1.2"

            from(components["java"])
        }
    }
}

dependencies {
    implementation(project(":model"))
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.moshi:moshi:1.14.0")
}