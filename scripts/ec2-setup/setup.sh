sudo apt update

sudo apt-get install openjdk-11-jdk

sudo apt install maven

cd /home/ubuntu

git clone https://github.com/med-united/connector-admin.med-united.health.git

sudo apt install postgresql postgresql-contrib

sudo systemctl start postgresql.service

wget https://github.com/wildfly/wildfly/releases/download/28.0.1.Final/wildfly-28.0.1.Final.tar.gz

sudo tar xvf wildfly-28.0.1.Final.tar.gz

# Setup wildfly to run as a service
sudo groupadd --system wildfly
sudo useradd -s /sbin/nologin --system -d /opt/wildfly  -g wildfly wildfly

sudo mkdir /etc/wildfly

sudo cp /opt/wildfly/docs/contrib/scripts/systemd/wildfly.conf /etc/wildfly/
sudo cp /opt/wildfly/docs/contrib/scripts/systemd/wildfly.service /etc/systemd/system/
sudo cp /opt/wildfly/docs/contrib/scripts/systemd/launch.sh /opt/wildfly/bin/
sudo chmod +x /opt/wildfly/bin/launch.sh

sudo chown -R wildfly:wildfly /opt/wildfly

sudo systemctl daemon-reload

sudo systemctl start wildfly
sudo systemctl enable wildfly

# Check the status of wildfly service
sudo systemctl status wildfly

# For accessing WildFly Admin Console from Web Interface
sudo vim /opt/wildfly/bin/launch.sh

# Modfiy launch.sh like this:
#!/bin/bash

# if [ "x$WILDFLY_HOME" = "x" ]; then
#     WILDFLY_HOME="/opt/wildfly"
# fi

# if [[ "$1" == "domain" ]]; then
#     $WILDFLY_HOME/bin/domain.sh -c $2 -b $3
# else
#     $WILDFLY_HOME/bin/standalone.sh -c $2 -b $3 -bmanagement=0.0.0.0
# fi

# Add wildfly user
sudo /opt/wildfly/bin/add-user.sh
