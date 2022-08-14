package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setPort(8889)
      .setHost("localhost")
      .setDatabase("staj")
      .setUser("root")
      .setPassword("root");

// Pool options
    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

// Create the client pool
    SqlClient client = MySQLPool.pool(vertx, connectOptions, poolOptions);


    MuseumRepository repository = new MuseumRepository(client);
    TheatreRepository repository1 = new TheatreRepository(client);

    GenericMuseumHandler handler = new GenericMuseumHandler(repository);
    GenericTheatreHandler handler1 = new GenericTheatreHandler(repository1);

    Router router = Router.router(vertx);

    router.route("/api/museums*").handler(BodyHandler.create());
    router.get("/api/museums/location/:locationName").handler(handler::readByLocation);
    router.get("/api/museums/rating/:rating").handler(handler::readByRating);
    router.get("/api/museums/name/:name").handler(handler::readByName);
    router.get("/api/museums/number/:number").handler(handler::readByNumber);
    router.get("/api/museums/type/:type").handler(handler::readByType);

    router.route("/api/theatres*").handler(BodyHandler.create());
    router.get("/api/theatres/number/:number").handler(handler1::readByNumber);



    vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8080");
      } else {
        startPromise.fail(http.cause());
      }
    });


  }


}
