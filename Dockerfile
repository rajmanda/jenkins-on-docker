FROM jenkins/jenkins:lts

# Switch to root to install packages
USER root

# Install JDK 17 and Maven
RUN apt-get update && apt-get install -y openjdk-17-jdk maven && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Create a script to print environment variables and keep the container running
RUN echo '#!/bin/bash' > /usr/local/bin/print_env_vars.sh && \
    echo 'echo "JAVA_HOME: $JAVA_HOME"' >> /usr/local/bin/print_env_vars.sh && \
    echo 'echo "MAVEN_HOME: $MAVEN_HOME"' >> /usr/local/bin/print_env_vars.sh && \
    echo 'echo "PATH: $PATH"' >> /usr/local/bin/print_env_vars.sh && \
    echo 'echo "Maven Version:" && mvn -v' >> /usr/local/bin/print_env_vars.sh && \
    # Keep the container running with a default command
    #echo 'tail -f /dev/null' >> /usr/local/bin/print_env_vars.sh && \
    chmod +x /usr/local/bin/print_env_vars.sh

# Create the .ssh directory in Jenkins home with correct permissions
RUN mkdir -p /var/jenkins_home/.ssh && \
    chmod 700 /var/jenkins_home/.ssh && \
    chown -R jenkins:jenkins /var/jenkins_home/.ssh

# # Generate RSA key pair for Jenkins in /var/jenkins_home/.ssh
# RUN ssh-keygen -t rsa -b 2048 -f /var/jenkins_home/.ssh/id_rsa -q -N "" && \
#     chown jenkins:jenkins /var/jenkins_home/.ssh/id_rsa /var/jenkins_home/.ssh/id_rsa.pub && \
#     chmod 600 /var/jenkins_home/.ssh/id_rsa && \
#     chmod 644 /var/jenkins_home/.ssh/id_rsa.pub


# Create the init scripts and set permissions
COPY src/groovy/* /usr/share/jenkins/ref/init.groovy.d/

# Set ownership and make scripts executable
RUN chown jenkins:jenkins /usr/share/jenkins/ref/init.groovy.d/*.groovy && \
    chmod +x /usr/share/jenkins/ref/init.groovy.d/*.groovy

# Create entrypoint script for adding known hosts
COPY src/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Install plugins with specific versions
RUN jenkins-plugin-cli --plugins \
    git:latest \
    workflow-aggregator:latest \
    ssh-agent:latest \
    blueocean:latest \
    credentials-binding:latest \
    matrix-auth:latest

# Switch back to jenkins user
USER jenkins

# Set entrypoint script
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]


# #####  To Run Jenkins on local use this command. ######
# docker build --no-cache -t dockerrajmanda/jenkins:latest
# docker run -d -p 8080:8080 -p 50000:50000 --name jenkins -v ~/.ssh:/var/jenkins_home/.ssh:Z  dockerrajmanda/jenkins:latest  or docker-compose up -d

#-d: Runs the container in detached mode (in the background).
#-p 8080:8080: Maps port 8080 on your localhost to port 8080 in the container (Jenkins default web interface).
#-p 50000:50000: Maps port 50000 for Jenkins agents.
#--name jenkins: Names the container "jenkins".
#-v jenkins_home:/var/jenkins_home: Creates a named volume to persist Jenkins data.

######## RUN ngrok to Expose Jenkins
#docker run --net=host -it -e NGROK_AUTHTOKEN=2mW9616b5xPpcagIIBn3a0c4jFZ_2JeyoRDcpBitQCMxoSeWH ngrok/ngrok:latest http 8080