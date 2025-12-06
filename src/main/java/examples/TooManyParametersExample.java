package examples;

/**
 * Example class to test TooManyParametersCheck.
 * This class contains methods with varying parameter counts.
 */
public class TooManyParametersExample {

    // This should be flagged (6 parameters > 5 max)
    public void tooManyParams(int a, int b, int c, String d, double e, float f) {
        System.out.println("Too many parameters!");
    }

    // This should be flagged (7 parameters)
    public int calculateComplexValue(int x, int y, int z, String name,
                                     boolean flag, double rate, long timestamp) {
        return x + y + z;
    }

    // This should pass (5 parameters = exactly at limit)
    public void atLimit(int a, int b, int c, int d, int e) {
        System.out.println("At the limit");
    }

    // This should pass (3 parameters < 5)
    public void acceptable(String name, int age, boolean active) {
        System.out.println("Acceptable number of parameters");
    }

    // This should pass (0 parameters)
    public void noParams() {
        System.out.println("No parameters");
    }
}