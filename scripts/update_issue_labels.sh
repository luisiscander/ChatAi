#!/usr/bin/env bash

set -euo pipefail

# Usage:
#   ./scripts/update_issue_labels.sh <owner/repo>
#
# Example:
#   ./scripts/update_issue_labels.sh luisiscander/ChatAi

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

echo "Updating labels for all issues in repository: ${REPO}"

# Create the new labels if they don't exist
echo "Creating standard labels..."
gh label create "enhancement" --color "0e8a16" --description "New feature or improvement" --repo "${REPO}" 2>/dev/null || true
gh label create "bug" --color "d73a4a" --description "Something isn't working" --repo "${REPO}" 2>/dev/null || true

# Get all issues and update their labels
echo "Fetching all issues..."
issues=$(gh issue list --repo "${REPO}" --json number --jq '.[].number')

for issue_num in $issues; do
  echo "Updating issue #${issue_num}..."
  
  # Remove all existing labels and add enhancement label
  # (All user stories are enhancements, not bugs)
  gh issue edit "${issue_num}" --repo "${REPO}" --remove-label "*" --add-label "enhancement" >/dev/null 2>&1 || true
done

echo "All issues updated successfully!"
echo "Issues now have 'enhancement' label instead of priority/estimate/epic labels."
