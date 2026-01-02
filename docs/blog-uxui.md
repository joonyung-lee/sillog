# Blog UX/UI 설계 문서

## 개요

긴 형식의 글을 작성하고 공유하는 "Blog" 기능.
마크다운 파일로 작성하고, 글 목록과 상세 페이지로 구성된다.

---

## 디자인 원칙

| 원칙 | 설명 |
|------|------|
| 간결함 | 불필요한 장식 없이 텍스트 중심 |
| 직선적 | border-radius 최소화, 날카로운 모서리 |
| 단색 | 흑백 + 회색 계열만 사용, 강조색 없음 |
| 가독성 | 긴 글에 최적화된 타이포그래피, 적절한 줄 간격 |

---

## 색상 팔레트

```
ink      #1a1a1a   본문 텍스트
muted    #6b7280   보조 텍스트 (날짜, 메타)
subtle   #f8f9fa   배경 (필요시)
border   #e5e7eb   구분선
white    #ffffff   기본 배경
```

---

## 라우팅

| URL | 페이지 |
|-----|--------|
| `/blog` | 글 목록 |
| `/blog/{slug}` | 글 상세 |

---

## 파일 구조

```
content/
└── blog/
    ├── my-first-post.md
    ├── learning-kotlin.md
    └── ...
```

### 마크다운 Frontmatter

```yaml
---
title: 코틀린을 배우며
date: 2025-01-02
summary: 코틀린의 주요 특징과 자바와의 차이점을 정리했다.
---

본문 내용...
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `title` | string | ✓ | 글 제목 |
| `date` | date (YYYY-MM-DD) | ✓ | 작성일 |
| `summary` | string | | 글 요약 (목록에 표시, 선택) |

### 파일명 규칙

- `slug.md` 형식 (예: `learning-kotlin.md`)
- slug가 URL 경로가 됨 (`/blog/learning-kotlin`)
- kebab-case 권장

---

## 글 목록 페이지 (/blog)

### 레이아웃

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ joonyung                                       changelog   blog   about     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Blog                                                                       │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │                                                                         ││
│  │  2025. 1. 2    코틀린을 배우며                                          ││
│  │                코틀린의 주요 특징과 자바와의 차이점을 정리했다.          ││
│  │                                                                         ││
│  │  2024. 12. 15  블로그를 시작하며                                        ││
│  │                sillog 블로그를 만들게 된 이유와 목표.                    ││
│  │                                                                         ││
│  │  2024. 11. 30  Spring WebFlux 입문                                      ││
│  │                리액티브 프로그래밍의 기초와 WebFlux 사용법.              ││
│  │                                                                         ││
│  └─────────────────────────────────────────────────────────────────────────┘│
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 목록 아이템 구조

```
┌───────────────────────────────────────────────────────────────────┐
│ [날짜]         [제목]                                              │
│                [요약 (있으면)]                                      │
└───────────────────────────────────────────────────────────────────┘
```

| 요소 | 너비 | 스타일 |
|------|------|--------|
| 날짜 | 100px (고정) | text-sm, text-muted, 오른쪽 정렬 |
| 간격 | 24px | |
| 제목 | 나머지 | text-base, text-ink, font-medium |
| 요약 | 나머지 | text-sm, text-muted |

### HTML 구조

```html
<a href="/blog/learning-kotlin" class="block group">
  <article class="flex gap-6 py-4 border-b border-border">
    <div class="w-24 flex-shrink-0 text-right">
      <time class="text-sm text-muted">2025. 1. 2</time>
    </div>
    <div class="flex-1">
      <h2 class="text-base font-medium text-ink group-hover:underline">
        코틀린을 배우며
      </h2>
      <p class="text-sm text-muted mt-1">
        코틀린의 주요 특징과 자바와의 차이점을 정리했다.
      </p>
    </div>
  </article>
</a>
```

### 인터랙션

| 요소 | 동작 |
|------|------|
| 아이템 hover | 제목에 underline |
| 아이템 클릭 | 글 상세 페이지로 이동 |

---

## 글 상세 페이지 (/blog/{slug})

### 레이아웃

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ joonyung                                       changelog   blog   about     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                    ┌─────────────────────────────────────┐                  │
│                    │                                     │                  │
│                    │  코틀린을 배우며                     │                  │
│                    │  2025. 1. 2                         │                  │
│                    │                                     │                  │
│                    │  ─────────────────────────────────  │                  │
│                    │                                     │                  │
│                    │  본문 내용이 여기에 표시됩니다.      │                  │
│                    │  마크다운으로 작성된 내용이          │                  │
│                    │  HTML로 렌더링되어 보여집니다.       │                  │
│                    │                                     │                  │
│                    │  ## 소제목                          │                  │
│                    │                                     │                  │
│                    │  단락 내용...                       │                  │
│                    │                                     │                  │
│                    │  - 목록 항목 1                      │                  │
│                    │  - 목록 항목 2                      │                  │
│                    │                                     │                  │
│                    │  ```kotlin                          │                  │
│                    │  fun main() {                       │                  │
│                    │      println("Hello")               │                  │
│                    │  }                                  │                  │
│                    │  ```                                │                  │
│                    │                                     │                  │
│                    └─────────────────────────────────────┘                  │
│                              └─── max-width: 680px ───┘                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 가독성 최적화

| 요소 | 값 | 이유 |
|------|-----|------|
| 본문 max-width | 680px | 한 줄에 45-75자 유지 (최적 가독성) |
| 본문 font-size | 17px | 편안한 읽기 크기 |
| 줄 간격 | 1.8 | 긴 글 가독성 향상 |
| 단락 간격 | 1.5em | 단락 구분 명확 |
| 제목 | 28px, font-weight 600 | 명확한 계층 구조 |
| 날짜 | 14px, muted | 보조 정보 |

### 헤더 영역

```html
<header class="mb-12">
  <h1 class="text-2xl font-semibold text-ink">코틀린을 배우며</h1>
  <time class="text-sm text-muted mt-2 block">2025. 1. 2</time>
</header>
<hr class="border-border mb-12">
```

### 본문 타이포그래피

```css
.blog-content {
  max-width: 680px;
  margin: 0 auto;
  font-size: 17px;
  line-height: 1.8;
  color: #1a1a1a;
}

.blog-content p {
  margin-bottom: 1.5em;
}

.blog-content h2 {
  font-size: 1.375em;  /* 22px */
  font-weight: 600;
  margin-top: 2.5em;
  margin-bottom: 0.75em;
}

.blog-content h3 {
  font-size: 1.125em;  /* 18px */
  font-weight: 600;
  margin-top: 2em;
  margin-bottom: 0.5em;
}

.blog-content ul, .blog-content ol {
  margin: 1.5em 0;
  padding-left: 1.5em;
}

.blog-content li {
  margin: 0.5em 0;
}

.blog-content blockquote {
  border-left: 3px solid #e5e7eb;
  padding-left: 1em;
  margin: 1.5em 0;
  color: #6b7280;
  font-style: italic;
}

.blog-content pre {
  background: #f8f9fa;
  padding: 1em;
  overflow-x: auto;
  margin: 1.5em 0;
  border: 1px solid #e5e7eb;
  font-size: 0.875em;
}

.blog-content code {
  font-family: ui-monospace, monospace;
  font-size: 0.9em;
  background: #f8f9fa;
  padding: 0.15em 0.4em;
}

.blog-content img {
  max-width: 100%;
  height: auto;
  margin: 2em 0;
  border: 1px solid #e5e7eb;
}

.blog-content a {
  color: #1a1a1a;
  text-decoration: underline;
  text-underline-offset: 3px;
}

.blog-content a:hover {
  color: #6b7280;
}
```

---

## 뒤로 가기 네비게이션

글 상세 페이지에서 목록으로 돌아가는 링크.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│  ← Blog                                                                     │
│                                                                             │
│                    ┌─────────────────────────────────────┐                  │
│                    │  코틀린을 배우며                     │                  │
│                    │  ...                                │                  │
```

```html
<a href="/blog" class="inline-flex items-center gap-1 text-sm text-muted hover:text-ink mb-8">
  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M15 19l-7-7 7-7"/>
  </svg>
  Blog
</a>
```

---

## 반응형

### Desktop (≥768px)

- 본문 max-width: 680px
- 좌우 여백 자동 (중앙 정렬)

### Mobile (<768px)

- 본문 100% (패딩 제외)
- 글꼴 크기 16px로 축소
- 줄 간격 1.7

---

## 도메인 모델

```kotlin
data class BlogPost(
    val slug: String,
    val title: String,
    val date: LocalDate,
    val summary: String?,
    val contentHtml: String
)
```

---

## API 설계

| Endpoint | Method | 설명 |
|----------|--------|------|
| `/blog` | GET | 글 목록 페이지 |
| `/blog/{slug}` | GET | 글 상세 페이지 |

---

## 구현 우선순위

### Phase 1: 기본 기능

- [ ] BlogPost 도메인 모델
- [ ] Frontmatter 파싱
- [ ] 글 목록 페이지
- [ ] 글 상세 페이지
- [ ] 본문 타이포그래피

### Phase 2: 개선

- [ ] 이전/다음 글 네비게이션
- [ ] 목차 (Table of Contents)
- [ ] 코드 하이라이팅

---

## 참고

- 강조색 없음
- 그림자 없음
- 애니메이션 없음
- border-radius 없음 (0px)
- 본문 가독성 최우선
- 680px max-width로 최적의 줄 길이 유지

