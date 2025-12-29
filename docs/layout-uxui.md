# sillog Layout UX/UI Specification

## Overview

Minimal top-navigation layout. No sidebar. Content-focused design.

---

## Layout Structure

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ joonyung                                       changelog   blog   about     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                              MAIN CONTENT                                   │
│                            (max-width: 1024px)                              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Header

| Property | Value |
|----------|-------|
| Height | 56px (h-14) |
| Position | Sticky (fixed on scroll) |
| Background | white/95% + backdrop-blur |
| Border | 1px solid #e5e5e5 |
| Max-width | 1024px (max-w-5xl) |
| Padding | 32px horizontal (px-8) |

### Why Sticky?

- Header is small (56px) - minimal content loss
- 3 tabs only - always-visible navigation is convenient
- Calendar date clicks need quick tab access

### Logo

| Property | Value |
|----------|-------|
| Text | "joonyung" |
| Size | 18px (text-lg) |
| Weight | 600 (semibold) |
| Link | /changelog |

### Navigation

| Property | Value |
|----------|-------|
| Gap | 32px (gap-8) |
| Font size | 14px (text-sm) |
| Active | underline 2px + #111 |
| Inactive | #666, hover → #111 |

---

## Routing

| Request | Response |
|---------|----------|
| `/` | Redirect 302 → `/changelog` |
| `/changelog` | Changelog page |
| `/changelog?date=2025-12-31` | Changelog with specific date |
| `/blog` | Blog page (placeholder) |
| `/about` | About page |

---

## Changelog Layout

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ joonyung                                       changelog   blog   about     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Changelog                                                                  │
│  Daily notes                                                                │
│                                                                             │
│  ┌──────────────────────────────────────────────┐ ┌───────────────────────┐ │
│  │                                              │ │ ▼ Dec 2025     < >   │ │
│  │  Entry list                                  │ ├───────────────────────┤ │
│  │  (flex-1, infinite scroll)                   │ │ S M T W T F S        │ │
│  │                                              │ │ 1 2 3 4 5 6 7        │ │
│  │                                              │ │ ...                  │ │
│  │                                              │ └───────────────────────┘ │
│  │                                              │        (w-64, sticky)     │
│  └──────────────────────────────────────────────┘                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

| Element | Width | Notes |
|---------|-------|-------|
| Entries | flex-1 | Takes remaining space |
| Calendar | 256px (w-64) | Fixed width, sticky |
| Gap | 32px (gap-8) | |

---

## Content Area

| Property | Value |
|----------|-------|
| Max-width | 1024px (max-w-5xl) |
| Padding horizontal | 32px (px-8) |
| Padding vertical | 40px (py-10) |

---

## Typography

| Element | Size | Weight |
|---------|------|--------|
| Page title | 20px (text-xl) | 600 |
| Page subtitle | 14px (text-sm) | 400, muted |
| Body | 16px | 400 |
| Calendar | 10-11px | 500 |

---

## Color Palette

| Token | Value |
|-------|-------|
| ink | #111111 |
| muted | #666666 |
| subtle | #f5f5f5 |
| border | #e5e5e5 |

---

## Responsive

| Viewport | Layout |
|----------|--------|
| Desktop (≥1024px) | 2-column: entries + calendar |
| Mobile (<1024px) | 1-column: calendar (collapsed) above entries |

---

## Implementation Notes

- Header uses `bg-white/95 backdrop-blur` for subtle transparency
- Calendar uses fixed width (w-64) for consistent sizing
- Entries use `flex-1` to fill remaining space
- All page titles use consistent `text-xl font-semibold`
