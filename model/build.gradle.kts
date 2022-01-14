plugins {
    id("com.google.devtools.ksp").version("1.6.10-1.0.2")
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
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")

    implementation("com.squareup.moshi:moshi:1.13.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
}

