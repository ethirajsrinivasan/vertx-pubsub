package com.example.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

    private JsonObject appConfig;

	@Override
    public void start() throws Exception {
    	
		// get application config
		appConfig = config();
    	
    	// get router and set the endpoints
    	Router router = Router.router(vertx);
    	router.get("/sendpubsubmessage").handler(new MessageHandler(vertx));
    	
    	// set config for worker verticle
    	JsonObject pubsubConfig = new JsonObject();
    	pubsubConfig.put("topic", "your_topic");
    	pubsubConfig.put("credentialsPath","path/to/credentials.json");
    	
    	// deploy worker verticle
    	DeploymentOptions pubsubOptions =  new DeploymentOptions().setWorker(true).setConfig(pubsubConfig);
        vertx.deployVerticle("com.example.demo.PubSubMessageProcessor", pubsubOptions);

        // start http server
        vertx.createHttpServer().requestHandler(req -> {
            try {
                router.accept(req);
            } catch(Throwable th) {
                req.response().setStatusCode(400).end(th.getMessage());
            }
        }).listen(8080);
        
    }
}
