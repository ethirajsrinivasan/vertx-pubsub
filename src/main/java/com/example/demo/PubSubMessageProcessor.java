package com.example.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import java.io.FileInputStream;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

public class PubSubMessageProcessor extends AbstractVerticle {


    private JsonObject pubsubConfig;
    private Publisher client;

    @Override
    public void start() throws Exception
    {
        pubsubConfig = config();
        client = createClient();
        
        // register handler
        EventBus eb = vertx.eventBus();
        eb.consumer("pubsub", jsonObjectMessage -> {
            sendMessageToPubSub(jsonObjectMessage);
        });
    }

    private void sendMessageToPubSub(Message<Object> jsonObjectMessage) {
        ByteString data = ByteString.copyFromUtf8(jsonObjectMessage.body().toString());
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
        client.publish(pubsubMessage);

    }

    // get publisher client
    private  Publisher createClient() throws Exception  {
        GoogleCredentials credentials  = GoogleCredentials.fromStream(new FileInputStream(pubsubConfig.getString("credentialsPath")));
        return Publisher.newBuilder(pubsubConfig.getString("topic")).setCredentialsProvider(FixedCredentialsProvider.create(credentials))
          .build();

    }

}