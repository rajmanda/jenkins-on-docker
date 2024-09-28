import jenkins.model.*
import hudson.security.*

def instance = Jenkins.getInstance()
def strategy = new GlobalMatrixAuthorizationStrategy()

// Create admin user
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("admin", "admin")
instance.setSecurityRealm(hudsonRealm)

// Set authorization strategy
strategy.add(Jenkins.ADMINISTER, "admin")
instance.setAuthorizationStrategy(strategy)

instance.save()
