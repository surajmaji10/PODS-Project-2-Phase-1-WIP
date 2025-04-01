package me.akashmaj.demomarketplaceservice;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import akka.actor.typed.javadsl.Behaviors;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@SpringBootApplication
public class DemoMarketplaceServiceApplication {

    /* root actor system */
    public static ActorSystem<Gateway.Command> system;
    /* gateway will be the root actor */
    public static ActorRef<Gateway.Command> gateway;
    /* ask() will wait for maximum of this */
    static Duration askTimeout;
    /* system scheduler for actor system */
    static Scheduler scheduler;

    public static void main(String[] args) {
        /* load the conf files located in resources */
        Config config = ConfigFactory.load("application.conf");
        System.out.println(config.getString("akka.actor.provider"));

        /* launch parent actor system (NOT USED in our case) */
        ActorSystem.create(
                DemoMarketplaceServiceApplication.create(), 
                "DemoMarketplaceServiceApplication"
            );

    }

    public static Behavior<Void> create() {

        return Behaviors.setup(context -> {

            /* creating the root actor syetem */
            system = ActorSystem.create(Gateway.create(), "Gateway");
            gateway = system;
            askTimeout = Duration.ofSeconds(30);
            scheduler = system.scheduler();

            /* Creates a HTTP server that runs on localhost and listens to port 8000 */
            HttpServer server = HttpServer.create(new InetSocketAddress(8081), 1000);
        
            /* The "handle" method will receive each http request and respond to it */
            MyProductsHandler myProductsHandler = new MyProductsHandler(gateway, askTimeout, scheduler);
            server.createContext("/products", myProductsHandler);

            MyOrdersHandler myOrdersHandler = new MyOrdersHandler(gateway, askTimeout, scheduler);
            server.createContext("/orders", myOrdersHandler);

            MarketplaceHandler marketplaceHandler = new MarketplaceHandler(gateway, askTimeout, scheduler);
            server.createContext("/marketplace", marketplaceHandler);


            /* custom thread pool */
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                8, // Core pool size (minimum number of threads)
                16, // Maximum pool size (maximum number of threads)
                30L, // Keep-alive time for idle threads
                TimeUnit.SECONDS, // Time unit for keep-alive time
                new LinkedBlockingQueue<>(1000) // Work queue for incoming requests
            );

            server.setExecutor(threadPoolExecutor);
            server.start();
            /* keep me (i.e., the root actor) alive, but  I don't want to receive messages */
            return Behaviors.empty();
        });
    }


}
