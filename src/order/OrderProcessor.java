package order;

public abstract class OrderProcessor {

    public abstract boolean submitOrder(Order order, java.util.function.Consumer<Order> onComplete);

    public abstract boolean isBusy();
}
