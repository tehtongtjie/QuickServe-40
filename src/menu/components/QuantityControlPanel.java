package menu.components;

import java.awt.*;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.swing.*;

// Panel untuk mengontrol kuantitas item dengan tombol tambah dan kurang
public class QuantityControlPanel extends JPanel {

    private int quantity = 0;
    private final String itemName; // nama item yang dikontrol
    private final Map<String, Integer> itemQuantities; // menyimpan semua kuantitas item dalam keranjang

    private final Runnable onChange;
    private final BiConsumer<String, Integer> dbSync;

    private JButton addBtn; // tombol tambah (+)
    private JPanel quantityPanel; // panel untuk kontrol kuantitas
    private JLabel qtyLabelField; // menampilkan kuantitas saat ini

    // konstruktor
    public QuantityControlPanel(
            String itemName,
            Map<String, Integer> itemQuantities,
            Runnable onChange,
            BiConsumer<String, Integer> dbSync
    ) {
        this.itemName = itemName;
        this.itemQuantities = itemQuantities;
        this.onChange = onChange;
        this.dbSync = dbSync;

        setOpaque(false);
        setPreferredSize(new Dimension(85, 30));
        setLayout(new BorderLayout());

        initializeAddButton();
        initializeQuantityPanel();

        // inisialisasi kuantitas dari itemQuantities
        this.quantity = itemQuantities.getOrDefault(itemName, 0);

        if (this.quantity > 0) {
            qtyLabelField.setText(String.valueOf(this.quantity));
            add(quantityPanel, BorderLayout.CENTER);
        } else {
            add(addBtn, BorderLayout.CENTER);
        }
    }

    // overload constructor tanpa dbSync
    public QuantityControlPanel(
            String itemName,
            Map<String, Integer> itemQuantities,
            Runnable onChange
    ) {
        this(itemName, itemQuantities, onChange, null);   // panggil constructor utama dengan dbSync null
    }
   
    // inisialisasi tombol tambah (+)
    private void initializeAddButton() {
        addBtn = new JButton("+");
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        addBtn.setBackground(new Color(255, 70, 70));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);

        addBtn.addActionListener(e -> {
            quantity++;
            itemQuantities.put(itemName, quantity);

            if (dbSync != null) dbSync.accept(itemName, +1);

            showQuantityPanel();
            if (onChange != null) onChange.run();
        });
    }

    private void initializeQuantityPanel() {
        quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        quantityPanel.setOpaque(false);

        JLabel minusBtn = new JLabel("-", SwingConstants.CENTER);
        minusBtn.setOpaque(true);
        minusBtn.setBackground(Color.WHITE);
        minusBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        minusBtn.setPreferredSize(new Dimension(25, 28));
        minusBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        // label untuk menampilkan kuantitas
        qtyLabelField = new JLabel("1");
        qtyLabelField.setFont(new Font("SansSerif", Font.BOLD, 14));
        qtyLabelField.setHorizontalAlignment(SwingConstants.CENTER);
        qtyLabelField.setPreferredSize(new Dimension(25, 28));

        JLabel plusBtn = new JLabel("+", SwingConstants.CENTER);
        plusBtn.setOpaque(true);
        plusBtn.setBackground(Color.WHITE);
        plusBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        plusBtn.setPreferredSize(new Dimension(25, 28));
        plusBtn.setFont(new Font("SansSerif", Font.BOLD, 18));

        minusBtn.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        if (quantity > 0) {
            quantity--;
            itemQuantities.put(itemName, quantity);

            if (dbSync != null) dbSync.accept(itemName, -1);
            qtyLabelField.setText(String.valueOf(quantity));

            if (onChange != null) onChange.run();
            if (quantity == 0) showAddButton();
        }
    }
});

plusBtn.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        quantity++;
        itemQuantities.put(itemName, quantity);

        if (dbSync != null) dbSync.accept(itemName, +1);
        qtyLabelField.setText(String.valueOf(quantity));

        if (onChange != null) onChange.run();
    }
});


        quantityPanel.add(minusBtn);
        quantityPanel.add(qtyLabelField);
        quantityPanel.add(plusBtn);
    }

    private void showQuantityPanel() {
        removeAll();
        add(quantityPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showAddButton() {
        removeAll();
        quantity = 0;
        itemQuantities.put(itemName, 0);
        add(addBtn, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
