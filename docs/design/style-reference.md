> **Inspiração visual apenas.** Não redefine o produto nem é checklist de componentes.
> Em conflito, prevalecem [product-brief.md](../product-brief.md), [prd/cambioo.md](../prd/cambioo.md) e [architecture.md](../architecture.md).
>
> **Fora da v1 (não implementar):** Currency Exchange Card com sparkline, badge com setas ↑↓, Quick Conversion Input Bar, Segmented Timeframe Switcher, layouts tablet/desktop multi-coluna, flag icons.
>
> **Útil na v1:** dark contido, hierarquia no número, contraste alto, cor reforçando alta/queda/estável sem ser o único sinal, tipografia expressiva (sans + mono opcional).

---
name: Parity Currency Interface
colors:
  surface: '#121316'
  surface-dim: '#121316'
  surface-bright: '#38393c'
  surface-container-lowest: '#0d0e11'
  surface-container-low: '#1b1b1f'
  surface-container: '#1f1f23'
  surface-container-high: '#292a2d'
  surface-container-highest: '#343538'
  on-surface: '#e3e2e6'
  on-surface-variant: '#c4c7c9'
  inverse-surface: '#e3e2e6'
  inverse-on-surface: '#303034'
  outline: '#8e9193'
  outline-variant: '#444749'
  surface-tint: '#c5c6c8'
  primary: '#ffffff'
  on-primary: '#2e3132'
  primary-container: '#e1e2e4'
  on-primary-container: '#626566'
  inverse-primary: '#5c5f60'
  secondary: '#70ffba'
  on-secondary: '#003822'
  secondary-container: '#01e599'
  on-secondary-container: '#00613e'
  tertiary: '#ffffff'
  on-tertiary: '#68000b'
  tertiary-container: '#ffdad7'
  on-tertiary-container: '#c31f29'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#e1e2e4'
  primary-fixed-dim: '#c5c6c8'
  on-primary-fixed: '#191c1e'
  on-primary-fixed-variant: '#444749'
  secondary-fixed: '#4dffb2'
  secondary-fixed-dim: '#00e297'
  on-secondary-fixed: '#002112'
  on-secondary-fixed-variant: '#005234'
  tertiary-fixed: '#ffdad7'
  tertiary-fixed-dim: '#ffb3ae'
  on-tertiary-fixed: '#410004'
  on-tertiary-fixed-variant: '#930014'
  background: '#121316'
  on-background: '#e3e2e6'
  surface-variant: '#343538'
typography:
  display-rate:
    fontFamily: Inter
    fontSize: 40px
    fontWeight: '600'
    lineHeight: 44px
    letterSpacing: -0.03em
  headline-lg:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 34px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 26px
    letterSpacing: -0.015em
  headline-sm:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 22px
    letterSpacing: -0.01em
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: -0.005em
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0em
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
    letterSpacing: 0em
  label-numeric:
    fontFamily: JetBrains Mono
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 18px
    letterSpacing: -0.01em
  label-delta:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  caption:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.04em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  gutter: 0.75rem
  gutter-md: 1rem
  margin: 1rem
  margin-tablet: 1.5rem
  margin-desktop: 2.5rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 0.75rem
  space-lg: 1.25rem
  space-xl: 2rem
---

## Brand & Style

### Visual Philosophy & Emotional Response
The design system is engineered around immediate legibility, mathematical precision, and an uncluttered editorial presence. Designed specifically for high-frequency currency checking and financial monitoring against the Brazilian Real (BRL), the visual tone eliminates ornamental noise in favor of high-contrast data prioritization. The interface elicits quiet confidence, speed, and analytical rigor.

### Aesthetic Movement
A synthesis of **Financial Modernism** and **Restrained Swiss Functionalism**. The design system favors:
- Monochromatic, atmospheric dark-mode surfaces with micro-contrast layer separation.
- High visual weight on tabular data points and exchange parity values.
- Muted semantic signaling where positive and negative yield changes are presented crisply without dominating the visual hierarchy.
- Seamless, borderless surface layering combined with razor-thin structural dividers (hairlines).

## Colors

### Surface Hierarchy & Foundation
The color architecture relies on a deep, obsidian-biased base palette to prevent OLED pixel fatigue and make currency pairings stand out instantly:
- **Base Canvas (`#0D0E11`)**: Deep carbon-neutral background.
- **Surface Elevation 1 (`#14161C`)**: Secondary card container fill.
- **Surface Elevation 2 (`#1B1E26`)**: Elevated inputs, active toggles, and modal sheets.
- **Border & Hairline (`#232733`)**: High-precision boundary separation for tabular rows and cards.

### Semantic & Market Direction
Market shifts reject neon oversaturation in favor of sharp, functional indicators:
- **Positive Parity (`#00E599`)**: High-legibility emerald for upward rate movements (`+%`).
- **Negative Parity (`#FF4D4D`)**: Crisp, balanced coral for downward rate movements (`-%`).
- **Neutral/Unchanged (`#7E8695`)**: Slate tone for flat volume and stable rate movements.

### Text & Data Contrast
- **Text High-Emphasis (`#F4F5F7`)**: Raw rate values, currency symbols, and primary figures.
- **Text Medium-Emphasis (`#9BA1B0`)**: Currency standard codes (USD, EUR, GBP) and time intervals.
- **Text Low-Emphasis (`#585E6E`)**: Footnotes, conversion timestamps, and sparkline axes.

## Typography

### Typographic Intent
The typography uses **Inter** for narrative, interface labels, and currency names, pairing it with **JetBrains Mono** exclusively for rates, tabular figure comparisons, and timestamp metadata. 

### Monospace & Tabular Figure Rules
- All numerical quotations (`display-rate`, values inside cards, percentage deltas) must strictly enforce OpenType tabular figures (`font-variant-numeric: tabular-nums;` or `font-feature-settings: "tnum" on, "cv05" on`). This prevents visual jitter when rates update dynamically.
- Monospace styling via JetBrains Mono provides unambiguous glyph differentiation (e.g., `0` with slash vs `O`, `1` vs `I`).

## Layout & Spacing

### Mobile-First Layout Model
The layout utilizes a fluid single-column hierarchy for mobile devices, progressing to multi-column analytical panels only on larger viewports:
- **Mobile (`< 600px`)**: Single column with continuous vertical scrolling. Margins are fixed at `1rem` (16px) to maximize horizontal data density.
- **Tablet (`600px – 1024px`)**: 2-column modular grid with `1.5rem` margins and `1rem` gutters, segregating major FX pairs from secondary conversion tracking.
- **Desktop (`> 1024px`)**: Centered maximum viewport constraint (`860px`) to preserve quick analytical scanning without eye-travel fatigue.

### Vertical Rhythm
A baseline rhythm of 4px anchors all spacing tokens:
- **Compact Pair Spacing (`space-xs` to `space-sm`)**: Used within rate-delta clusters and sparkline tags.
- **Structural Grouping (`space-md` to `space-lg`)**: Used between currency cards and filter segments.
- **Section Breaks (`space-xl`)**: Used to delineate conversion calculators from currency trend lists.

## Elevation & Depth

### Flat Tonal Layering
The design system avoids heavy drop shadows and skeletal glassmorphism, using calibrated dark surfaces and subtle outlines instead:

- **Level 0 (Canvas Base)**: `#0D0E11` — The static underlying surface.
- **Level 1 (Card Rest State)**: `#14161C` with a 1px solid border of `#232733`. Depth is communicated strictly by value contrast.
- **Level 2 (Active/Pressed Card)**: `#1B1E26` with a border transition to `#313747`. Zero displacement; elevation is confirmed through surface illumination.
- **Overlays & Drawers**: Backdrop tint using `rgba(13, 14, 17, 0.75)` with a 12px hardware-accelerated blur filter, focusing the user's attention on numeric keypads or currency search sheets.

## Shapes

### Edge Treatment
The shape language uses controlled, soft-edge geometry (`roundedness: 1`), balancing technical efficiency with modern ergonomics:
- **Micro UI & Badges**: 4px radius (`0.25rem`) for percentage pills and timestamp labels.
- **Base Cards & Inputs**: 8px radius (`0.5rem`) for currency line items, calculator fields, and segmented switches.
- **Bottom Sheets & Modal Surfaces**: 12px radius (`0.75rem`) on top corners for smooth slide-up actions.
- Circular rounding (`9999px`) is reserved solely for currency avatar flags or country indicator dots.

## Components

### Currency Exchange Card (Primary Atom)
The core component of the interface. Displays currency parity against BRL in a single scannable horizontal block.
- **Layout**: 3-zone layout inside a 68px height container:
  1. *Left*: Flag icon (24px) stacked horizontally with currency code (e.g., `USD`, `Inter SemiBold 16px`) and localized name (`Dólar Comercial`, `Inter Regular 12px`).
  2. *Center*: 30-day sparkline (width: 72px, height: 28px), rendered with vector smoothing, no fill gradient, and a 1.5px stroke width reflecting the net 30-day color (Emerald or Coral).
  3. *Right*: Spot conversion value (`R$ 5,642`, `JetBrains Mono Medium 16px`) stacked above the delta pill (`+0,32%`, `JetBrains Mono 12px`).
- **Interactive State**: Tap navigates directly to the expanded historical trajectory view; subtle background shift to `#1B1E26`.

### 30-Day Sparkline Micro-Graphic
- **Rendering**: SVG polyline without axis lines, labels, or point markers.
- **Stroke Color**: Semantic `#00E599` if `Rate[today] >= Rate[30d_ago]`; semantic `#FF4D4D` if `Rate[today] < Rate[30d_ago]`.
- **Stroke Style**: Continuous 1.5px curve (`tension: 0.2`).
- **End-cap Dot**: 3px filled circle positioned at the terminal point (latest rate).

### Delta Indicator Badge
- **Container**: Compact tag using 4px padding-x and 2px padding-y.
- **Fill**: Tinted background at 12% opacity of the semantic color (`rgba(0, 229, 153, 0.12)` or `rgba(255, 77, 77, 0.12)`).
- **Typography**: `label-delta` (JetBrains Mono SemiBold), rendered directly in the base semantic color. Preceded by standard unicode arrows (`↑` or `↓`).

### Quick Conversion Input Bar
- **Surface**: Embedded within Level 1 surface (`#14161C`).
- **Structure**: Borderless inline input field. Left side holds the base currency selector with a minimalist chevron; right side features high-legibility tabular entry for amounts.
- **Focus State**: Hairline border switches from `#232733` to `#F4F5F7`.

### Segmented Timeframe Switcher
- **Use**: Toggle between 1D, 7D, 30D, 1Y views.
- **Container**: Flat `#14161C` background with 4px inner padding.
- **Active Thumb**: `#1B1E26` background, 1px `#313747` border, `Inter SemiBold 12px` text in `#F4F5F7`.
- **Inactive Options**: Transparent background, `Inter Regular 12px` in `#7E8695`.

### Buttons
- **Primary CTA**: `#F4F5F7` background with `#0D0E11` text (high-contrast inversion). 44px minimum tap target, 8px border radius.
- **Secondary Action**: Bordered button with 1px `#232733` border, transparent background, and `#F4F5F7` text.
- **Ghost/Icon Button**: Transparent surface with 20px monochrome icons in `#9BA1B0`, shifting to `#F4F5F7` on press.