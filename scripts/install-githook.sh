#!/usr/bin/env bash
# Install git hooks from .githooks directory
HOOK_DIR=$(git rev-parse --git-dir)/hooks
cp .githooks/pre-commit "$HOOK_DIR/pre-commit"
chmod +x "$HOOK_DIR/pre-commit"
echo "Installed pre-commit hook"
