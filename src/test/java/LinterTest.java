import ASMParser.Parser;
import Checks.Check;
import Reporting.Reporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinterTest {

    private Parser parser;
    private MockReporter mockReporter;

    @BeforeEach
    void setUp() {
        parser = new Parser();
        mockReporter = new MockReporter();
    }

    @Test
    void testRunMultiple_withMultipleClasses() {
        // Use classes that exist in the examples package
        List<String> classNames = List.of(
                "examples.PublicFieldExample",
                "examples.MagicNumbersExample"
        );

        // Create linter with a check that always reports something
        List<Check> checks = List.of(new ReportingCheck());
        Linter linter = new Linter(parser, checks);

        linter.runMultiple(classNames, mockReporter);

        // Verify both classes were processed
        assertEquals(2, mockReporter.getReports().size(),
                "Expected exactly 2 reports (one per class)");
        // ClassNode.name uses internal format (slashes), not dots
        assertTrue(mockReporter.getReports().get(0).contains("examples/PublicFieldExample") ||
                   mockReporter.getReports().get(0).contains("examples.PublicFieldExample"));
        assertTrue(mockReporter.getReports().get(1).contains("examples/MagicNumbersExample") ||
                   mockReporter.getReports().get(1).contains("examples.MagicNumbersExample"));
    }

    @Test
    void testRunMultiple_withEmptyList() {
        List<String> classNames = List.of();

        List<Check> checks = List.of(new MockCheck());
        Linter linter = new Linter(parser, checks);

        linter.runMultiple(classNames, mockReporter);

        // Should complete without error, no reports
        assertEquals(0, mockReporter.getReports().size());
    }

    @Test
    void testRunMultiple_withInvalidClass() {
        List<String> classNames = List.of("NonExistentClass");

        List<Check> checks = List.of(new MockCheck());
        Linter linter = new Linter(parser, checks);

        linter.runMultiple(classNames, mockReporter);

        // Should report parse failure
        assertEquals(1, mockReporter.getReports().size());
        assertTrue(mockReporter.getReports().get(0).contains("Failed to parse"));
    }

    @Test
    void testLinter_withNoChecks() {
        List<String> classNames = List.of("examples.PublicFieldExample");

        // Create linter with no checks
        Linter linter = new Linter(parser, List.of());

        linter.runMultiple(classNames, mockReporter);

        // Should parse successfully but produce no reports (no checks to run)
        assertEquals(0, mockReporter.getReports().size());
    }

    @Test
    void testLinter_withNullChecksList() {
        List<String> classNames = List.of("examples.PublicFieldExample");

        // Create linter with null checks
        Linter linter = new Linter(parser, null);

        linter.runMultiple(classNames, mockReporter);

        // Should handle null gracefully and produce no reports
        assertEquals(0, mockReporter.getReports().size());
    }

    @Test
    void testLinter_checkReceivesCorrectClassNode() {
        List<String> classNames = List.of("examples.PublicFieldExample");

        CapturingCheck capturingCheck = new CapturingCheck();
        Linter linter = new Linter(parser, List.of(capturingCheck));

        linter.runMultiple(classNames, mockReporter);

        // Verify the check received the correct ClassNode
        assertEquals(1, capturingCheck.classesChecked.size());
        assertEquals("examples/PublicFieldExample", capturingCheck.classesChecked.get(0).name);
    }

    @Test
    void testLinter_withMultipleChecks() {
        List<String> classNames = List.of("examples.PublicFieldExample");

        ReportingCheck check1 = new ReportingCheck();
        ReportingCheck check2 = new ReportingCheck();
        Linter linter = new Linter(parser, List.of(check1, check2));

        linter.runMultiple(classNames, mockReporter);

        // Should run both checks, producing 2 reports
        assertEquals(2, mockReporter.getReports().size());
    }

    @Test
    void testLinter_checkThrowsException() {
        List<String> classNames = List.of("examples.PublicFieldExample");

        ThrowingCheck throwingCheck = new ThrowingCheck();
        Linter linter = new Linter(parser, List.of(throwingCheck));

        linter.runMultiple(classNames, mockReporter);

        // Should catch exception and report it
        assertEquals(1, mockReporter.getReports().size());
        assertTrue(mockReporter.getReports().get(0).contains("Check crashed"));
        assertTrue(mockReporter.getReports().get(0).contains("ThrowingCheck"));
    }

    @Test
    void testRunMultiple_withNullClassNamesList() {
        Linter linter = new Linter(parser, List.of(new MockCheck()));

        // Should handle null gracefully
        assertDoesNotThrow(() -> linter.runMultiple(null, mockReporter));
        assertEquals(0, mockReporter.getReports().size());
    }

    // Mock classes for testing

    private static class MockReporter implements Reporter {
        private final List<String> reports = new ArrayList<>();

        @Override
        public void report(String className, String message) {
            reports.add(className + ": " + message);
        }

        public List<String> getReports() {
            return reports;
        }
    }

    private static class MockCheck implements Check {
        @Override
        public boolean apply(ClassNode classNode, Reporter reporter) {
            // Always succeeds
            return true;
        }
    }

    private static class ReportingCheck implements Check {
        @Override
        public boolean apply(ClassNode classNode, Reporter reporter) {
            // Always reports something
            reporter.report(classNode.name, "Check executed");
            return true;
        }
    }

    private static class CapturingCheck implements Check {
        public final List<ClassNode> classesChecked = new ArrayList<>();

        @Override
        public boolean apply(ClassNode classNode, Reporter reporter) {
            classesChecked.add(classNode);
            return true;
        }
    }

    private static class ThrowingCheck implements Check {
        @Override
        public boolean apply(ClassNode classNode, Reporter reporter) {
            throw new RuntimeException("Test exception");
        }
    }
}
