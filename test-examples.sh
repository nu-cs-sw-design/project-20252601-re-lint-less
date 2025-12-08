#!/bin/bash

# Test script to verify linter output against expected results
# Usage: ./test-examples.sh

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Build the project first
echo "Building project..."
./gradlew build -q -x test > /dev/null 2>&1

# Create test output directory
mkdir -p test-output
mkdir -p expected-output

echo ""
echo "========================================"
echo "Running Linter Tests on Example Classes"
echo "========================================"
echo ""

# Test counter
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Function to run a test
run_test() {
    local test_name=$1
    local class_name=$2
    local expected_patterns=$3  # Comma-separated patterns that should appear

    TOTAL_TESTS=$((TOTAL_TESTS + 1))

    echo -n "Testing ${test_name}... "

    # Run linter and capture output
    ./gradlew run --args="${class_name}" -q 2>&1 | grep -v "Deprecated\|Task \|BUILD\|You can use\|For more\|Gradle" > "test-output/${test_name}.txt" || true

    # Check if all expected patterns are present
    local all_found=true
    IFS=',' read -ra PATTERNS <<< "$expected_patterns"

    for pattern in "${PATTERNS[@]}"; do
        if ! grep -q "$pattern" "test-output/${test_name}.txt"; then
            all_found=false
            break
        fi
    done

    if [ "$all_found" = true ]; then
        echo -e "${GREEN}PASS${NC}"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        return 0
    else
        echo -e "${RED}FAIL${NC}"
        echo "  Expected patterns not found: $expected_patterns"
        echo "  Actual output:"
        cat "test-output/${test_name}.txt" | sed 's/^/    /'
        FAILED_TESTS=$((FAILED_TESTS + 1))
        return 1
    fi
}

# Function to run a negative test (should not produce output)
run_negative_test() {
    local test_name=$1
    local class_name=$2

    TOTAL_TESTS=$((TOTAL_TESTS + 1))

    echo -n "Testing ${test_name} (should be clean)... "

    # Run linter and capture output
    ./gradlew run --args="${class_name}" -q 2>&1 | grep -v "Deprecated\|Task \|BUILD\|You can use\|For more\|Gradle" > "test-output/${test_name}.txt" || true

    # Check if output is empty (no issues found)
    if [ ! -s "test-output/${test_name}.txt" ] || [ $(wc -l < "test-output/${test_name}.txt") -eq 0 ]; then
        echo -e "${GREEN}PASS${NC}"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        return 0
    else
        echo -e "${RED}FAIL${NC}"
        echo "  Expected no output, but got:"
        cat "test-output/${test_name}.txt" | sed 's/^/    /'
        FAILED_TESTS=$((FAILED_TESTS + 1))
        return 1
    fi
}

# Run tests on example classes
echo "--- Testing Checks ---"
echo ""

# PublicFieldCheck
run_test "public-field" \
    "examples.PublicFieldExample" \
    "badField,public"

# MagicNumberCheck
run_test "magic-numbers" \
    "examples.MagicNumbersExample" \
    "Magic number 123"

# NamingConventionCheck
run_test "naming-convention" \
    "examples.NamingConventionExample" \
    "CalculateSum,camelCase,get_user_name,PROCESS"

# TooManyParametersCheck
run_test "too-many-params" \
    "examples.TooManyParametersExample" \
    "tooManyParams,6 parameters,calculateComplexValue,7 parameters"

# GodClassCheck
run_test "god-class" \
    "examples.GodClassExample" \
    "22 methods,11 fields,God Class"

# TooManyNestedIfsCheck
run_test "nested-ifs" \
    "examples.NestedIfExample" \
    "deep,nested conditionals,depth 4"

# UnusedVariablesCheck
run_test "unused-variables" \
    "examples.UnusedVariablesExample" \
    "unused,hasUnusedVariable,temp,result,computed,doubled"

# RedundantInterfacesCheck
run_test "redundant-interfaces" \
    "examples.RedundantExample" \
    "redundantly declared,examples/A"

# ComprehensiveExample (tests multiple checks)
run_test "comprehensive" \
    "examples.ComprehensiveExample" \
    "Magic number,parameters,unused variable"

# BadClassName
run_test "bad-class-name" \
    "examples.badClassName" \
    "badClassName,PascalCase"

echo ""
echo "--- Testing Multiple Files ---"
echo ""

# Test running multiple files at once
TOTAL_TESTS=$((TOTAL_TESTS + 1))
echo -n "Testing multiple files... "
./gradlew run --args="examples.PublicFieldExample examples.MagicNumbersExample" -q 2>&1 | \
    grep -v "Deprecated\|Task \|BUILD\|You can use\|For more\|Gradle" > "test-output/multiple-files.txt" || true

if grep -q "PublicFieldExample" "test-output/multiple-files.txt" && \
   grep -q "MagicNumbersExample" "test-output/multiple-files.txt"; then
    echo -e "${GREEN}PASS${NC}"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}FAIL${NC}"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

echo ""
echo "========================================"
echo "Test Results"
echo "========================================"
echo ""
echo "Total Tests:  $TOTAL_TESTS"
echo -e "Passed:       ${GREEN}$PASSED_TESTS${NC}"
if [ $FAILED_TESTS -gt 0 ]; then
    echo -e "Failed:       ${RED}$FAILED_TESTS${NC}"
else
    echo "Failed:       0"
fi
echo ""

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}✓ All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}✗ Some tests failed${NC}"
    echo ""
    echo "Test outputs saved in test-output/"
    exit 1
fi