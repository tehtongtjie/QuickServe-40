package menu;
import java.util.Objects;

public class MenuItem {
    private String name;
    private double price;
    private double rating;
    private String imageUrl;
    private String description; // Field untuk deskripsi

    // Konstruktor menerima deskripsi
    public MenuItem(String name, double price, double rating, String imageUrl, String description) {
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.imageUrl = (imageUrl == null || imageUrl.isEmpty()) ? "default.png" : imageUrl;
        this.description = (description == null || description.isEmpty()) 
                           ? "Deskripsi makanan belum tersedia." 
                           : description;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getRating() { return rating; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return name + " - Rp " + (int)price + " - Rating: " + rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return Double.compare(menuItem.price, price) == 0 &&
               Double.compare(menuItem.rating, rating) == 0 &&
               name.equals(menuItem.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, rating);
    }
}