package com.example.demo;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class MessageHandler implements Handler<RoutingContext> {

	private Vertx vertx;
    // constructor
	public MessageHandler(Vertx vertx) {
		this.vertx = vertx;
	}

	// send message to verticle via eventbus
	@Override
	public void handle(RoutingContext event) {
		JsonObject message = new JsonObject();
		String value = event.request().params().get("message");
		message.put("key", value);
		vertx.eventBus().send("pubsub", message);
	}

}
