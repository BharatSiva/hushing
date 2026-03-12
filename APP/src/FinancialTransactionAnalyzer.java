import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    long time;
    String account;

    Transaction(int id, int amount, String merchant, long time, String account) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.time = time;
        this.account = account;
    }
}

public class FinancialTransactionAnalyzer {

    List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public void findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                Transaction other = map.get(complement);
                System.out.println("TwoSum Pair → (" + other.id + ", " + t.id + ")");
            }

            map.put(t.amount, t);
        }
    }

    public void findTwoSumWithinOneHour(int target) {

        HashMap<Integer, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                for (Transaction other : map.get(complement)) {

                    if (Math.abs(t.time - other.time) <= 3600) {
                        System.out.println("TwoSum 1hr Pair → (" + other.id + ", " + t.id + ")");
                    }
                }
            }

            map.putIfAbsent(t.amount, new ArrayList<>());
            map.get(t.amount).add(t);
        }
    }

    public void detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        for (Map.Entry<String, List<Transaction>> entry : map.entrySet()) {

            List<Transaction> list = entry.getValue();

            if (list.size() > 1) {

                System.out.print("Duplicate → Amount: " + list.get(0).amount +
                        ", Merchant: " + list.get(0).merchant +
                        ", Accounts: ");

                for (Transaction t : list) {
                    System.out.print(t.account + " ");
                }

                System.out.println();
            }
        }
    }

    public void findKSum(int k, int target) {
        backtrack(0, k, target, new ArrayList<>());
    }

    private void backtrack(int start, int k, int target, List<Transaction> path) {

        if (k == 0 && target == 0) {

            System.out.print("KSum → (");

            for (Transaction t : path) {
                System.out.print(t.id + " ");
            }

            System.out.println(")");
            return;
        }

        if (k == 0 || target < 0)
            return;

        for (int i = start; i < transactions.size(); i++) {

            Transaction t = transactions.get(i);

            path.add(t);

            backtrack(i + 1, k - 1, target - t.amount, path);

            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) {

        FinancialTransactionAnalyzer system = new FinancialTransactionAnalyzer();

        system.addTransaction(new Transaction(1, 500, "StoreA", 36000, "acc1"));
        system.addTransaction(new Transaction(2, 300, "StoreB", 36900, "acc2"));
        system.addTransaction(new Transaction(3, 200, "StoreC", 37800, "acc3"));
        system.addTransaction(new Transaction(4, 500, "StoreA", 38000, "acc4"));

        system.findTwoSum(500);

        system.findTwoSumWithinOneHour(500);

        system.detectDuplicates();

        system.findKSum(3, 1000);
    }
}