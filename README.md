# java-cluster-demo

```bash
mvn package 
java -jar target/java-cluster-demo-0.0.1-SNAPSHOT.jar
java -jar target/java-cluster-demo-0.0.1-SNAPSHOT.jar --server.port=8091
java -jar target/java-cluster-demo-0.0.1-SNAPSHOT.jar --server.port=8092
```

Feel free to add or remove nodes and see how state replicates through the various nodes.

```bash
# To add key pair
curl localhost:8090/foo/bar
# To read key pair from different remote host
curl localhost:8091/foo
```

Note please modify your /etc/hosts file to run locally
```bash
echo "127.0.0.1 join.internal.com" >> /etc/hosts
```

To Test endpoint
```bash
curl http://dist-keys-elb-795693697.us-west-2.elb.amazonaws.com/foo/bar
curl http://dist-keys-elb-795693697.us-west-2.elb.amazonaws.com/foo
curl http://dist-keys-elb-795693697.us-west-2.elb.amazonaws.com/foo/boo
curl http://dist-keys-elb-795693697.us-west-2.elb.amazonaws.com/foo
```
