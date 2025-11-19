package Reporting;

public class ConsoleReporter implements Reporter {
    @Override
    public void report(String className, String message) {
        System.out.println("[" + className + "] " + message);
    }
}
