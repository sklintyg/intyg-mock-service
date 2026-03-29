#!/usr/bin/env bash
set -euo pipefail

echo "Step 1/3: Checking formatting..."
./gradlew spotlessCheck --quiet

echo "Step 2/3: Building (compile + unit tests)..."
./gradlew build --quiet

echo "Step 3/3: Running integration tests..."
./gradlew :integration-test:integrationTest --quiet

echo "All quality checks passed."
