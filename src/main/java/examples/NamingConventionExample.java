package examples;

/**
 * Example class to test NamingConventionCheck.
 * This class contains various naming convention violations.
 */
public class NamingConventionExample {

    // GOOD: Constant follows UPPER_SNAKE_CASE
    private static final int MAX_SIZE = 100;

    // GOOD: Regular field follows camelCase
    private String userName;

    // BAD: Regular field doesn't follow camelCase (starts with uppercase)
    private String UserName;

    // BAD: Regular field has underscores
    private int user_age;

    // BAD: Constant doesn't follow UPPER_SNAKE_CASE (has lowercase)
    private static final String apiKey = "secret";

    // GOOD: Method follows camelCase
    public void calculateTotal() {
        System.out.println("Calculating...");
    }

    // BAD: Method doesn't follow camelCase (starts with uppercase)
    public void CalculateSum() {
        System.out.println("Bad naming");
    }

    // BAD: Method has underscores
    public void get_user_name() {
        System.out.println("Bad naming");
    }

    // BAD: Method is all uppercase
    public void PROCESS() {
        System.out.println("Bad naming");
    }

    // GOOD: Private method with camelCase
    private void helperMethod() {
        System.out.println("Helper");
    }
}