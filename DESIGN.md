# Alertify Design System

This document outlines the design tokens and system extracted from the Stitch project `16471863901000993630`.

## 1. Core Design Tokens

### Typography
- **Headline Font:** Manrope
- **Body Font:** Plus Jakarta Sans
- **Label Font:** Plus Jakarta Sans
- **Base Font:** Manrope

### Shape & Spacing
- **Roundness:** ROUND_FULL
- **Spacing Scale:** 3

### Color Palette (Named Colors)

| Token Name | Hex Code |
| :--- | :--- |
| **Primary** | #005f9b |
| **Primary Container** | #47a6f7 |
| **On Primary** | #ecf3ff |
| **Secondary** | #006855 |
| **Secondary Container** | #5ffbd6 |
| **On Secondary** | #c4ffec |
| **Tertiary** | #6d5a00 |
| **Tertiary Container** | #fad538 |
| **On Tertiary** | #fff2ce |
| **Background** | #f5f7f9 |
| **On Background** | #2c2f31 |
| **Surface** | #f5f7f9 |
| **On Surface** | #2c2f31 |
| **Surface Variant** | #d9dde0 |
| **On Surface Variant** | #595c5e |
| **Outline** | #747779 |
| **Error** | #b31b25 |
| **Error Container** | #fb5151 |
| **On Error** | #ffefee |

---

## 2. Design Philosophy & Guidelines

### Overview & Creative North Star: "The Guardian's Glow"
This design system moves away from the cold, clinical aesthetic of traditional security software and embraces **"The Guardian's Glow."** The North Star for this experience is a synthesis of safety and sophistication—a digital environment that feels as protective as it is premium.

To break the "template" look, we utilize **Asymmetric Depth**. By overlapping glass panels and using significant typography scale shifts, we create a sense of intentional, editorial hierarchy. The UI is not a flat plane; it is a multi-dimensional space where critical information "floats" closer to the user, while secondary controls recede into a soft, blurred background.

---

### Colors & Surface Philosophy
The palette is rooted in a clinical `surface-bright` (#f5f7f9), but it is brought to life through "vibrational" gradients that signal safety and activity.

#### The "No-Line" Rule
**Explicit Instruction:** 1px solid borders are strictly prohibited for sectioning or containment. Boundaries must be defined through:
1. **Background Shifts:** e.g., a `surface-container-low` section resting on a `background` base.
2. **Tonal Transitions:** Using subtle shifts between container tiers to imply edge.
3. **Physical Offset:** Creating depth through layering rather than outlining.

#### Surface Hierarchy & Nesting
Treat the UI as a series of stacked sheets of frosted glass.
- **Base Layer:** `background` (#f5f7f9).
- **Sectional Layer:** `surface-container-low` (#eef1f3) for grouping related content.
- **Interactive Layer:** `surface-container-lowest` (#ffffff) for cards and primary touch targets.
- **Floating Layer:** Glassmorphism panels.

#### The "Glass & Gradient" Rule
Floating elements (Modals, Navigation Bars, Emergency Triggers) must use Glassmorphism.
- **Effect:** `surface-container-lowest` at 60-80% opacity with a `20px - 40px` backdrop-blur.
- **Signature Textures:** Apply a linear gradient from `primary` (#005f9b) to `primary-container` (#47a6f7) for high-stakes CTAs. For "Safe" status indicators, use a gradient transition from `secondary` (#006855) to `secondary-container` (#5ffbd6).

---

### Typography: Editorial Authority
We use a high-contrast pairing to balance technical precision with human warmth.

- **Display & Headlines (Manrope):** The "Authority" face. Large, bold, and expansive.
- **Body & Labels (Plus Jakarta Sans):** The "Human" face. Highly legible and modern.

---

### Elevation & Depth: Tonal Layering
Traditional structural lines are replaced by physical light logic.

- **The Layering Principle:** To lift a card, place a `surface-container-lowest` card on top of a `surface-container-low` background.
- **Ambient Shadows:** For floating glass panels, use a shadow with a 40px blur and 6% opacity.
- **The "Ghost Border" Fallback:** If a container requires further definition, use a "Ghost Border": the `outline-variant` token at **15% opacity**.
- **Soft Glow:** High-priority elements (like the "SOS" trigger) should emit a soft outer glow.

---

### Components: The Premium Kit

#### Buttons
- **Primary:** Gradient-fill (`primary` to `primary-container`), `xl` roundedness (3rem), with a subtle white inner-glow (1px, 20% opacity) at the top edge.
- **Secondary:** Glassmorphic base, `outline-variant` Ghost Border (15%), `xl` roundedness.

#### Input Fields
- **Styling:** No bottom line. Use `surface-container-high` as a filled background with `md` (1.5rem) corners.
- **Active State:** Transition background to `surface-container-lowest` and apply a `primary` Ghost Border.

#### Cards & Lists
- **Standard:** Use `surface-container-lowest` on a `surface` background.
- **Divider Rule:** **Forbid 1px dividers.** Separate list items using 12px of vertical white space or by alternating very subtle background tints.

#### Signature Component: The "Status Halo"
A large, semi-transparent circular element behind the main dashboard metric that pulses with a gradient of `secondary` (Green) when safe, or `error_container` (Red) during alerts. This uses `backdrop-blur` to soften the entire UI's appearance.

---

### Do’s and Don’ts

#### Do:
- **Do** use extreme roundedness (`lg` to `xl`) for all interactive containers.
- **Do** embrace asymmetry.
- **Do** allow background gradients to "bleed" through glass elements.

#### Don’t:
- **Don’t** use black (#000000) for text or shadows. Use `on-surface` (#2c2f31).
- **Don’t** use standard Material "elevated" shadows.
- **Don’t** use hard-edged rectangles.
- **Don’t** clutter. increase font size and add white space if it feels busy.
