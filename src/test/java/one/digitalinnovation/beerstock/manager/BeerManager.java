package one.digitalinnovation.beerstock.manager;

public class BeerManager {

    private static final ThreadLocal<String> createdBeerName = new ThreadLocal<>();
    private static final ThreadLocal<Long> createdBeerId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentQuantity = new ThreadLocal<>();

    public static void setTestName(String name) {
        createdBeerName.set(name);
    }

    public static void setTestId(Long id) {
        createdBeerId.set(id);
    }

    public static void setCurrentQuantity(int quantity) {
        currentQuantity.set(String.valueOf(quantity));
    }


    public static String getTestName() {
        return createdBeerName.get();
    }

    public static Long getTestId() {
        return createdBeerId.get();
    }

    public static String getCurrentQuantity() {
        return currentQuantity.get();
    }

    public static void remove() {
        createdBeerName.remove();
        createdBeerId.remove();
        currentQuantity.remove();
    }

}
