---
name: Coastal Sunlight Modernism
colors:
  surface: '#f1fbff'
  surface-dim: '#c2dfe9'
  surface-bright: '#f1fbff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#e2f7ff'
  surface-container: '#d6f3fd'
  surface-container-high: '#d0edf7'
  surface-container-highest: '#cbe7f2'
  on-surface: '#021f26'
  on-surface-variant: '#3c4949'
  inverse-surface: '#19343c'
  inverse-on-surface: '#dbf5ff'
  outline: '#6c7a79'
  outline-variant: '#bbc9c8'
  surface-tint: '#006a68'
  primary: '#006a68'
  on-primary: '#ffffff'
  primary-container: '#25b7b5'
  on-primary-container: '#004242'
  inverse-primary: '#56d9d7'
  secondary: '#7d5700'
  on-secondary: '#ffffff'
  secondary-container: '#febe46'
  on-secondary-container: '#704e00'
  tertiary: '#675d49'
  on-tertiary: '#ffffff'
  tertiary-container: '#b0a48c'
  on-tertiary-container: '#423a28'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#77f6f3'
  primary-fixed-dim: '#56d9d7'
  on-primary-fixed: '#00201f'
  on-primary-fixed-variant: '#00504f'
  secondary-fixed: '#ffdeab'
  secondary-fixed-dim: '#fbbc44'
  on-secondary-fixed: '#271900'
  on-secondary-fixed-variant: '#5f4100'
  tertiary-fixed: '#efe1c7'
  tertiary-fixed-dim: '#d2c5ac'
  on-tertiary-fixed: '#211b0b'
  on-tertiary-fixed-variant: '#4e4633'
  background: '#f1fbff'
  on-background: '#021f26'
  surface-variant: '#cbe7f2'
typography:
  display:
    fontFamily: Plus Jakarta Sans
    fontSize: 48px
    fontWeight: '800'
    lineHeight: 56px
    letterSpacing: -0.02em
  display-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 36px
    fontWeight: '800'
    lineHeight: 44px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.015em
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 34px
    letterSpacing: -0.015em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 22px
    fontWeight: '700'
    lineHeight: 28px
    letterSpacing: -0.01em
  title-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  title-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 22px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 18px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 12px
    letterSpacing: 0.04em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-md: 1.5rem
  margin: 1rem
  margin-md: 2rem
  margin-lg: 3rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2.5rem
---

## Brand & Style

This design system expresses a vibrant, coastal lifestyle distilled into a clean, modern digital companion. Inspired by Da Nang’s dynamic geography—where ocean surf meets golden shorelines and marble cliffs—the visual tone balances the airy clarity of an international hospitality platform with the instant utility of urban transit tools.

The interface evokes optimism, relaxed confidence, and effortless movement. It avoids decorative clutter in favor of breathing space, crisp typographic contrast, and sunlight-infused warmth. The aesthetic relies on luminous tonal layering, pill-shaped interactions, and rich location imagery framed by soft, generous curves.

## Colors

The palette mirrors the sensory rhythm of coastal central Vietnam: turquoise surf, tropical sun, and deep ocean depths.

- **Primary Teal (`#25B7B5`)**: The navigational core. Used for primary calls-to-action, active bottom bar destinations, interactive map pins, and signature highlights.
- **Light Aqua (`#BFEFEA`) & Soft Aqua (`#EAF9F7`)**: Used for background washes, active container fills, and micro-interaction focus states to keep the experience light and refreshing.
- **Deep Ocean Teal (`#17323A`)**: The primary neutral. Replaces harsh pitch-blacks for all headline typography, primary icons, and structural focus rings.
- **Secondary Slate (`#6F7E82`)**: Subtle metadata, timestamps, crowd level text, and inactive tab indicators.
- **Accent Sunlight (`#FFBF47`) & Warm Sand (`#FFF1D6`)**: Used purposefully for reviews, ratings, real-time alerts, current status indicators, and budget tier pills.
- **Soft Border (`#DCEEEE`)**: Delimits structural segments with low-contrast precision, preserving depth without heavy separation lines.

## Typography

Plus Jakarta Sans is chosen for its geometric foundation, tall x-height, and open counters. It provides native, balanced diacritic rendering for Vietnamese characters while projecting an international, approachable feel.

- Headlines leverage tight letter spacing and bold weights to ground sections firmly against fluid imagery.
- Numeric ratings and price metrics adopt tabular alignment to ensure fast scanning in list views and map cards.
- Label styles are engineered for badges, time-of-day tags, and live crowd indicators, using high-weight compact layouts for legibility under bright outdoor conditions.

## Layout & Spacing

The layout operates on an 8pt base grid within a responsive fluid column structure:
- **Mobile (< 768px)**: 4-column layout with 16px outer margins and 16px gutters. Single column content stacks with horizontal snap-scroll rails for place cards, itineraries, and quick categories.
- **Tablet (768px - 1024px)**: 8-column layout with 32px margins and 24px gutters. Split-screen discovery (interactive map on the right, fluid feed on the left).
- **Desktop (> 1024px)**: 12-column layout capped at 1280px max-width with 48px outer margins. Supports 3-to-4 column content grids for curated trail cards.

Generous vertical spacing isolates narrative chunks, while compact horizontal spacing keeps metadata chips grouped tightly with their respective media.

## Elevation & Depth

Visual hierarchy combines warm ambient light with aqua-tinted soft shadows to simulate tropical daylight:

- **Level 0 (Flat)**: Transparent or canvas background (`#EAF9F7` or `#FFFFFF`).
- **Level 1 (Card & Rail)**: Elevated with `0px 4px 20px -2px rgba(23, 50, 58, 0.05)`, backed by a `1px` continuous border in `#DCEEEE`.
- **Level 2 (Hover & Floating Sheets)**: Elevated with `0px 12px 32px -4px rgba(37, 183, 181, 0.12)`, creating an ambient, oceanic glow beneath cards.
- **Level 3 (Modal & Floating Action Button)**: Elevated with `0px 20px 40px -8px rgba(23, 50, 58, 0.16)` with subtle backdrop blur (`12px`) when overlaying photography.
- **Glass Surfaces**: Top navigation bars and bottom navigation docks utilize `rgba(255, 255, 255, 0.85)` with `backdrop-filter: blur(16px)` and a bottom border of `#DCEEEE`.

## Shapes

The design system employs high-curvature geometry to evoke sea glass and wave silhouettes:

- **Primary Cards**: Generous 24px (`rounded-2xl`) or 32px (`rounded-3xl`) corner radii provide an inviting, modern frame for landscape and culinary photography.
- **Interactive Controls & Pills**: Buttons, input bars, category filters, and metadata tags are strictly fully rounded pills (`rounded-full` / `9999px`).
- **Inner Imagery**: Nested image corners mirror the outer parent card minus the internal padding (typically 16px–20px radii) to ensure optical concentricity.

## Components

### Buttons
- **Primary**: Solid Teal (`#25B7B5`) fill, Deep Ocean Teal or White label text, pill-shaped (`9999px`), 48px standard height, no border. Focus displays an outer 3px ring in `#BFEFEA`.
- **Secondary**: Soft Aqua (`#EAF9F7`) fill with Primary Teal text and a subtle `#DCEEEE` perimeter border.
- **Floating Action Button (Map View Toggle)**: Deep Ocean Teal (`#17323A`) fill with Pure White text and icon, elevated to Level 3.

### Discovery & Itinerary Cards
- Pure White base with 24px radius, surrounded by a 1px `#DCEEEE` border.
- 16:10 aspect ratio imagery with an inner 16px radius, overlaid with floating pills for ratings and saved status.
- Card footer displays location tags, estimated visit duration, and clear crowd level meters.

### Chips & Badges
- **Crowd Meters**: Pill-shaped badges using soft tinted fills. "Low" in Soft Aqua with Teal text, "Moderate" in Warm Sand with Amber text, "Peak" in soft coral tint.
- **Filter Chips**: Neutral White with `#DCEEEE` borders; shifts to Primary Teal fill with White text upon selection.
- **Best-Time Badges**: Warm Sand (`#FFF1D6`) background with `#17323A` text and `#FFBF47` accent icon (e.g., golden hour or sunset markers).

### Input Fields & Search Bars
- Pill-shaped 52px high container in Pure White, featuring a leading search icon in `#6F7E82`.
- Subtle resting border in `#DCEEEE`, expanding into a 2px Primary Teal focus ring with soft ambient glow.
- Quick filter clear buttons nested inside the trailing edge as circular soft-gray pills.

### Lists & Steps (Itinerary Timeline)
- Chronological trail items connected by a 2px dotted or solid vertical rule in `#BFEFEA`.
- Step nodes rendered as 12px Teal dots or 28px numbered aqua disks.
- Spacing between timeline stops remains fixed at 24px, with travel-time duration chips anchored centrally between steps.

### Checkboxes & Segmented Controls
- Segmented switches use an enclosing pill track in `#EAF9F7` with a sliding Pure White thumb element carrying Level 1 elevation.
- Checkboxes feature a 6px rounded square in `#DCEEEE` that fills with `#25B7B5` and a crisp white checkmark when checked.