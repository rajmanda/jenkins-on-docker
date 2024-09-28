#!/bin/bash

# Add GitHub.com and hub.docker.com to known_hosts for SSH
if [ ! -f /var/jenkins_home/.ssh/known_hosts ]; then
    ssh-keyscan github.com >> /var/jenkins_home/.ssh/known_hosts
    ssh-keyscan hub.docker.com >> /var/jenkins_home/.ssh/known_hosts
    chown jenkins:jenkins /var/jenkins_home/.ssh/known_hosts
    chmod 644 /var/jenkins_home/.ssh/known_hosts
fi

# Start Jenkins normally
exec /usr/bin/tini -- /usr/local/bin/jenkins.sh
