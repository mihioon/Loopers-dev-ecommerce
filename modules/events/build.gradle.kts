dependencies {
    // Jackson for JSON serialization
    implementation(project(":supports:jackson"))
    
    // Spring Context for event publishing (optional)
    compileOnly("org.springframework:spring-context")
}