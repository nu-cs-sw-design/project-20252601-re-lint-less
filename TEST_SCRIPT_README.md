# Test Script Usage

## Overview

`test-examples.sh` is an automated test script that verifies the linter works correctly by running it against example classes and checking the output.

## Usage

```bash
./test-examples.sh
```

## What It Does

1. **Builds the project** (quietly, excluding unit tests)
2. **Runs the linter** on each example class
3. **Validates output** by checking for expected patterns
4. **Reports results** with pass/fail status

## Test Cases

The script tests all checks against their corresponding example files:

| Test | Example Class | Validates |
|------|--------------|-----------|
| `public-field` | PublicFieldExample | Detects public fields |
| `magic-numbers` | MagicNumbersExample | Finds magic numbers |
| `naming-convention` | NamingConventionExample | Checks naming rules |
| `too-many-params` | TooManyParametersExample | Flags methods with too many parameters |
| `god-class` | GodClassExample | Identifies god classes |
| `nested-ifs` | NestedIfExample | Detects deeply nested conditionals |
| `unused-variables` | UnusedVariablesExample | Finds unused variables |
| `redundant-interfaces` | RedundantExample | Identifies redundant interface declarations |
| `comprehensive` | ComprehensiveExample | Tests multiple checks together |
| `bad-class-name` | badClassName | Validates class naming conventions |
| `multiple-files` | Multiple classes | Tests running on multiple files at once |

## Output

### Successful Run
```
========================================
Running Linter Tests on Example Classes
========================================

--- Testing Checks ---

Testing public-field... PASS
Testing magic-numbers... PASS
...

========================================
Test Results
========================================

Total Tests:  11
Passed:       11
Failed:       0

✓ All tests passed!
```

### Failed Run
```
Testing public-field... FAIL
  Expected patterns not found: badField,public
  Actual output:
    [actual linter output here]

========================================
Test Results
========================================

Total Tests:  11
Passed:       10
Failed:       1

✗ Some tests failed

Test outputs saved in test-output/
```

## Test Output Files

All test outputs are saved in `test-output/` directory:
- `test-output/public-field.txt`
- `test-output/magic-numbers.txt`
- etc.

You can inspect these files to see the actual linter output for each test.

## Modifying the Script

### Adding a New Test

```bash
run_test "my-test-name" \
    "examples.MyExampleClass" \
    "pattern1,pattern2,pattern3"
```

The test passes if **all** patterns appear in the output.

### Adding a Negative Test (should produce no output)

```bash
run_negative_test "clean-class" \
    "examples.CleanExample"
```

The test passes if the linter produces **no** output (no issues found).

## Exit Codes

- `0` - All tests passed
- `1` - One or more tests failed

This makes it easy to use in CI/CD pipelines:

```bash
./test-examples.sh && echo "Deploy!" || echo "Fix issues first"
```

## Requirements

- Bash shell
- Gradle
- Project must be in a buildable state

## Tips

- Run `./gradlew clean` before the test script if you want a completely fresh build
- The script automatically excludes Gradle noise from output
- Test outputs are regenerated on each run
- You can run individual tests by extracting the `run_test` function calls