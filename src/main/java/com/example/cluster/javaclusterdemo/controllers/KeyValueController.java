package com.example.cluster.javaclusterdemo.controllers;

import com.example.cluster.javaclusterdemo.models.DataEvent;
import com.example.cluster.javaclusterdemo.services.GossipService;
import com.example.cluster.javaclusterdemo.services.LocalStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class KeyValueController {

    @Autowired
    GossipService gossipService;

    @Autowired
    LocalStateService localStateService;

    private static final String responseTemplate = "{\"key\"=\"%s\",\"value\"=\"%s\"}";

    @RequestMapping("/{key}/{value}")
    public String setKey(@PathVariable("key") String key, @PathVariable("value") String value) {
        gossipService.replicateStateToOtherMembers(new DataEvent(key, value));
        return String.format(responseTemplate, key, value);
    }

    @RequestMapping("/{key}")
    public String getKey(@PathVariable("key") String key) {
        return String.format(responseTemplate, key, localStateService.getValueByKey(key));
    }
}