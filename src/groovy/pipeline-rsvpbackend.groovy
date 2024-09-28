import jenkins.model.*
import hudson.model.*
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import hudson.plugins.git.*
import hudson.triggers.SCMTrigger

// Define the Jenkins instance and the name for the new pipeline job
def instance = Jenkins.getInstance()
def jobName = "rsvpbackend-pipeline"

// Check if the job already exists
def existingJob = instance.getItem(jobName)
if (existingJob != null) {
    println "Job '${jobName}' already exists. Aborting."
    return
}

// Create a new pipeline job
def job = instance.createProject(WorkflowJob, jobName)

// Set Git repository information
def repoUrl = 'git@github.com:rajmanda/rsvpbackend.git'
def branch = 'main'  // Change branch if necessary
def credentialsId = 'GIT_CREDENTIALS' // Use your Git SSH credentials ID

// Set the pipeline definition to use the Jenkinsfile from the repo
def scm = new GitSCM(
    repoUrl,               // Git repository URL
    [new BranchSpec(branch)], // Branch to build
    false,                  // Do not configure repository browser
    Collections.emptyList(), // Submodule configs
    null,                    // Git browser (not needed)
    credentialsId            // Credentials ID to access repo
)
job.definition = new CpsScmFlowDefinition(scm, "Jenkinsfile")

// Save the job configuration
job.save()

println "Pipeline job '${jobName}' created successfully."

// Optionally, trigger the first build
job.scheduleBuild(0)
