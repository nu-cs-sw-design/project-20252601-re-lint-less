import BytecodeParser.ASM.ASMParser;
import BytecodeParser.IClassParser;
import BytecodeParser.Parser;
import Checks.Check;
import Checks.CheckFactory;
import Reporting.Reporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that verify the combination of:
 *  - Parser + Checks (from CheckFactory)
 *  - Example classes in src/main/java/examples
 *
 * produces exactly the expected linter output.
 */
class ChecksTest {

    private Parser parser;
    private Linter linter;
    private RecordingReporter reporter;

    @BeforeEach
    void setUp() {
        IClassParser asmParser = new ASMParser();
        parser = new Parser(asmParser);
        List<Check> checks = CheckFactory.createChecks();
        linter = new Linter(parser, checks);
        reporter = new RecordingReporter();
    }

    @Test
    void examplesProduceExpectedReports() {
        // Dotted names used as inputs; internal names (with /) appear in reports.
        List<String> classNames = List.of(
                "examples.UnusedVariablesExample",
                "examples.RedundantExample",
                "examples.MagicNumbersExample",
                "examples.GodClassExample",
                "examples.TooManyParamsExample",
                "examples.TooManyParametersExample",
                "examples.ComprehensiveExample",
                "examples.badClassName",
                "examples.NamingConventionExample"
                // If you later want to include more examples (NestedIf, PublicField, etc.)
                // you can add them here and extend expectedMessages.
        );

        linter.runMultiple(classNames, reporter);

        Set<String> actual = new HashSet<>(reporter.getMessages());
        Set<String> expected = new HashSet<>(expectedMessages());

        assertEquals(expected, actual,
                () -> "Expected and actual linter reports differ.\n" +
                        "Missing: " + diff(expected, actual) + "\n" +
                        "Unexpected: " + diff(actual, expected));
    }

    // ---------------------------------------------------------------------
    // Expected output, exactly as from the user's linter run
    // ---------------------------------------------------------------------

    private List<String> expectedMessages() {
        return List.of(
                // UnusedVariablesExample: magic numbers
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

                // UnusedVariablesExample: unused vars
                "[examples/UnusedVariablesExample] Method 'hasUnusedVariable' declares unused variable 'unused'",
                "[examples/UnusedVariablesExample] Method 'multipleUnused' declares unused variable 'temp'",
                "[examples/UnusedVariablesExample] Method 'multipleUnused' declares unused variable 'result'",
                "[examples/UnusedVariablesExample] Method 'assignedButNeverRead' declares unused variable 'computed'",
                "[examples/UnusedVariablesExample] Method 'complexUnused' declares unused variable 'doubled'",

                // RedundantExample
                "[examples/RedundantExample] Interface 'examples/A' is redundantly declared; it is already inherited from the superclass or another implemented interface.",

                // MagicNumbersExample
                "[examples/MagicNumbersExample] Magic number 123 found in method <init>",

                // GodClassExample: empty methods
                "[examples/GodClassExample] Method 'method1' only contains a return statement",
                "[examples/GodClassExample] Method 'method2' only contains a return statement",
                "[examples/GodClassExample] Method 'method3' only contains a return statement",
                "[examples/GodClassExample] Method 'method4' only contains a return statement",
                "[examples/GodClassExample] Method 'method5' only contains a return statement",
                "[examples/GodClassExample] Method 'method6' only contains a return statement",
                "[examples/GodClassExample] Method 'method7' only contains a return statement",
                "[examples/GodClassExample] Method 'method8' only contains a return statement",
                "[examples/GodClassExample] Method 'method9' only contains a return statement",
                "[examples/GodClassExample] Method 'method10' only contains a return statement",
                "[examples/GodClassExample] Method 'method11' only contains a return statement",
                "[examples/GodClassExample] Method 'method12' only contains a return statement",
                "[examples/GodClassExample] Method 'method13' only contains a return statement",
                "[examples/GodClassExample] Method 'method14' only contains a return statement",
                "[examples/GodClassExample] Method 'method15' only contains a return statement",
                "[examples/GodClassExample] Method 'method16' only contains a return statement",
                "[examples/GodClassExample] Method 'method17' only contains a return statement",
                "[examples/GodClassExample] Method 'method18' only contains a return statement",
                "[examples/GodClassExample] Method 'method19' only contains a return statement",
                "[examples/GodClassExample] Method 'method20' only contains a return statement",
                "[examples/GodClassExample] Method 'method21' only contains a return statement",

                // GodClassExample: class-level metrics
                "[examples/GodClassExample] Class has 22 methods (max allowed: 20) - possible God Class",
                "[examples/GodClassExample] Class has 11 fields (max allowed: 10) - possible God Class",

                // TooManyParamsExample
                "[examples/TooManyParamsExample] Method 'tooMany' has 6 parameters (max allowed: 5)",
                "[examples/TooManyParamsExample] Method 'fine' only contains a return statement",
                "[examples/TooManyParamsExample] Method 'tooMany' only contains a return statement",

                // TooManyParametersExample
                "[examples/TooManyParametersExample] Method 'tooManyParams' has 6 parameters (max allowed: 5)",
                "[examples/TooManyParametersExample] Method 'calculateComplexValue' has 7 parameters (max allowed: 5)",

                // ComprehensiveExample: magic numbers
                "[examples/ComprehensiveExample] Magic number 10 found in method processOrder",
                "[examples/ComprehensiveExample] Magic number 1.15 found in method processOrder",
                "[examples/ComprehensiveExample] Magic number 10 found in method inefficientCalculation",
                "[examples/ComprehensiveExample] Magic number 20 found in method inefficientCalculation",
                "[examples/ComprehensiveExample] Magic number 30 found in method inefficientCalculation",

                // ComprehensiveExample: too many params
                "[examples/ComprehensiveExample] Method 'processOrder' has 8 parameters (max allowed: 5)",
                "[examples/ComprehensiveExample] Method 'calculateTotal' has 6 parameters (max allowed: 5)",

                // ComprehensiveExample: unused vars
                "[examples/ComprehensiveExample] Method 'processOrder' declares unused variable 'tempValue'",
                "[examples/ComprehensiveExample] Method 'processOrder' declares unused variable 'tax'",
                "[examples/ComprehensiveExample] Method 'calculateTotal' declares unused variable 'subtotal'",
                "[examples/ComprehensiveExample] Method 'inefficientCalculation' declares unused variable 'c'",
                "[examples/ComprehensiveExample] Method 'inefficientCalculation' declares unused variable 'product'",

                // badClassName
                "[examples/badClassName] Class name 'badClassName' does not follow PascalCase convention",

                // NamingConventionExample
                "[examples/NamingConventionExample] Method name 'CalculateSum' does not follow camelCase convention",
                "[examples/NamingConventionExample] Method name 'get_user_name' does not follow camelCase convention",
                "[examples/NamingConventionExample] Method name 'PROCESS' does not follow camelCase convention",
                "[examples/NamingConventionExample] Field name 'UserName' does not follow camelCase convention",
                "[examples/NamingConventionExample] Field name 'user_age' does not follow camelCase convention",
                "[examples/NamingConventionExample] Constant field 'apiKey' does not follow UPPER_SNAKE_CASE convention"
        );
    }

    private List<String> diff(Set<String> a, Set<String> b) {
        List<String> result = new ArrayList<>();
        for (String s : a) {
            if (!b.contains(s)) {
                result.add(s);
            }
        }
        return result;
    }

    // ---------------------------------------------------------------------
    // Reporter that mimics ConsoleReporter format: "[className] message"
    // ---------------------------------------------------------------------

    private static class RecordingReporter implements Reporter {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void report(String className, String message) {
            messages.add("[" + className + "] " + message);
        }

        public List<String> getMessages() {
            return messages;
        }
    }
}
