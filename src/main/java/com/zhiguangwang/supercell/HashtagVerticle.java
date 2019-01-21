package com.zhiguangwang.supercell;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

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
        router.get("/hashtag/:hashtag").handler(this::getHiLoFromHashtag);
        router.get("/id/:id").handler(this::getHashtagFromHilo);
        return router;
    }

    private void getHiLoFromHashtag(RoutingContext ctx) {
        var hashtag = ctx.request().getParam("hashtag");
        var hiLo = HashtagUtils.getHiLoFromHashtag(hashtag);
        var hi = hiLo[0];
        var lo = hiLo[1];
        ctx.response().end(hi + "-" + lo);
    }

    private void getHashtagFromHilo(RoutingContext ctx) {
        var id = ctx.request().getParam("id");
        var arr = id.split("-");
        var hi = Long.parseLong(arr[0]);
        var lo = Long.parseLong(arr[1]);
        var hashtag = HashtagUtils.getHashtagFromHiLo(hi, lo);
        ctx.response().end(hashtag);
    }
}
