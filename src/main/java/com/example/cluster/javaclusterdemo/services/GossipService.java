package com.example.cluster.javaclusterdemo.services;

import com.example.cluster.javaclusterdemo.models.DataEvent;
import io.scalecube.cluster.Cluster;
import io.scalecube.cluster.membership.MembershipEvent;
import io.scalecube.transport.Address;
import io.scalecube.transport.Message;
import io.scalecube.transport.TransportConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class GossipService {

    static Logger logger = Logger.getLogger(GossipService.class);
    private Cluster coreCluster;

    @Autowired
    LocalStateService localStateService;

    /**
     * Sets up local gossip cluster and attempts to connect to remote nodes. Sets up all event listeners needed for this
     * app.
     */
    @PostConstruct
    public void init() {
        coreCluster = Cluster.joinAwait(Address.create("join.internal.com", TransportConfig.DEFAULT_PORT));

        // Membership event listeners
        coreCluster.listenMembership()
                .subscribe(event -> {
                    logger.info("Received membership: " + event);
                    //If this is brand new node send it all the state so it can catch up.
                    handleNewNode(event);
                });

        // Data replication events.
        coreCluster.listenGossips().subscribe(msg -> {
            logger.debug("Received Message. " + msg);
            if (msg.data().getClass().equals(DataEvent.class)) {
                handleReplicatedState(msg.data());
            } else {
                logger.error("Received unhandled message");
            }
        });
        coreCluster.listen().subscribe(msg -> {
            logger.debug("Received Message. " + msg);
            if (msg.data().getClass().equals(DataEvent.class)) {
                handleReplicatedState(msg.data());
            } else {
                logger.error("Received unhandled message");
            }
        });
        logger.info("Successfully joined cluster. Other members:" + coreCluster.otherMembers());
    }

    /**
     * Handles replicating state throughout our cluster. Will first make sure state is persisted locally then it will
     * gossip out to all members of our cluster.
     *
     * @param event
     */
    public void replicateStateToOtherMembers(DataEvent event) {
        //First always store state locally
        localStateService.tryToPersistState(event);
        //Then gossip state to all other members.
        Message message = Message.fromData(event);
        coreCluster.spreadGossip(message);
    }

    /**
     * Handles state received from remote nodes and will attempt to save it locally if appropriate.
     *
     * @param event Inbound event containing updated value from remote node.
     */
    private void handleReplicatedState(DataEvent event) {
        logger.info("Got message: Key=" + event.getKey() + " Value=" + event.getValue());
        localStateService.tryToPersistState(event);
    }

    private void handleNewNode(MembershipEvent event) {
        if (event.type().equals(MembershipEvent.Type.ADDED)) {
            Map<String, DataEvent> keys = localStateService.getAllLocalKeysAndValues();
            logger.info("Saw new node added to cluster spreading local data to remote nodes. keysSent=" + keys.size());
            keys.forEach((k, v) -> coreCluster.spreadGossip(Message.fromData(v)));
        }
    }
}
