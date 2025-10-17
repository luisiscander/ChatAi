#!/usr/bin/env bash

set -euo pipefail

# Usage:
#   ./scripts/delete_all_issues.sh <owner/repo>

REPO="${1:-}"

if [[ -z "${REPO}" ]]; then
  echo "Error: missing <owner/repo> argument."
  echo "Usage: $0 <owner/repo>"
  exit 1
fi

if ! command -v gh >/dev/null 2>&1; then
  echo "Error: GitHub CLI (gh) is not installed. See https://cli.github.com/."
  exit 1
fi

# Verify authentication
if ! gh auth status --hostname github.com >/dev/null 2>&1; then
  echo "You are not authenticated with gh. Run: gh auth login"
  exit 1
fi

echo "Deleting all issues in repository: ${REPO}"

# Get all issues
issues=$(gh issue list --repo "${REPO}" --json number --jq '.[].number')

for issue_num in $issues; do
  echo "Deleting issue #${issue_num}..."
  gh issue close "${issue_num}" --repo "${REPO}" --comment "Issue closed to recreate as scenario-based issues" >/dev/null
done

echo "All issues have been closed!"
