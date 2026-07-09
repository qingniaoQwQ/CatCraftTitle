package catcraft.title;

public class ShopItem {
    private final int id;
    private final int type;
    private final String display;
    private final int price;

    public ShopItem(int id, int type, String display, int price) {
        this.id = id;
        this.type = type;
        this.display = display;
        this.price = price;
    }

    public int getId() { return id; }
    public int getType() { return type; }
    public String getDisplay() { return display; }
    public int getPrice() { return price; }
}