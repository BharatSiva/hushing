import java.util.*;

public class FlashSaleInventoryManager {

    // productId -> stock count
    private HashMap<String, Integer> inventory;

    // productId -> waiting list (FIFO)
    private HashMap<String, LinkedHashMap<Integer, Integer>> waitingList;

    public FlashSaleInventoryManager() {
        inventory = new HashMap<>();
        waitingList = new HashMap<>();
    }

    // Add product with initial stock
    public void addProduct(String productId, int stock) {
        inventory.put(productId, stock);
        waitingList.put(productId, new LinkedHashMap<>());
    }

    // Check stock availability
    public int checkStock(String productId) {
        return inventory.getOrDefault(productId, 0);
    }

    // Purchase item (Thread-safe)
    public synchronized void purchaseItem(String productId, int userId) {

        int stock = inventory.getOrDefault(productId, 0);

        if (stock > 0) {

            stock--;
            inventory.put(productId, stock);

            System.out.println("User " + userId +
                    " purchase SUCCESS. Remaining stock: " + stock);

        } else {

            LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

            int position = queue.size() + 1;
            queue.put(userId, position);

            System.out.println("User " + userId +
                    " added to WAITING LIST. Position #" + position);
        }
    }

    // Show waiting list
    public void showWaitingList(String productId) {

        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

        System.out.println("Waiting List:");

        for (Map.Entry<Integer, Integer> entry : queue.entrySet()) {
            System.out.println("User " + entry.getKey()
                    + " -> Position " + entry.getValue());
        }
    }

    public static void main(String[] args) {

        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();

        manager.addProduct("IPHONE15_256GB", 3);

        System.out.println("Stock: "
                + manager.checkStock("IPHONE15_256GB") + " units");

        manager.purchaseItem("IPHONE15_256GB", 101);
        manager.purchaseItem("IPHONE15_256GB", 102);
        manager.purchaseItem("IPHONE15_256GB", 103);

        // Stock finished
        manager.purchaseItem("IPHONE15_256GB", 104);
        manager.purchaseItem("IPHONE15_256GB", 105);

        manager.showWaitingList("IPHONE15_256GB");
    }
}