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

    public static ActorSystem<Gateway.Command> system;
    public static ActorRef<Gateway.Command> gateway;
    static Duration askTimeout;
    static Scheduler scheduler;



    public static void main(String[] args) {
        Config config = ConfigFactory.load("application.conf");
        System.out.println(config.getString("akka.actor.provider"));

        ActorSystem.create(DemoMarketplaceServiceApplication.create(), "DemoMarketplaceServiceApplication");

    }

    public static Behavior<Void> create() {

        return Behaviors.setup(context -> {

            /* creating the root actor */
            system = ActorSystem.create(Gateway.create(), "Gateway");
            gateway = system;
            askTimeout = Duration.ofSeconds(30);
            scheduler = system.scheduler();



            HttpServer server = HttpServer.create(new InetSocketAddress(8081), 1000);
            /* Creates a HTTP server that runs on localhost and listens to port 8000 */

            /* The "handle" method will receive each http request and respond to it */
            MyProductsHandler myProductsHandler = new MyProductsHandler(gateway, askTimeout, scheduler);
            server.createContext("/products", myProductsHandler);

            MyOrdersHandler myOrdersHandler = new MyOrdersHandler(gateway, askTimeout, scheduler);
            server.createContext("/orders", myOrdersHandler);

            MarketplaceHandler marketplaceHandler = new MarketplaceHandler(gateway, askTimeout, scheduler);
            server.createContext("/marketplace", marketplaceHandler);

//  
//            MyWalletsHandler myWalletsHandler = new MyWalletsHandler(gateway, askTimeout, scheduler);
//            server.createContext("/wallets", myWalletsHandler);

            // server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
            // /* Create a thread pool and give it to the server. Server will submit each incoming request to the thread pool. Thread pool will pick a free thread (whenever it becomes available) and run the handle() method in this thread. The request is given as argument to the handle() method. */
            // server.start();
            // /* Start the server */


            // Inside the create() method
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                8, // Core pool size (minimum number of threads)
                16, // Maximum pool size (maximum number of threads)
                30L, // Keep-alive time for idle threads
                TimeUnit.SECONDS, // Time unit for keep-alive time
                new LinkedBlockingQueue<>(1000) // Work queue for incoming requests
            );

            server.setExecutor(threadPoolExecutor);
            server.start();
            

            return Behaviors.empty(); // keep me (i.e., the root actor) alive, but  I don't want to receive messages
        });
    }


}
