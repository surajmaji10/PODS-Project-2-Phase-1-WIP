package me.akashmaj.demomarketplaceservice;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletionStage;

public class DeleteOrder extends AbstractBehavior<DeleteOrder.Command> {

    public static ActorRef<Gateway.Command> gateway;

    public interface Command {}

    public static class RequestDelete implements Command {
        public final Integer orderId;
        public final ActorRef<Order.Command> orderRef;
        public final ActorRef<Gateway.OrderInfo> replyTo;
        

        public RequestDelete(Integer orderId, ActorRef<Order.Command> orderRef, ActorRef<Gateway.OrderInfo> replyTo) {
            this.orderId = orderId;
            this.orderRef = orderRef;
            this.replyTo = replyTo;
        }
    }

    public static Behavior<Command> create(ActorRef<Gateway.Command> gateway) {
        return Behaviors.setup(context -> new DeleteOrder(context, gateway));
    }

    private DeleteOrder(ActorContext<Command> context,ActorRef<Gateway.Command> gateway ) {
        super(context);
        this.gateway = gateway;
        
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(RequestDelete.class, this::onRequestDelete)
                .build();
    }

    private Behavior<Command> onRequestDelete(RequestDelete request) {
        CompletionStage<Gateway.OrderInfo> orderDetails = AskPattern.ask(
                request.orderRef,
                (ActorRef<Gateway.OrderInfo> replyTo) -> new Order.GetOrder(request.orderId, replyTo),
                Duration.ofSeconds(30),
                getContext().getSystem().scheduler()
        );

        orderDetails.thenAccept(orderInfo -> {
            if (orderInfo == null || orderInfo.orderStatus.equals("DELETED")) {
                request.replyTo.tell(new Gateway.OrderInfo(null, -1, -1, "NOT_FOUND", 0, null, null));
                return;
            }

            for (var item : orderInfo.itemsToOrder) {
                Integer productId = item.get("product_id");
                Integer quantity = item.get("quantity");
            
                // Ask Gateway for the Product ActorRef
                CompletionStage<ActorRef<Product.Command>> productRefStage = AskPattern.ask(
                    gateway,
                    (ActorRef<ActorRef<Product.Command>> replyTo) -> new Gateway.GetProductRef(productId, replyTo),
                    Duration.ofSeconds(30),
                    getContext().getSystem().scheduler()
                );
            
                productRefStage.thenAccept(productRef -> {
                    if (productRef != null) {
                        productRef.tell(new Product.ProductUpdateRequest(null, productId, quantity));
                    } else {
                        Color.red("ProductRef not found for productId: " + productId);
                    }
                }).exceptionally(ex -> {
                    Color.red("Failed to get productRef for productId: " + productId);
                    ex.printStackTrace();
                    return null;
                });
            }

            API.updateUserWallet(orderInfo.userId, orderInfo.totalPrice, "credit");
            request.orderRef.tell(new Order.UpdateOrder(orderInfo.orderId, "DELETED", null));
            request.replyTo.tell(new Gateway.OrderInfo(null, orderInfo.orderId, orderInfo.userId, "DELETED", orderInfo.totalPrice, orderInfo.itemsToOrder, null));
        }).exceptionally(ex -> {
            request.replyTo.tell(new Gateway.OrderInfo(null, -1, -1, "ROLLBACK_FAILED", 0, null, null));
            return null;
        });

        return this;
    }
}
