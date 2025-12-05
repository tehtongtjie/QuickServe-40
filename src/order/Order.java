package order;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Order {
    public enum Status { PENDING, PROCESSING, READY, FINISHED }

    private final String id;
    public static class OrderItem {
        private final String name;
        private final int quantity;
        private final double pricePerUnit;

        public OrderItem(String name, int quantity, double pricePerUnit) {
            this.name = name;
            this.quantity = quantity;
            this.pricePerUnit = pricePerUnit;
        }

        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public double getPricePerUnit() { return pricePerUnit; }
        public double getTotalPrice() { return quantity * pricePerUnit; }
    }

    private final List<OrderItem> items;
    private final double totalAmount;
    private volatile Status status;
    private volatile int estimatedSeconds;
    private final long createdAt;
    private final AtomicInteger remainingSeconds = new AtomicInteger(0);

    public Order(String id, List<OrderItem> items, double totalAmount, int estimatedSeconds) {
        this.id = id;
        this.items = items;
        this.totalAmount = totalAmount;
        this.estimatedSeconds = estimatedSeconds;
        this.createdAt = System.currentTimeMillis();
        this.status = Status.PENDING;
        this.remainingSeconds.set(estimatedSeconds);
    }

    public String getId() { return id; }
    public List<OrderItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public Status getStatus() { return status; }
    public void setStatus(Status s) { this.status = s; }
    public int getEstimatedSeconds() { return estimatedSeconds; }
    public void setEstimatedSeconds(int secs) { this.estimatedSeconds = secs; this.remainingSeconds.set(secs); }
    public int getRemainingSeconds() { return remainingSeconds.get(); }
    public void setRemainingSeconds(int secs) { remainingSeconds.set(secs); }
    public void decrementRemaining() { remainingSeconds.decrementAndGet(); }
    public long getCreatedAt() { return createdAt; }
}
