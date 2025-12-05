package order;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrdersManager {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final List<Order> orders = new ArrayList<>();

    private OrdersManager() {}

    private static class Holder { static final OrdersManager I = new OrdersManager(); }

    public static OrdersManager getInstance() { return Holder.I; }

    public synchronized void addOrder(Order o) {
        orders.add(0, o); 
        pcs.firePropertyChange("orderAdded", null, o);
    }

    public synchronized void updateOrder(Order o) {
        pcs.firePropertyChange("orderUpdated", null, o);
    }

    public synchronized List<Order> getOrders() {
        return Collections.unmodifiableList(new ArrayList<>(orders));
    }

    public void addPropertyChangeListener(PropertyChangeListener l) { pcs.addPropertyChangeListener(l); }
    public void removePropertyChangeListener(PropertyChangeListener l) { pcs.removePropertyChangeListener(l); }
}
