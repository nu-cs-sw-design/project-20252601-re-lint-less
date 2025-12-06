package examples;

/**
 * Example class to test UnusedVariablesCheck.
 * This class contains methods with unused local variables.
 */
public class UnusedVariablesExample {

    // This should flag 'unused' variable
    public void hasUnusedVariable() {
        int used = 10;
        int unused = 20;  // This variable is never read
        System.out.println("Used value: " + used);
    }

    // This should flag 'temp' and 'result' variables
    public void multipleUnused() {
        String name = "Alice";
        int age = 30;
        String temp = "temporary";  // Never used
        int result = 42;  // Never used

        System.out.println(name + " is " + age + " years old");
    }

    // This should NOT flag anything (all variables are used)
    public int allVariablesUsed() {
        int a = 5;
        int b = 10;
        int sum = a + b;
        return sum;
    }

    // This should flag 'computed' (assigned but never read)
    public void assignedButNeverRead() {
        int x = 100;
        int computed = x * 2;  // Computed but never used
        System.out.println("X is: " + x);
    }

    // Edge case: variable used in its own assignment (still should be flagged if not used after)
    public void complexUnused() {
        int value = 5;
        int doubled = value * 2;  // Used 'value', but 'doubled' is never read
        System.out.println("Original: " + value);
    }

    // This should NOT flag anything (loop variable is used)
    public void loopVariableUsed() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Iteration: " + i);
        }
    }
}