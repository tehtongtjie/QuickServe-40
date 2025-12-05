package order;

import app.DBHelper;
import java.util.Random;

public class SimpleOrderProcessor extends OrderProcessor {

    private volatile boolean busy = false;
    private final Random rand = new Random();
    private final int pesananId;
    private final DBHelper db = new DBHelper();

    public SimpleOrderProcessor(int pesananId) {
        this.pesananId = pesananId;
    }

    @Override
    public synchronized boolean submitOrder(Order order, java.util.function.Consumer<Order> onComplete) {
        if (busy) return false;
        busy = true;

        int secs = 10 + rand.nextInt(10);
        order.setEstimatedSeconds(secs);
        order.setStatus(Order.Status.PROCESSING);
        OrdersManager.getInstance().updateOrder(order);

        db.updateStatusPesanan(pesananId, "dimasak");

        Thread t = new Thread(() -> {
            try {
                while (order.getRemainingSeconds() > 0) {
                    Thread.sleep(1000);
                    order.decrementRemaining();
                    OrdersManager.getInstance().updateOrder(order);
                }

                order.setStatus(Order.Status.READY);
                OrdersManager.getInstance().updateOrder(order);

                db.updateStatusPesanan(pesananId, "selesai");

                if (onComplete != null) onComplete.accept(order);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                busy = false;
            }
        });

        t.setDaemon(true);
        t.start();
        return true;
    }

    @Override
    public boolean isBusy() {
        return busy;
    }
}
