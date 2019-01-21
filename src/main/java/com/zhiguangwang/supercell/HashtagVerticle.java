package com.zhiguangwang.supercell;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import static com.zhiguangwang.supercell.HashtagUtils.*;

public class HashtagVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(HashtagVerticle.class);

    private HttpServer httpServer;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        httpServer = vertx.createHttpServer()
            .requestHandler(createRouter())
            .listen(8080, ar -> {
                if (ar.succeeded()) {
                    startFuture.complete();
                } else {
                    startFuture.fail(ar.cause());
                }
            });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        httpServer.close(stopFuture);
    }

    private Router createRouter() {
        var router = Router.router(vertx);
        router.get("/hashtag/:hashtag").handler(this::parseHashtag);
        router.get("/hilo/:hilo").handler(this::parseHiLo);
        router.get("/id/:id").handler(this::parseId);
        return router;
    }

    private void parseHashtag(RoutingContext ctx) {
        var hashtag = ctx.request().getParam("hashtag");
        var hiLo = getHiLoFromHashtag(hashtag);
        var id = getLongFromHashtag(hashtag);
        var json = toJson(hashtag, hiLo, id);
        ctx.response().end(json.encodePrettily());
    }

    private void parseHiLo(RoutingContext ctx) {
        var hiLo = ctx.request().getParam("hilo");
        var arr = hiLo.split("-");
        var hi = Long.parseLong(arr[0]);
        var lo = Long.parseLong(arr[1]);
        var hashtag = getHashtagFromHiLo(hi, lo);
        var id = getLongFromHashtag(hashtag);
        var json = toJson(hashtag, new long[]{hi, lo}, id);
        ctx.response().end(json.encodePrettily());
    }

    private void parseId(RoutingContext ctx) {
        var idString = ctx.request().getParam("id");
        var id = Long.parseLong(idString);
        var hashtag = getHashtagFromLong(id);
        var hiLo = getHiLoFromHashtag(hashtag);
        var json = toJson(hashtag, hiLo, id);
        ctx.response().end(json.encodePrettily());
    }

    private static JsonObject toJson(String hashtag, long[] hiLo, long id) {
        var hi = hiLo[0];
        var lo = hiLo[1];
        return new JsonObject()
            .put("hashtag", hashtag)
            .put("hilo", hi + "-" + lo)
            .put("id", Long.toString(id));
    }
}
