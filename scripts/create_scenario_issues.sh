#!/usr/bin/env bash

set -euo pipefail

# Usage:
#   ./scripts/create_scenario_issues.sh <owner/repo> [path/to/REQUIREMENTS.MD]
#
# Example:
#   ./scripts/create_scenario_issues.sh luisiscander/ChatAi ./REQUIREMENTS.MD

REPO="${1:-}"
REQ_PATH="${2:-REQUIREMENTS.MD}"

if [[ -z "${REPO}" ]]; then
  echo "Error: missing <owner/repo> argument."
  echo "Usage: $0 <owner/repo> [path/to/REQUIREMENTS.MD]"
  exit 1
fi

if [[ ! -f "${REQ_PATH}" ]]; then
  echo "Error: requirements file not found at: ${REQ_PATH}"
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

echo "Using repository: ${REPO}"

# Create standard labels
echo "Creating standard labels..."
gh label create "enhancement" --color "0e8a16" --description "New feature or improvement" --repo "${REPO}" 2>/dev/null || true
gh label create "bug" --color "d73a4a" --description "Something isn't working" --repo "${REPO}" 2>/dev/null || true

echo "Parsing scenarios and creating issues..."

# Variables to track current context
current_epic=""
current_hu=""
current_hu_title=""
current_hu_priority=""
current_hu_estimate=""
current_feature=""
current_scenario=""
current_scenario_content=""
in_feature=0
in_scenario=0
in_gherkin=0

# Function to create issue for a scenario
create_scenario_issue() {
  if [[ -z "${current_scenario}" || -z "${current_hu}" ]]; then
    return
  fi

  # Build title
  local title="${current_hu}: ${current_scenario}"
  
  # Build body with context
  local issue_body=$(cat <<EOF
**Épica:** ${current_epic}
**Historia de Usuario:** ${current_hu_title}
**Prioridad:** ${current_hu_priority}
**Estimación:** ${current_hu_estimate}
**Feature:** ${current_feature}

**Scenario:**
${current_scenario_content}

**Criterios de Aceptación:**
${current_scenario_content}
EOF
)

  echo "Creating issue: ${title}"
  
  # Create issue with enhancement label
  gh issue create \
    --repo "${REPO}" \
    --title "${title}" \
    --body "${issue_body}" \
    --label "enhancement" >/dev/null

  # Reset scenario variables
  current_scenario=""
  current_scenario_content=""
}

# Function to flush current scenario
flush_scenario() {
  create_scenario_issue
}

# Read the requirements file line by line
while IFS='' read -r line || [[ -n "$line" ]]; do
  # Epic detection
  if [[ "$line" =~ ^##\ ÉPICA\ ([0-9]+): ]]; then
    flush_scenario
    epic_num="${BASH_REMATCH[1]}"
    current_epic="ÉPICA ${epic_num}"
    in_feature=0
    in_scenario=0
    continue
  fi

  # HU heading detection
  if [[ "$line" =~ ^###\ HU-([0-9]{3}):\ (.*)$ ]]; then
    flush_scenario
    current_hu="HU-${BASH_REMATCH[1]}"
    current_hu_title="${BASH_REMATCH[2]}"
    current_hu_priority=""
    current_hu_estimate=""
    current_feature=""
    in_feature=0
    in_scenario=0
    continue
  fi

  # Priority
  if [[ "$line" =~ ^\*\*Prioridad:\*\*\ (.*)$ ]]; then
    current_hu_priority=$(echo "${BASH_REMATCH[1]}" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
    continue
  fi

  # Estimate
  if [[ "$line" =~ ^\*\*Estimación:\*\*\ (.*)$ ]]; then
    current_hu_estimate=$(echo "${BASH_REMATCH[1]}" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
    continue
  fi

  # Feature detection
  if [[ "$line" =~ ^Feature:\ (.*)$ ]]; then
    flush_scenario
    current_feature="${BASH_REMATCH[1]}"
    in_feature=1
    in_scenario=0
    continue
  fi

  # Scenario detection
  if [[ "$line" =~ ^\ \ Scenario:\ (.*)$ ]]; then
    flush_scenario
    current_scenario="${BASH_REMATCH[1]}"
    current_scenario_content=""
    in_scenario=1
    continue
  fi

  # End of gherkin block
  if [[ "$line" =~ ^\`\`\`$ ]] && [[ $in_gherkin -eq 1 ]]; then
    in_gherkin=0
    continue
  fi

  # Start of gherkin block
  if [[ "$line" =~ ^\`\`\`gherkin$ ]]; then
    in_gherkin=1
    continue
  fi

  # Capture scenario content (lines that start with spaces in gherkin blocks)
  if [[ $in_gherkin -eq 1 ]] && [[ $in_scenario -eq 1 ]] && [[ "$line" =~ ^\ \ .* ]]; then
    current_scenario_content+="$line\n"
  fi

done < "${REQ_PATH}"

# Flush last scenario if pending
flush_scenario

echo "All scenario issues created successfully in ${REPO}."
echo "Each scenario from the requirements now has its own issue."
