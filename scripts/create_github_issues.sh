#!/usr/bin/env bash

set -euo pipefail

# Usage:
#   ./scripts/create_github_issues.sh <owner/repo> [path/to/REQUIREMENTS.MD]
#
# Example:
#   ./scripts/create_github_issues.sh luisiscander/ChatAi ./REQUIREMENTS.MD
#
# Notes:
# - Requires GitHub CLI: https://cli.github.com/
# - Authenticate first: gh auth login
# - This script creates labels (idempotent) and issues for each HU in REQUIREMENTS.MD

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

# Helper: create or update a label (idempotent)
create_label() {
  local name="$1"
  local color="$2"
  local desc="$3"
  # Try to create; if exists, update with --force
  if ! gh label create "${name}" --color "${color}" --description "${desc}" --repo "${REPO}" 2>/dev/null; then
    gh label edit "${name}" --color "${color}" --description "${desc}" --repo "${REPO}" >/dev/null
  fi
}

echo "Creating standard labels (priority, estimate, type/epic placeholders)..."

# Priority labels
create_label "priority: Alta"  "d73a4a" "Alta prioridad"
create_label "priority: Media" "fbca04" "Prioridad media"
create_label "priority: Baja"  "0e8a16" "Baja prioridad"

# Estimate labels (0-13+ common range)
for pts in 1 2 3 5 8 13; do
  create_label "estimate: ${pts}" "5319e7" "Estimación en puntos: ${pts}"
done

# Type label for user stories
create_label "type: historia de usuario" "cfd3d7" "Historia de usuario (HU)"

# Detect epics and create epic labels dynamically (e.g., "epic: ÉPICA 1")
echo "Scanning epics to create epic labels..."
grep -E "^## +ÉPICA +[0-9]+:" -n "${REQ_PATH}" | sed -E 's/^.*ÉPICA ([0-9]+):.*/\1/' | sort -u | while read -r epic_num; do
  [[ -z "${epic_num}" ]] && continue
  create_label "epic: ÉPICA ${epic_num}" "1d76db" "Epica ${epic_num}"
done

echo "Parsing historias de usuario (HU-XXX) y creando issues..."

# We will parse REQUIREMENTS.MD by blocks between HU headings.
# For each HU block, we capture: HU code, Title, Priority, Estimate, current Epic context, and first Gherkin block as body.

current_epic=""
hu_code=""
hu_title=""
priority=""
estimate=""
body=""
in_gherkin=0

flush_issue() {
  if [[ -z "${hu_code}" ]]; then
    return
  fi

  local title="${hu_code}: ${hu_title}"
  # Build labels list
  labels=("type: historia de usuario")
  if [[ -n "${current_epic}" ]]; then
    labels+=("epic: ${current_epic}")
  fi
  if [[ -n "${priority}" ]]; then
    labels+=("priority: ${priority}")
  fi
  if [[ -n "${estimate}" ]]; then
    # Use only the numeric part if present (e.g., "3 puntos" -> 3)
    est_num=$(echo "${estimate}" | grep -Eo "[0-9]+" || true)
    if [[ -n "${est_num}" ]]; then
      labels+=("estimate: ${est_num}")
    fi
  fi

  # Compose body with metadata header and the captured gherkin
  issue_body=$(cat <<EOF
Épica: ${current_epic}
Prioridad: ${priority}
Estimación: ${estimate}

Descripción y criterios de aceptación:

${body}
EOF
)

  # Create issue
  # Join labels by comma
  IFS="," read -r -a labels_csv <<< "${labels[*]}"
  echo "Creating issue: ${title}"
  gh issue create \
    --repo "${REPO}" \
    --title "${title}" \
    --body "${issue_body}" \
    --label "${labels[@]}" >/dev/null

  # Reset HU vars
  hu_code=""
  hu_title=""
  priority=""
  estimate=""
  body=""
  in_gherkin=0
}

while IFS='' read -r line || [[ -n "$line" ]]; do
  # Epic detection
  if [[ "$line" =~ ^##\ ÉPICA\ ([0-9]+): ]]; then
    epic_num="${BASH_REMATCH[1]}"
    current_epic="ÉPICA ${epic_num}"
    continue
  fi

  # New HU heading flushes previous HU
  if [[ "$line" =~ ^###\ HU-([0-9]{3}):\ (.*)$ ]]; then
    flush_issue
    hu_code="HU-${BASH_REMATCH[1]}"
    hu_title="${BASH_REMATCH[2]}"
    continue
  fi

  # Priority
  if [[ "$line" =~ ^\*\*Prioridad:\*\*\ (.*)$ ]]; then
    priority="${BASH_REMATCH[1]}"
    continue
  fi

  # Estimate
  if [[ "$line" =~ ^\*\*Estimación:\*\*\ (.*)$ ]]; then
    estimate="${BASH_REMATCH[1]}"
    continue
  fi

  # Start/End gherkin block
  if [[ "$line" =~ ^```gherkin$ ]]; then
    in_gherkin=1
    body+=$'```gherkin\n'
    continue
  fi
  if [[ "$line" =~ ^```$ ]] && [[ $in_gherkin -eq 1 ]]; then
    in_gherkin=0
    body+=$'```\n'
    continue
  fi

  # Capture gherkin content
  if [[ $in_gherkin -eq 1 ]]; then
    body+="$line\n"
  fi
done < "${REQ_PATH}"

# Flush last HU if pending
flush_issue

echo "All issues created successfully in ${REPO}."



