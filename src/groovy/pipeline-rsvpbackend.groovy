import jenkins.model.*
import hudson.model.*
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import hudson.plugins.git.*

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

// Set up the Git repository
def scm = new GitSCM(
    GitSCM.createRepoList("git@github.com:rajmanda/rsvpbackend.git", null),
    Collections.singletonList(new BranchSpec("*/main")),
    false,
    Collections.emptyList(),
    null,
    null,
    Collections.emptyList()
)
job.definition = new CpsScmFlowDefinition(scm, "Jenkinsfile")

// Save the job configuration
job.save()

println "Pipeline job '${jobName}' created successfully."

// Optionally, trigger the first build
job.scheduleBuild(0)
