# Changelog UX/UI 설계 문서

## 개요

매일의 짧은 기록을 남기는 "Changelog" 기능.  
마크다운 파일로 작성하고, 일자별로 타임라인 형태로 표시한다.  
Goal Tree와 통합하여 목표 달성 추적도 가능하다.

---

## 디자인 원칙

| 원칙 | 설명 |
|------|------|
| 간결함 | 불필요한 장식 없이 텍스트 중심 |
| 직선적 | border-radius 최소화, 날카로운 모서리 |
| 단색 | 흑백 + 회색 계열만 사용, 강조색 없음 |
| 밀도 | 정보 밀도 높게, 여백은 가독성 확보 수준만 |

---

## 색상 팔레트

```
ink      #1a1a1a   본문 텍스트
muted    #6b7280   보조 텍스트 (날짜, 메타)
subtle   #f8f9fa   배경 (필요시)
border   #e5e7eb   구분선
white    #ffffff   기본 배경
```

강조색(accent) 사용하지 않음.

---

## 타이포그래피

- 폰트: Pretendard (시스템 폰트 폴백)
- 본문: 14px, line-height 1.6
- 날짜: 12px, uppercase, letter-spacing 0.05em
- 제목(년/월 구분): 11px, muted, uppercase

---

## 레이아웃 (2-Column, 독립 스크롤)

### 전체 구조

전체 페이지 스크롤 없이, 좌우 영역이 각각 독립적으로 스크롤된다.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ joonyung                                       changelog   blog   about     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Changelog                                                                  │
│  Daily notes                                                                │
│                                                                             │
│  ┌──────────────────────────────────────────────────┬──────────────────────┐│
│  │                                                  │ Goals          [▼]  ││
│  │  2025                                            │ ┌────────────────────┤│
│  │                                                  │ │ 커리어 ─────── 55% ││
│  │  DEC 30   ✓ Docker 배포 설정                     │ │ ├─ 8구단 1순위 40% ││
│  │           ✓ Caddy 학습                           │ │ │  ├─ 몸만들기  50% ││
│  │                                                  │ │ │  │  ├─ ✓ 체력    ││
│  │           Caddy handle 블록 순서가               │ │ │  │  └─ ○ 근력    ││
│  │           라우팅 우선순위를 결정.                │ │ │  └─ 멘탈 ────  0% ││
│  │                                                  │ │ └─ sillog ──── 66% ││
│  │  DEC 29   Spring 설정 정리.                      │ │    ├─ ✓ 기본 기능  ││
│  │                                                  │ │    ├─ ✓ Docker 배포││
│  │  DEC 28   htmx 무한 스크롤 구현.                 │ │    └─ ○ Goal 트리  ││
│  │                                                  │ │                    ││
│  │  ...                                             │ │ (스크롤 가능)       ││
│  │                                                  │ └────────────────────┤│
│  │  ▼ (무한 스크롤)                                 │                      ││
│  │                                                  │ ┌────────────────────┤│
│  │  (이 영역 자체 스크롤)                           │ │ Dec 2025     ◀ ▶  ││
│  │                                                  │ ├────────────────────┤│
│  │                                                  │ │ S  M  T  W  T  F  S││
│  │                                                  │ │     1  2  3  4  5  6│
│  │                                                  │ │  7  8  9 10 11 12 13│
│  │                                                  │ │ 14 15 16 17 18 19 20│
│  │                                                  │ │ 21 22 23 24 25 26 27│
│  │                                                  │ │ 28 29 [30] 31       │
│  │                                                  │ └────────────────────┤│
│  └──────────────────────────────────────────────────┴──────────────────────┘│
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
        └─────────────── 70% ───────────────┘  └────────── 30% ──────────┘
```

### 영역 구성

| 영역 | 너비 | 높이 | 스크롤 |
|------|------|------|--------|
| Changelog | 70% | 100% (남은 높이) | 자체 스크롤 (무한 스크롤) |
| Goals | 30% 상단 | flex-1 (가변) | 자체 스크롤 |
| Calendar | 30% 하단 | 자동 (~180px) | 스크롤 없음 (고정) |

### 스크롤 전략: 독립 스크롤

```
┌─────────────────────────────────────────────────────────────────┐
│ Header (고정, 56px)                                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────┐  ┌──────────────────────────┐ │
│  │                              │  │ Goals                    │ │
│  │  Changelog                   │  │ (overflow-y: auto)       │ │
│  │  (overflow-y: auto)          │  │ (flex: 1)                │ │
│  │                              │  │                          │ │
│  │                              │  ├──────────────────────────┤ │
│  │                              │  │ Calendar                 │ │
│  │                              │  │ (flex-shrink: 0)         │ │
│  └──────────────────────────────┘  └──────────────────────────┘ │
│                                                                 │
│  height: calc(100vh - header - padding)                         │
└─────────────────────────────────────────────────────────────────┘
```

**장점:**
- Goals가 길어져도 Calendar는 항상 보임
- Changelog 무한 스크롤과 독립적으로 동작
- 날짜 하이라이트 기능이 항상 visible한 Calendar에서 작동

**구현:**
```css
.main-container {
  height: calc(100vh - 56px - 80px); /* header + padding */
  overflow: hidden;
}

.changelog-panel {
  overflow-y: auto;
}

.right-panel {
  display: flex;
  flex-direction: column;
}

.goals-section {
  flex: 1;
  overflow-y: auto;
  min-height: 0; /* flex item 스크롤을 위해 필요 */
}

.calendar-section {
  flex-shrink: 0;
}
```

---

## Entry 레이아웃 (인라인 스타일)

### 기본 형태

카드 형식 대신 인라인 형식으로, 날짜와 내용이 수평 배치된다.

```
DEC 30   ✓ Docker 배포 설정
         ✓ Caddy 학습

         Caddy handle 블록 순서가 라우팅 우선순위를 결정.

DEC 29   Spring 설정 정리.

DEC 28   htmx 무한 스크롤 구현.
         센티넬 방식으로 양방향 로딩 지원.
```

### 레이아웃 구조

```
┌─────────┬──────────────────────────────────────────────────┐
│ 날짜    │ 내용 (여러 줄 가능)                               │
│ (고정)  │                                                  │
└─────────┴──────────────────────────────────────────────────┘
```

| 요소 | 너비 | 정렬 | 스타일 |
|------|------|------|--------|
| 날짜 | 80px (고정) | 오른쪽 정렬 | 12px, muted, uppercase |
| 간격 | 16px | - | 공백 |
| 내용 | 나머지 | 왼쪽 정렬 | 14px, ink |

### HTML 구조

```html
<div class="entry">
  <div class="entry-date">DEC 30</div>
  <div class="entry-content">
    <div class="completed-goals">
      <span>✓ Docker 배포 설정</span>
      <span>✓ Caddy 학습</span>
    </div>
    <div class="notes">
      Caddy handle 블록 순서가 라우팅 우선순위를 결정.
    </div>
  </div>
</div>
```

### CSS

```css
.entry {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.entry-date {
  width: 80px;
  flex-shrink: 0;
  text-align: right;
  font-size: 12px;
  color: var(--muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.entry-content {
  flex: 1;
  font-size: 14px;
  line-height: 1.6;
}
```

### 년도 구분선

```
2025
────────────────────────────────────────────────

DEC 30   내용...
```

- 년도: 11px, muted, font-weight 500
- 구분선: 1px solid border
- margin: 32px 0 24px 0

---

## Goal Tree

### 핵심 개념

| 개념 | 설명 |
|------|------|
| Goal Tree | 계층적 목표 구조 (YAML 파일로 정의) |
| Goal | 트리의 노드 (parent 0개 = root, 1개 = 자식) |
| Daily Entry | 날짜별 기록 (완료한 목표 + 자유 노트) |
| 달성률 | 자식 노드들의 완료 비율 (리프는 완료 여부) |

### YAML 구조

```yaml
# content/goals.yaml
- id: career
  title: 커리어
  children:
    - id: draft-1st
      title: 8구단 1순위
      children:
        - id: body
          title: 몸만들기
          children:
            - id: stamina
              title: 체력
            - id: strength
              title: 근력
        - id: mental
          title: 멘탈
    - id: sillog
      title: sillog 블로그 완성
      children:
        - id: sillog-basic
          title: 기본 기능
        - id: sillog-docker
          title: Docker 배포
        - id: sillog-goals
          title: Goal 트리 기능

- id: health
  title: 건강
  children:
    - id: running
      title: 주 3회 러닝
```

### Status 정의

| Status | 의미 | Leaf에서 | 부모 노드에서 |
|--------|------|----------|---------------|
| `PENDING` | 시작 전 | 직접 설정 | 자식이 모두 PENDING |
| `IN_PROGRESS` | 진행 중 | 직접 설정 | 자식 중 하나라도 시작됨 |
| `DONE` | 완료 | 직접 설정 + completedAt | 자식이 모두 DONE |

**규칙:**
- Leaf 노드만 `status`와 `completedAt` 직접 가짐
- 부모 노드의 상태는 자식들로부터 **파생** (derivedStatus)
- `completedAt`은 `status: DONE`일 때만 유효

### 진행률 계산

```
progressRate(goal) =
  if goal is leaf:
    return status == DONE ? 1.0 : 0.0
  else:
    return average(children.map(progressRate))

derivedStatus(goal) =
  if goal is leaf:
    return status
  else if all children are DONE:
    return DONE
  else if any child is not PENDING:
    return IN_PROGRESS
  else:
    return PENDING
```

### Leaf 노드 스타일 (Status별)

```
┌─ PENDING (시작 전) ───────────────────────────────────────────┐
│                                                               │
│   ○ 근력                                                      │
│                                                               │
│   아이콘: ○ (빈 원)                                            │
│   제목: text-muted                                            │
│   우측: (없음)                                                 │
│                                                               │
└───────────────────────────────────────────────────────────────┘

┌─ IN_PROGRESS (진행 중) ───────────────────────────────────────┐
│                                                               │
│   ◐ 체력                                                      │
│                                                               │
│   아이콘: ◐ (반원) 또는 ▸ (삼각형)                             │
│   제목: text-ink                                              │
│   우측: (없음)                                                 │
│                                                               │
└───────────────────────────────────────────────────────────────┘

┌─ DONE (완료) ─────────────────────────────────────────────────┐
│                                                               │
│   ✓ 기본 기능                               2025. 12. 28      │
│                                                               │
│   아이콘: ✓ (체크)                                            │
│   제목: text-ink                                              │
│   우측: completedAt 날짜 (yyyy. M. d 형식), text-muted        │
│                                                               │
└───────────────────────────────────────────────────────────────┘
```

### 부모 노드 스타일 (파생 Status별)

```
┌─ PENDING (0%) ────────────────────────────────────────────────┐
│                                                               │
│   ▸ 멘탈                               [░░░░░░░░░░░░░░]  0%   │
│                                                               │
│   아이콘: ▸ (삼각형)                                          │
│   제목: text-muted                                            │
│   우측: 빈 프로그레스 바 + 퍼센트                              │
│                                                               │
└───────────────────────────────────────────────────────────────┘

┌─ IN_PROGRESS (1~99%) ─────────────────────────────────────────┐
│                                                               │
│   ▸ 몸만들기                           [████████░░░░░░] 50%   │
│                                                               │
│   아이콘: ▸ (삼각형)                                          │
│   제목: text-ink                                              │
│   우측: 프로그레스 바 + 퍼센트                                 │
│                                                               │
└───────────────────────────────────────────────────────────────┘

┌─ DONE (100%) ─────────────────────────────────────────────────┐
│                                                               │
│   ✓ sillog 완성                              2025. 12. 30     │
│                                                               │
│   아이콘: ✓ (체크)                                            │
│   제목: text-ink                                              │
│   우측: 마지막 자식의 completedAt (프로그레스 바 대신 날짜)    │
│                                                               │
└───────────────────────────────────────────────────────────────┘
```

### 노드 레이아웃 구조

```
┌─────────────────────────────────────────────────────────────┐
│ [아이콘] [제목]                              [우측 영역]     │
└─────────────────────────────────────────────────────────────┘

아이콘 (w-4):
  - ○  PENDING leaf
  - ◐  IN_PROGRESS leaf  
  - ✓  DONE (leaf 또는 부모)
  - ▸  부모 노드 (PENDING/IN_PROGRESS)

제목:
  - text-muted: PENDING
  - text-ink: IN_PROGRESS, DONE

우측 영역 (ml-auto):
  - 프로그레스 바 + %: 부모 노드 (PENDING, IN_PROGRESS)
  - 날짜 (yyyy. M. d): DONE (completedAt 있을 때)
  - (없음): Leaf의 PENDING, IN_PROGRESS
```

### CSS/Tailwind 클래스

| 요소 | Status | 클래스 |
|------|--------|--------|
| 아이콘 | PENDING | `text-muted text-xs` |
| 아이콘 | IN_PROGRESS | `text-ink text-xs` |
| 아이콘 | DONE | `text-ink text-xs` |
| 제목 | PENDING | `text-sm text-muted` |
| 제목 | IN_PROGRESS | `text-sm text-ink` |
| 제목 | DONE | `text-sm text-ink` |
| 날짜 | DONE | `text-xs text-muted tabular-nums` |
| 프로그레스 바 배경 | - | `w-16 h-1 bg-subtle` |
| 프로그레스 바 채움 | - | `h-full bg-ink` |
| 퍼센트 | - | `text-xs text-muted tabular-nums` |

### 트리 전체 예시

```
▸ 커리어 ────────────────────────────────────── [████████░░░░] 66%
├─ ▸ 8구단 1순위 ────────────────────────────── [████░░░░░░░░] 33%
│  ├─ ▸ 몸만들기 ────────────────────────────── [██████░░░░░░] 50%
│  │  ├─ ✓ 체력 ────────────────────────────────── 2025. 12. 15
│  │  └─ ○ 근력
│  └─ ○ 멘탈
└─ ✓ sillog 완성 ──────────────────────────────── 2025. 12. 30
   ├─ ✓ 기본 기능 ──────────────────────────────── 2025. 12. 25
   ├─ ✓ Docker 배포 ────────────────────────────── 2025. 12. 28
   └─ ✓ Goal 트리 ──────────────────────────────── 2025. 12. 30

▸ 건강 ──────────────────────────────────────── [░░░░░░░░░░░░]  0%
└─ ◐ 주 3회 러닝
```

---

## Daily Entry 포맷

### YAML frontmatter + 마크다운

```yaml
---
completed:
  - sillog-docker
  - sillog-basic
---
Caddy handle 블록 순서가 라우팅 우선순위를 결정.
내일은 Goal 트리 UI 작업 시작.
```

| 필드 | 타입 | 설명 |
|------|------|------|
| completed | string[] | 완료한 goal id 목록 |
| (본문) | markdown | 자유 기록 |

### 파일 구조

```
content/
├── goals.yaml           ← 목표 트리 정의
└── changelog/
    ├── 2025-12-30/
    │   └── content.md   ← 일별 기록 (frontmatter + 마크다운)
    └── ...
```

---

## 캘린더

### 접기/펴기

**펼친 상태 (기본):**
```
┌────────────────────────────────┐
│ ▼ 2025년 12월           < >   │
├────────────────────────────────┤
│ 일 월 화 수 목 금 토           │
│    1  2  3  4  5  6           │
│  7  8  9 10 11 12 13          │
│ 14 15 16 17 18 19 20          │
│ 21 22 23 24 [25] 26 27        │
│ 28 29 30 31                   │
└────────────────────────────────┘
```

**접은 상태:**
```
┌────────────────────────────────┐
│ ▶ Calendar                    │
└────────────────────────────────┘
```

### 날짜 하이라이트

IntersectionObserver로 viewport에 보이는 엔트리의 날짜를 캘린더에서 하이라이트.

| 상태 | 스타일 |
|------|--------|
| 일반 (엔트리 있음) | `bg-subtle` |
| 하이라이트 (현재 보는 중) | `ring-2 ring-ink ring-offset-1` |
| 오늘 | `bg-ink text-white` |
| 엔트리 없음 | `text-muted` |

---

## 인터랙션

### Changelog

| 요소 | 동작 |
|------|------|
| 무한 스크롤 | htmx sentinel 방식, 양방향 로딩 |
| 날짜 클릭 | 해당 날짜로 스크롤 (캘린더에서) |

### Goal Tree

| 동작 | 결과 |
|------|------|
| 리프 노드 클릭 | 완료/미완료 토글 (오늘 날짜에 기록) |
| 부모 노드 클릭 | 접기/펼치기 (v2) |

### Goal 완료 토글 플로우

```
[사용자: Goal Tree에서 "Docker 배포" 클릭]
         │
         ▼
┌─────────────────────────────────────────┐
│ POST /htmx/goals/toggle                 │
│ Body: goalId=sillog-docker              │
└─────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│ Server:                                 │
│ 1. 오늘 날짜의 content.md 수정          │
│ 2. frontmatter에 completed 추가/제거    │
│ 3. 전체 달성률 재계산                   │
│ 4. 갱신된 트리 반환                     │
└─────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│ htmx OOB swap: Goal Tree 영역 갱신      │
└─────────────────────────────────────────┘
```

---

## 반응형

### Desktop (≥1024px)

```
┌────────────────────────────────────────────────────────────┐
│  Changelog 70%              │  Goals + Calendar 30%        │
│  (자체 스크롤)              │  (Goals: 자체 스크롤)        │
│                             │  (Calendar: 고정)            │
└────────────────────────────────────────────────────────────┘
```

### Mobile (<1024px)

```
┌────────────────────────────────┐
│ Goals (collapsed)              │  ← 접힌 상태 기본
├────────────────────────────────┤
│ Calendar (collapsed)           │  ← 접힌 상태 기본
├────────────────────────────────┤
│                                │
│ Changelog 100%                 │
│ (스크롤)                       │
│                                │
└────────────────────────────────┘
```

모바일에서는 Goals, Calendar 모두 접힌 상태로 시작.

---

## API 설계

### htmx Fragments

| Endpoint | Method | 설명 |
|----------|--------|------|
| `/htmx/changelog/entries` | GET | 엔트리 목록 |
| `/htmx/changelog/more-before` | GET | 과거 엔트리 로드 |
| `/htmx/changelog/more-after` | GET | 미래 엔트리 로드 |
| `/htmx/changelog/calendar` | GET | 캘린더 |
| `/htmx/goals/tree` | GET | Goal Tree |
| `/htmx/goals/toggle` | POST | 완료 토글 |

---

## 도메인 모델

```kotlin
data class Goal(
    val id: String,
    val title: String,
    val parentId: String?,
    val children: List<Goal> = emptyList()
)

data class ChangelogEntry(
    val date: LocalDate,
    val completedGoalIds: Set<String>,
    val contentHtml: String
)

data class GoalNodeView(
    val id: String,
    val title: String,
    val isLeaf: Boolean,
    val isCompleted: Boolean,
    val completionPercent: Int,
    val children: List<GoalNodeView>,
    val depth: Int
)
```

---

## 파일 구조

```
src/main/kotlin/dev/joonyung/sillog/
├── domain/
│   ├── Goal.kt
│   ├── ChangelogEntry.kt
│   └── GoalRepository.kt
├── application/
│   ├── ChangelogService.kt
│   └── GoalService.kt
├── infrastructure/
│   └── YamlGoalRepository.kt
└── inbound/
    └── controller/
        └── ChangelogController.kt
```

---

## 구현 우선순위

### Phase 1: Goal Tree 읽기 전용 ✅

- [x] Goal 도메인 모델
- [x] YamlGoalRepository (goals.yaml 읽기)
- [x] Goal Tree fragment
- [x] 레이아웃 변경

### Phase 2: Entry와 연동

- [ ] Entry frontmatter 파싱 (completed 필드)
- [ ] 달성률 계산 로직
- [ ] Entry 인라인 레이아웃 (날짜 | 내용)
- [ ] 트리에 완료 상태 반영
- [ ] 독립 스크롤 구현

### Phase 3: 완료 토글

- [ ] 트리에서 클릭으로 완료 토글
- [ ] content.md frontmatter 수정
- [ ] htmx OOB swap으로 갱신

### Phase 4: 개선

- [ ] Goal 추가/삭제 UI
- [ ] 트리 접기/펼치기
- [ ] 모바일 반응형

---

## 참고

- 강조색 없음
- 그림자 없음
- 애니메이션 없음
- border-radius 없음 (0px)
- 최소한의 요소로 최대한의 가독성
- 캘린더는 직접 구현 (라이브러리 사용 안 함)
- Goal Tree는 YAML 파일로 정의 (Repository 패턴으로 DB 전환 가능)
