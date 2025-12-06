package examples;

/**
 * Comprehensive example that triggers all three new checks.
 */
public class ComprehensiveExample {

    // Triggers TooManyParametersCheck (8 parameters > 5)
    public void processOrder(String customerId, String orderId, int quantity,
                           double price, String address, boolean expedited,
                           long timestamp, String notes) {
        // Triggers UnusedVariablesCheck - 'tempValue' is never used
        int tempValue = quantity * 10;

        // Triggers MagicNumberCheck
        double tax = price * 1.15;

        System.out.println("Processing order: " + orderId);
    }

    // Triggers TooManyParametersCheck (6 parameters)
    public double calculateTotal(double base, double tax, double shipping,
                                double discount, double fees, boolean premium) {
        // Triggers UnusedVariablesCheck - 'subtotal' is never read
        double subtotal = base + tax;

        return base + tax + shipping - discount + fees;
    }

    // This method is fine (4 parameters)
    public String formatName(String first, String middle, String last, String suffix) {
        return first + " " + middle + " " + last + " " + suffix;
    }

    // Triggers UnusedVariablesCheck - multiple unused variables
    public void inefficientCalculation() {
        int a = 10;
        int b = 20;
        int c = 30;  // Never used
        int sum = a + b;
        int product = a * b;  // Never used

        System.out.println("Sum: " + sum);
    }
}