package com.example.cluster.javaclusterdemo.services;

import com.example.cluster.javaclusterdemo.models.DataEvent;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LocalStateService {
    private Logger logger = Logger.getLogger(this.getClass());
    private Map<String, DataEvent> keysAndValues = new HashMap<>();

    void tryToPersistState(DataEvent event) {
        //Check to make sure we don't have a more recent value
        if (keysAndValues.containsKey(event.getKey())) {
            DataEvent localEvent = keysAndValues.get(event.getKey());
            if (localEvent.getTime_created() > event.getTime_created()) {
                logger.info("Have more recent version of key=" + event.getKey() + " not updating.");
            } else {
                //If passed key is more recent persist locally as well.
                logger.info("Persisting updated value to local state. key=" + event.getKey());
                keysAndValues.put(event.getKey(), event);
            }
        } else {
            //If key does not exist yet add to local state
            logger.info("Persisting new value to local state. key=" + event.getKey());
            keysAndValues.put(event.getKey(), event);
        }
    }

    public Map<String, DataEvent> getAllLocalKeysAndValues(){
        return this.keysAndValues;
    }

    public Object getValueByKey(String key){
        return keysAndValues.get(key).getValue();
    }
}
