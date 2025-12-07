import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Reporting.Reporter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LinterExamplesTest {

    /**
     * TestReporter records all messages the linter sends, in the same
     * "[examples/XYZ] message" format as your sample output.
     */
    private static class TestReporter implements Reporter {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void report(String className, String message) {
            // Normalize className so both "examples.Foo" and "examples/Foo" become "examples/Foo"
            String normalized = className.replace('.', '/');
            messages.add("[" + normalized + "] " + message);
        }

        public List<String> getMessages() {
            return messages;
        }
    }

    // Cache of the full linter output from running over the examples directory
    private static List<String> allOutput;

    private static List<String> getAllOutput() throws IOException {
        if (allOutput == null) {
            TestReporter reporter = new TestReporter();
            Linter linter = new Linter();
            linter.addChecks(CheckFactory.createChecks());

            // Run linter the SAME way you do “for real”:
            // discover all classes under build/classes/java/main/examples
            List<String> classes = Linter.discoverClasses(
                    new String[] { "build/classes/java/main/examples" }
            );
            linter.runMultiple(classes, reporter);
            allOutput = reporter.getMessages();
        }
        return allOutput;
    }

    /** Get only the lines that belong to a given class, e.g. "examples/UnusedVariablesExample". */
    private List<String> outputForClass(String normalizedClassName) throws IOException {
        String prefix = "[" + normalizedClassName + "]";
        return getAllOutput().stream()
                .filter(line -> line.startsWith(prefix))
                .collect(Collectors.toList());
    }

    /** Assert that all expected lines appear in the actual output for that class. */
    private void assertOutputContainsAllForClass(String normalizedClassName,
                                                 List<String> expectedLines) throws IOException {
        List<String> actual = outputForClass(normalizedClassName);

        StringBuilder debug = new StringBuilder();
        debug.append("Actual output for ").append(normalizedClassName).append(":\n");
        for (String line : actual) {
            debug.append(line).append("\n");
        }

        for (String expected : expectedLines) {
            assertTrue(
                    actual.contains(expected),
                    "Missing expected line:\n" + expected + "\n\n" + debug
            );
        }
    }

    @Test
    void unusedVariablesExampleOutputMatchesExpected() throws Exception {
        List<String> expected = List.of(
                "[examples/UnusedVariablesExample] Visited class.",
                "[examples/UnusedVariablesExample] Magic number 10 found in method hasUnusedVariable",
                "[examples/UnusedVariablesExample] Magic number 20 found in method hasUnusedVariable",
                "[examples/UnusedVariablesExample] Magic number 30 found in method multipleUnused",
                "[examples/UnusedVariablesExample] Magic number 42 found in method multipleUnused",
                "[examples/UnusedVariablesExample] Magic number 5 found in method allVariablesUsed",
                "[examples/UnusedVariablesExample] Magic number 10 found in method allVariablesUsed",
                "[examples/UnusedVariablesExample] Magic number 100 found in method assignedButNeverRead",
                "[examples/UnusedVariablesExample] Magic number 2 found in method assignedButNeverRead",
                "[examples/UnusedVariablesExample] Magic number 5 found in method complexUnused",
                "[examples/UnusedVariablesExample] Magic number 2 found in method complexUnused",
                "[examples/UnusedVariablesExample] Magic number 10 found in method loopVariableUsed",
                "[examples/UnusedVariablesExample] Unreachable code detected in method '<init>'",
                "[examples/UnusedVariablesExample] Unreachable code detected in method 'hasUnusedVariable'",
                "[examples/UnusedVariablesExample] Unreachable code detected in method 'multipleUnused'",
                "[examples/UnusedVariablesExample] Unreachable code detected in method 'allVariablesUsed'",
                "[examples/UnusedVariablesExample] Unreachable code detected in method 'assignedButNeverRead'",
                "[examples/UnusedVariablesExample] Unreachable code detected in method 'complexUnused'",
                "[examples/UnusedVariablesExample] Unreachable code detected in method 'loopVariableUsed'",
                "[examples/UnusedVariablesExample] Method 'hasUnusedVariable' declares unused variable 'unused'",
                "[examples/UnusedVariablesExample] Method 'multipleUnused' declares unused variable 'temp'",
                "[examples/UnusedVariablesExample] Method 'multipleUnused' declares unused variable 'result'",
                "[examples/UnusedVariablesExample] Method 'assignedButNeverRead' declares unused variable 'computed'",
                "[examples/UnusedVariablesExample] Method 'complexUnused' declares unused variable 'doubled'"
        );

        assertOutputContainsAllForClass("examples/UnusedVariablesExample", expected);
    }

    @Test
    void redundantExampleOutputMatchesExpected() throws Exception {
        List<String> expected = List.of(
                "[examples/RedundantExample] Visited class.",
                "[examples/RedundantExample] Interface 'examples/A' is redundantly declared; it is already inherited from the superclass or another implemented interface.",
                "[examples/RedundantExample] Unreachable code detected in method '<init>'"
        );

        assertOutputContainsAllForClass("examples/RedundantExample", expected);
    }

    @Test
    void magicNumbersExampleOutputMatchesExpected() throws Exception {
        List<String> expected = List.of(
                "[examples/MagicNumbersExample] Visited class.",
                "[examples/MagicNumbersExample] Magic number 123 found in method <init>",
                "[examples/MagicNumbersExample] Unreachable code detected in method '<init>'"
        );

        assertOutputContainsAllForClass("examples/MagicNumbersExample", expected);
    }

    @Test
    void nestedIfExampleOutputMatchesExpected() throws Exception {
        List<String> expected = List.of(
                "[examples/NestedIfExample] Visited class.",
                "[examples/NestedIfExample] Method 'deep' has nested conditionals of depth 4 (max allowed is 3).",
                "[examples/NestedIfExample] Unreachable code detected in method '<init>'",
                "[examples/NestedIfExample] Unreachable code detected in method 'deep'",
                "[examples/NestedIfExample] Unreachable code detected in method 'shallow'"
        );

        assertOutputContainsAllForClass("examples/NestedIfExample", expected);
    }

    @Test
    void godClassExampleOutputMatchesExpected() throws Exception {
        List<String> expected = List.of(
                "[examples/GodClassExample] Visited class.",
                "[examples/GodClassExample] Unreachable code detected in method '<init>'",
                "[examples/GodClassExample] Unreachable code detected in method 'method1'",
                "[examples/GodClassExample] Unreachable code detected in method 'method2'",
                "[examples/GodClassExample] Unreachable code detected in method 'method3'",
                "[examples/GodClassExample] Unreachable code detected in method 'method4'",
                "[examples/GodClassExample] Unreachable code detected in method 'method5'",
                "[examples/GodClassExample] Unreachable code detected in method 'method6'",
                "[examples/GodClassExample] Unreachable code detected in method 'method7'",
                "[examples/GodClassExample] Unreachable code detected in method 'method8'",
                "[examples/GodClassExample] Unreachable code detected in method 'method9'",
                "[examples/GodClassExample] Unreachable code detected in method 'method10'",
                "[examples/GodClassExample] Unreachable code detected in method 'method11'",
                "[examples/GodClassExample] Unreachable code detected in method 'method12'",
                "[examples/GodClassExample] Unreachable code detected in method 'method13'",
                "[examples/GodClassExample] Unreachable code detected in method 'method14'",
                "[examples/GodClassExample] Unreachable code detected in method 'method15'",
                "[examples/GodClassExample] Unreachable code detected in method 'method16'",
                "[examples/GodClassExample] Unreachable code detected in method 'method17'",
                "[examples/GodClassExample] Unreachable code detected in method 'method18'",
                "[examples/GodClassExample] Unreachable code detected in method 'method19'",
                "[examples/GodClassExample] Unreachable code detected in method 'method20'",
                "[examples/GodClassExample] Unreachable code detected in method 'method21'",
                "[examples/GodClassExample] Class has 22 methods (max allowed: 20) - possible God Class",
                "[examples/GodClassExample] Class has 11 fields (max allowed: 10) - possible God Class"
        );

        assertOutputContainsAllForClass("examples/GodClassExample", expected);
    }

    @Test
    void bExampleOutputMatchesExpected() throws Exception {
        List<String> expected = List.of(
                "[examples/B] Visited class."
        );

        assertOutputContainsAllForClass("examples/B", expected);
    }

    @Test
    void tooManyParamsExampleOutputMatchesExpected() throws Exception {
        List<String> expected = List.of(
                "[examples/TooManyParamsExample] Visited class.",
                "[examples/TooManyParamsExample] Method 'tooMany' has 6 parameters (max allowed: 5)",
                "[examples/TooManyParamsExample] Unreachable code detected in method '<init>'",
                "[examples/TooManyParamsExample] Unreachable code detected in method 'fine'",
                "[examples/TooManyParamsExample] Unreachable code detected in method 'tooMany'"
        );

        assertOutputContainsAllForClass("examples/TooManyParamsExample", expected);
    }

    @Test
    void tooManyParametersExampleOutputMatchesExpected() throws Exception {
        List<String> expected = List.of(
                "[examples/TooManyParametersExample] Visited class.",
                "[examples/TooManyParametersExample] Method 'tooManyParams' has 6 parameters (max allowed: 5)",
                "[examples/TooManyParametersExample] Method 'calculateComplexValue' has 7 parameters (max allowed: 5)",
                "[examples/TooManyParametersExample] Unreachable code detected in method '<init>'",
                "[examples/TooManyParametersExample] Unreachable code detected in method 'tooManyParams'",
                "[examples/TooManyParametersExample] Unreachable code detected in method 'calculateComplexValue'",
                "[examples/TooManyParametersExample] Unreachable code detected in method 'atLimit'",
                "[examples/TooManyParametersExample] Unreachable code detected in method 'acceptable'",
                "[examples/TooManyParametersExample] Unreachable code detected in method 'noParams'"
        );

        assertOutputContainsAllForClass("examples/TooManyParametersExample", expected);
    }

    @Test
    void comprehensiveExampleOutputMatchesExpected() throws Exception {
        List<String> expected = List.of(
                "[examples/ComprehensiveExample] Visited class.",
                "[examples/ComprehensiveExample] Magic number 10 found in method processOrder",
                "[examples/ComprehensiveExample] Magic number 1.15 found in method processOrder",
                "[examples/ComprehensiveExample] Magic number 10 found in method inefficientCalculation",
                "[examples/ComprehensiveExample] Magic number 20 found in method inefficientCalculation",
                "[examples/ComprehensiveExample] Magic number 30 found in method inefficientCalculation",
                "[examples/ComprehensiveExample] Method 'processOrder' has 8 parameters (max allowed: 5)",
                "[examples/ComprehensiveExample] Method 'calculateTotal' has 6 parameters (max allowed: 5)",
                "[examples/ComprehensiveExample] Unreachable code detected in method '<init>'",
                "[examples/ComprehensiveExample] Unreachable code detected in method 'processOrder'",
                "[examples/ComprehensiveExample] Unreachable code detected in method 'calculateTotal'",
                "[examples/ComprehensiveExample] Unreachable code detected in method 'formatName'",
                "[examples/ComprehensiveExample] Unreachable code detected in method 'inefficientCalculation'",
                "[examples/ComprehensiveExample] Method 'processOrder' declares unused variable 'tempValue'",
                "[examples/ComprehensiveExample] Method 'processOrder' declares unused variable 'tax'",
                "[examples/ComprehensiveExample] Method 'calculateTotal' declares unused variable 'subtotal'",
                "[examples/ComprehensiveExample] Method 'inefficientCalculation' declares unused variable 'c'",
                "[examples/ComprehensiveExample] Method 'inefficientCalculation' declares unused variable 'product'"
        );

        assertOutputContainsAllForClass("examples/ComprehensiveExample", expected);
    }

    @Test
    void aExampleOutputMatchesExpected() throws Exception {
        List<String> expected = List.of(
                "[examples/A] Visited class."
        );

        assertOutputContainsAllForClass("examples/A", expected);
    }

    @Test
    void unreachableCodeExampleOutputMatchesExpected() throws Exception {
        List<String> expected = List.of(
                "[examples/UnreachableCodeExample] Visited class.",
                "[examples/UnreachableCodeExample] Unreachable code detected in method '<init>'",
                "[examples/UnreachableCodeExample] Unreachable code detected in method 'unreachableCodeExample'"
        );

        assertOutputContainsAllForClass("examples/UnreachableCodeExample", expected);
    }

    @Test
    void badClassNameExampleOutputMatchesExpected() throws Exception {
        List<String> expected = List.of(
                "[examples/badClassName] Visited class.",
                "[examples/badClassName] Unreachable code detected in method '<init>'",
                "[examples/badClassName] Unreachable code detected in method 'someMethod'",
                "[examples/badClassName] Class name 'badClassName' does not follow PascalCase convention"
        );

        assertOutputContainsAllForClass("examples/badClassName", expected);
    }

    @Test
    void publicFieldExampleOutputMatchesExpected() throws Exception {
        List<String> expected = List.of(
                "[examples/PublicFieldExample] Visited class.",
                "[examples/PublicFieldExample] Field 'badField' is public; consider using private/protected with accessors.",
                "[examples/PublicFieldExample] Unreachable code detected in method '<init>'"
        );

        assertOutputContainsAllForClass("examples/PublicFieldExample", expected);
    }

    @Test
    void namingConventionExampleOutputMatchesExpected() throws Exception {
        List<String> expected = List.of(
                "[examples/NamingConventionExample] Visited class.",
                "[examples/NamingConventionExample] Unreachable code detected in method '<init>'",
                "[examples/NamingConventionExample] Unreachable code detected in method 'calculateTotal'",
                "[examples/NamingConventionExample] Unreachable code detected in method 'CalculateSum'",
                "[examples/NamingConventionExample] Unreachable code detected in method 'get_user_name'",
                "[examples/NamingConventionExample] Unreachable code detected in method 'PROCESS'",
                "[examples/NamingConventionExample] Unreachable code detected in method 'helperMethod'",
                "[examples/NamingConventionExample] Method name 'CalculateSum' does not follow camelCase convention",
                "[examples/NamingConventionExample] Method name 'get_user_name' does not follow camelCase convention",
                "[examples/NamingConventionExample] Method name 'PROCESS' does not follow camelCase convention",
                "[examples/NamingConventionExample] Field name 'UserName' does not follow camelCase convention",
                "[examples/NamingConventionExample] Field name 'user_age' does not follow camelCase convention",
                "[examples/NamingConventionExample] Constant field 'apiKey' does not follow UPPER_SNAKE_CASE convention"
        );

        assertOutputContainsAllForClass("examples/NamingConventionExample", expected);
    }
}
