# Ascend System (Solo Leveling–inspired Fitness Discipline)

## Overview
This Android app enforces a reality-based training log. Progress is earned, reversible, and backed by logged performance. The app blocks all access until Google Sign-In succeeds and onboarding is completed.

## Setup
1. Create a Firebase project and enable Authentication (Google) and Firestore.
2. Download `google-services.json` and place it in `app/`.
3. Replace `google_web_client_id` in `app/src/main/res/values/strings.xml` with your web client ID.
4. Run the app from Android Studio.

## Core Logic
### XP Logic
- XP is calculated per workout based on exercise type and performance volume.
- XP is intentionally linear and favors measurable output: reps, duration, or distance.
- Level is derived from total XP (`level = xp / 500 + 1`).

### Rank Logic
- Ranks (E → S) are based on *current* capability only.
- Rank upgrades require a manual evaluation and meeting thresholds for all required exercises.
- Rank can be downgraded through inactivity penalties.

### Integrity System
- Integrity score starts at 100 and can be reduced for manual entries, validation failures, and unrealistically fast completions.
- Low integrity blocks rank upgrades and removes XP multipliers until consistent data rebuilds trust.

### Inactivity Penalties
- On resume, the app compares today with the last active date and applies penalties:
  - 48 hours: warning only
  - 72 hours: streak reset, multiplier removed, minor integrity loss
  - 5 days: XP reduction, rank upgrades locked
  - 7 days: rank downgraded by 1 tier, significant XP loss
  - 14 days: multiple rank downgrade, major XP/integrity loss, re-evaluation required
  - 30+ days: rank reset to lowest tier, XP reduced to baseline level

## Code Structure
- `core/`: shared models and rules for XP, rank, integrity, and inactivity.
- `auth/`: Google Sign-In and Firebase Auth integration.
- `data/`: Firestore persistence.
- `ui/`: Compose screens for login, onboarding, home, workout, and rank.
