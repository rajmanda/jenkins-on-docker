import jenkins.model.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.domains.*

def instance = Jenkins.getInstance()
def domain = Domain.global()

println("Starting to add Docker Hub credentials...")

// Specify the username and password for Docker Hub
def username = "dockerrajmanda" // Docker Hub username
def password = "your_password_here" // Docker Hub password (change as needed)

// Create Username with Password credentials
def dockerHubCredentials = new UsernamePasswordCredentialsImpl(
    CredentialsScope.GLOBAL,
    "DOCKER_HUB_CREDENTIALS", // ID
    "Docker Hub Credentials", // Description
    username, // Username
    password // Password
)

println("Creating Docker Hub credentials with ID: DOCKER_HUB_CREDENTIALS") // Debugging the credentials creation

// Add the credentials to the Jenkins instance
try {
    CredentialsProvider.lookupStores(instance).each { store ->
        store.addCredentials(domain, dockerHubCredentials)
        println("Docker Hub credentials added successfully to store: ${store.toString()}")
    }
} catch (Exception e) {
    println("Failed to add Docker Hub credentials: ${e.message}")
}

instance.save()
println("Jenkins instance saved after adding Docker Hub credentials.")