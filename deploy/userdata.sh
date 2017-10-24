#!/bin/bash -v
apt-get update -y
sudo yum install java-1.8.0 -y
sudo yum remove java-1.7.0-openjdk -y
sudo hostname `hostname -f`

wget -O app.jar https://s3-us-west-2.amazonaws.com/ellery-artifacts/java-cluster-demo-0.0.1-SNAPSHOT.jar

java -jar app.jar
