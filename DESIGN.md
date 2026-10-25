---
name: KoraInsight Sports Analytics
colors:
  primary: '#3463CC'
  on-primary: '#FFFFFF'
  primary-container: '#E8EEFC'
  on-primary-container: '#1E3A8A'
  secondary: '#F05369'
  on-secondary: '#FFFFFF'
  secondary-container: '#FFECEF'
  on-secondary-container: '#7A1426'
  background: '#F6F8FE'
  on-background: '#1E242B'
  surface: '#FFFFFF'
  on-surface: '#1E242B'
  surface-variant: '#F0F3F8'
  on-surface-variant: '#8D98A5'
  outline: '#E2E8F0'
  outline-variant: '#CBD5E1'
  surface-dim: '#F0F4FC'
  surface-bright: '#FFFFFF'
  surface-container-lowest: '#FFFFFF'
  surface-container-low: '#F8FAFD'
  surface-container: '#F0F3F8'
  surface-container-high: '#E8EEFC'
  surface-container-highest: '#DFE7F6'
typography:
  display-xl:
    fontFamily: Plus Jakarta Sans
    fontSize: 34px
    fontWeight: '800'
    lineHeight: '1.2'
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '700'
    lineHeight: '1.2'
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: '1.3'
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '500'
    lineHeight: '1.5'
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: '1.5'
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: '1.2'
    letterSpacing: 0.04em
rounded:
  sm: 0.375rem
  DEFAULT: 0.75rem
  md: 1rem
  lg: 1.25rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  xs: 0.5rem
  sm: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  gutter: 1rem
  margin-mobile: 1rem
---

# KoraInsight Design System

KoraInsight is a high-performance football (soccer) intelligence and live match tracking mobile application. The visual language blends premium sports broadcasting aesthetics with clean, data-dense fintech clarity.

---

## 1. Brand & Visual Personality

- **Vibe:** Clean, modern, athletic, data-rich, distraction-free.
- **Lighting:** Light mode default with crisp elevated white cards resting on a subtle ice-tinted background (`#F6F8FE`).
- **Accent Philosophy:**
  - **Electric Blue (`#3463CC`)**: Core brand, active navigation, home team stats, primary buttons, major scores.
  - **Coral Red (`#F05369`)**: Live indicator pulse, opposition/away stats, red cards, key match alerts.

---

## 2. Color Palette & Roles

### Light Theme (Default)
| Token Name | Hex Code | Usage |
| :--- | :--- | :--- |
| **Primary** | `#3463CC` | Brand blue, active bottom tab icon/text, primary CTA buttons |
| **Primary Container** | `#E8EEFC` | Active navigation pill background, selected date pills, badge fills |
| **On Primary Container** | `#1E3A8A` | Deep contrast blue for icons and text inside primary container |
| **Secondary (Accent)** | `#F05369` | Live match tags ('72 LIVE), away/opposition highlights, alert badges |
| **Secondary Container** | `#FFECEF` | Soft pink/coral pill background for live matches or urgent alerts |
| **On Secondary Container**| `#7A1426` | Deep crimson for text on secondary container |
| **App Background** | `#F6F8FE` | Cool tinted light slate background beneath all cards |
| **Card Surface** | `#FFFFFF` | Match cards, floating navbar surface, modal dialogs |
| **Pill Surface Variant** | `#F0F3F8` | Unselected filter chips, league tab pills, search bar fill |
| **Dark On Surface** | `#1E242B` | Team names, scores, prominent titles, headers |
| **Slate On Surface Variant** | `#8D98A5` | Timestamps, league subtitles, match status, inactive tab icons |
| **Subtle Outline** | `#E2E8F0` | Card borders, dividers, subtle stroke lines |

### Dark Theme Counterparts
- **Primary:** `#90B4FF` | **Primary Container:** `#1B3D8A` | **On Primary Container:** `#D6E3FF`
- **Secondary:** `#FFB2BC` | **Secondary Container:** `#6B1829` | **On Secondary Container:** `#FFD9DF`
- **Background:** `#0F141C` | **Card Surface:** `#181F2A` | **Pill Variant:** `#222B38`
- **On Surface:** `#E6EDF5` | **On Surface Variant:** `#94A3B8` | **Outline:** `#2E3846`

---

## 3. Signature Component: Floating Bottom Navigation Bar

Every screen in KoraInsight features the signature floating bottom navigation bar that sits elevated above the scrolling content:

- **Shape & Geometry:** Pill shape with `32dp` rounded corners (`rounded-full`).
- **Positioning:** Floating anchored at the bottom edge:
  - Horizontal margin: `24dp` inset from left and right edges (`fillMaxWidth`).
  - Bottom margin: `12dp` lifted from bottom screen border.
- **Surface & Shadow:**
  - Background color: Solid `#FFFFFF` (`CardSurface`).
  - Drop shadow: High-elevation soft diffused drop shadow (`elevation: 24dp`, ambient color `#B9000000`, spot color `#46000000`).
- **Items & Tabs:**
  - 3 items: **Matches** (calendar icon), **Leagues** (trophy / star icon), **Profile** (person icon).
  - Arrangement: Even horizontal spacing (`Arrangement.SpaceAround`).
- **Active State Interaction:**
  - The active tab expands horizontally into a rounded capsule pill.
  - Background: `#E8EEFC` (`Primary Container`).
  - Icon & Text Color: `#3463CC` (`Primary Blue`).
  - Shows: 24dp Icon + 8dp spacing + `14sp SemiBold` label text (e.g. "Matches").
  - Padding inside pill: `16dp horizontal, 10dp vertical`.
- **Inactive State Interaction:**
  - Transparent background.
  - Shows: Icon only (`24dp`), tinted in `#8D98A5` (`SlateOnSurfaceVariant`). No label text visible.

---

## 4. UI Components & Layout Guidelines

### A. Match Scorecards
- **Shape:** `16dp` to `20dp` rounded corners (`rounded-xl`).
- **Surface:** `#FFFFFF` background with a subtle `#E2E8F0` 1px border and soft 4dp shadow.
- **Layout:**
  - Top row: League name (small uppercase `#8D98A5`), match status (e.g. `LIVE 68'` in `#F05369` pill or `FT` in `#8D98A5`).
  - Center: Home Team Logo + Name (`#1E242B`), Score (`2 - 1` bold `24sp`), Away Team Logo + Name.
  - Bottom row / details: Key events (e.g., scorers, red card indicator).

### B. Date & Filter Ribbon (Horizontal Chips)
- Horizontal scrollable row below top bar.
- Pill shape (`rounded-full`, 32dp radius).
- Selected pill: `#3463CC` background with `#FFFFFF` text or `#E8EEFC` background with `#1E3A8A` text.
- Unselected pill: `#F0F3F8` surface with `#8D98A5` text.

### C. Team Comparison & Stats Bars
- Dual-colored comparison meters:
  - Home team stat: `#3463CC` (Brand Blue).
  - Away team stat: `#F05369` (Coral Red) or neutral `#8D98A5`.
  - Track background: `#F0F3F8`.
  - Rounded ends (`rounded-full`).

---

## 5. Stitch Screen Generation Prompt (Ready to Use)

Use this prompt when generating screens in Stitch:

```text
Design a modern, high-polish mobile football (soccer) analytics and live matches screen for "KoraInsight" (Android, Light Mode, 390px mobile viewport).

Theme and Color Scheme:
- Canvas/App Background: Light ice-slate #F6F8FE
- Card Surface: Pure white #FFFFFF with soft border #E2E8F0 and smooth 6dp elevation
- Primary Brand Accent: Deep Royal Blue #3463CC (used for active buttons, home team stats, prominent scores)
- Primary Container: Soft Blue #E8EEFC with dark blue text #1E3A8A
- Secondary Accent: Coral Red #F05369 (used for LIVE badge, alert indicators, away team comparison)
- Text Colors: Main titles/scores in deep obsidian #1E242B, secondary metadata in muted slate #8D98A5
- Typography: Plus Jakarta Sans / Inter, bold dynamic headers, tabular numbers for match minutes and scores.

Screen Structure:
1. Top App Bar:
   - Greeting / App Title: "KoraInsight" with a live dot indicator.
   - Right side: Search icon button and Notification bell with a tiny coral badge.
2. Horizontal Date Selector:
   - Rounded pill buttons: "Yesterday", "Today (Active pill in #3463CC)", "Tomorrow", "Sat 26", "Sun 27".
3. Featured "Match of the Day" Live Hero Card:
   - Large white rounded card (20dp corner radius)
   - Top banner: "UEFA Champions League" with star icon and pulsating coral badge "'74 LIVE"
   - Matchup: Arsenal vs Real Madrid, large scores "2 - 1", team crest placeholders, scorers listed in 12sp slate.
   - Quick match stats preview: Possession bar (54% Blue #3463CC vs 46% Coral #F05369).
4. Matches List by Competition:
   - Section header: "Premier League" with league badge.
   - 2-3 compact match cards with team names, crests, kickoff time or current score, and match odds/form pill.
5. Floating Bottom Navigation Bar (Mandatory signature element):
   - Elevated floating white capsule bar with 32dp corner radius (full pill)
   - Lifted 12dp from screen bottom with 24dp side margin, deep soft drop shadow.
   - 3 tabs: "Matches", "Leagues", "Profile"
   - "Matches" is active: highlighted with an #E8EEFC rounded pill background containing a #3463CC calendar icon and "Matches" bold label.
   - "Leagues" and "Profile" are inactive: showing only subtle slate icons (#8D98A5) with no background.
```
