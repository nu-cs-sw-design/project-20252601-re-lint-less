package examples;

/**
 * Example class with a bad class name (starts with lowercase).
 * This should be flagged by NamingConventionCheck.
 */
public class badClassName {

    public void someMethod() {
        System.out.println("Class name violates PascalCase convention");
    }
}