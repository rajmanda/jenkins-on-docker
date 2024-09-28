import jenkins.model.*
import hudson.security.*
import com.cloudbees.jenkins.plugins.sshcredentials.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import hudson.util.Secret


def instance = Jenkins.getInstance()
def domain = Domain.global()

println("Starting add-ssh-credentials.groovy")

// Specify the path to your private key
def privateKeyFile = new File("/var/jenkins_home/.ssh/id_rsa") // Adjust this path as necessary

// Check if the private key file exists
if (!privateKeyFile.exists()) {
    println("Private key file does not exist: ${privateKeyFile.path}")
    return // Exit the script if the file does not exist
}

// Read the private key content
def privateKey = privateKeyFile.text.trim()
println("Private Key Length: ${privateKey.length()}") // Debugging the private key length

// Create SSH User with Private Key credentials
def sshCredentials = new BasicSSHUserPrivateKey(
    CredentialsScope.GLOBAL,
    "GIT_CREDENTIALS", // ID
    "your-username", // Username (change as needed)
    new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(privateKey),
    "", // Passphrase (leave empty if none)
    "Git Credentials" // Description
)

println("Creating SSH credentials with ID: GIT_CREDENTIALS") // Debugging the credentials creation

// Add the credentials to the Jenkins instance
try {
    CredentialsProvider.lookupStores(instance).each { store ->
        store.addCredentials(domain, sshCredentials)
        println("SSH credentials added successfully to store: ${store.toString()}")
    }
} catch (Exception e) {
    println("Failed to add SSH credentials: ${e.message}")
}

instance.save()
println("Jenkins instance saved after adding credentials.")

