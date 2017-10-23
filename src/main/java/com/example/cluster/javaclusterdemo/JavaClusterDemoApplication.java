package com.example.cluster.javaclusterdemo;

import io.scalecube.cluster.Cluster;
import io.scalecube.transport.Address;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaClusterDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaClusterDemoApplication.class, args);
    }
}
