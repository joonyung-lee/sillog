# sillog

A minimal personal blog for daily changelog entries. Built with server-rendered HTML, no JavaScript frameworks.

## Overview

sillog (from Korean "실록", meaning "veritable records") is a personal blog designed for short, chronological daily entries. Content is authored in Markdown and stored in a separate Git repository, enabling a clean separation between application code and content.

## Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ Internet                                                                    │
│      │                                                                      │
│      │ :443 (HTTPS)                                                         │
│      ▼                                                                      │
│ ┌─────────────────────────────────────────────────────────────────────────┐ │
│ │ Cloudflare (CDN + TLS termination)                                      │ │
│ └────────────────────────────┬────────────────────────────────────────────┘ │
│                              │ Tunnel                                       │
│                              ▼                                              │
│ ┌─────────────────────────────────────────────────────────────────────────┐ │
│ │ cloudflared                                                             │ │
│ │  • Cloudflare Tunnel connector                                          │ │
│ └────────────────────────────┬────────────────────────────────────────────┘ │
│                              │ :80                                          │
│                              ▼                                              │
│ ┌─────────────────────────────────────────────────────────────────────────┐ │
│ │ Caddy                                                                   │ │
│ │  • Static file serving (images)                                         │ │
│ │  • Reverse proxy to application                                         │ │
│ └────────────────────────────┬────────────────────────────────────────────┘ │
│                              │ :8080                                        │
│                              ▼                                              │
│ ┌─────────────────────────────────────────────────────────────────────────┐ │
│ │ Spring Boot (WebFlux)                                                   │ │
│ │  • Thymeleaf templates                                                  │ │
│ │  • htmx for dynamic updates                                             │ │
│ │  • Markdown parsing (CommonMark)                                        │ │
│ │  • Scheduled content sync (git pull)                                    │ │
│ └────────────────────────────┬────────────────────────────────────────────┘ │
│                              │                                              │
│                              ▼                                              │
│ ┌─────────────────────────────────────────────────────────────────────────┐ │
│ │ Content Volume                                                          │ │
│ │  /content/changelog/YYYY-MM-DD/content.md                               │ │
│ │  /content/blog/*.md                                                     │ │
│ │  /content/goals.yaml                                                    │ │
│ └─────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Tech Stack

| Layer | Technology |
|-------|------------|
| Runtime | Kotlin, JDK 21 |
| Framework | Spring Boot 4, Spring WebFlux |
| Templating | Thymeleaf |
| Frontend | [htmx](https://htmx.org), Tailwind CSS |
| Markdown | CommonMark |
| Reverse Proxy | Caddy |
| Container | Docker, Docker Compose |

## Local Development

```bash
./gradlew bootRun
# http://localhost:8080
```

## Deployment Commands

### First-time Setup (on host)

```bash
# 1. Content directory
sudo mkdir -p /var/data/sillog-content
sudo chown $USER:$USER /var/data/sillog-content
git clone <content-repo-url> /var/data/sillog-content

# 2. Environment file
cp .env.example .env
vim .env
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Spring profile (`local` / `prod`) | - |
| `SILLOG_WEBHOOK_API_KEY` | API key for content sync webhook | - |
| `CONTENT_PATH` | Host path to content directory | `/var/data/sillog-content` |
| `CADDYFILE` | Caddyfile to use | `Caddyfile` |
| `CLOUDFLARE_TUNNEL_TOKEN` | Cloudflare Tunnel token | - |

Example `.env` for local development:
```bash
SPRING_PROFILES_ACTIVE=local
SILLOG_WEBHOOK_API_KEY=local-dev-key
CONTENT_PATH=/Users/me/sillog-content
CADDYFILE=Caddyfile.local
CLOUDFLARE_TUNNEL_TOKEN=  # Not needed for local
```

Example `.env` for production:
```bash
SPRING_PROFILES_ACTIVE=prod
SILLOG_WEBHOOK_API_KEY=$(openssl rand -hex 32)
CONTENT_PATH=/var/data/sillog-content
CADDYFILE=Caddyfile
CLOUDFLARE_TUNNEL_TOKEN=your-tunnel-token-here
```

**Getting Cloudflare Tunnel Token:**
1. Go to [Cloudflare Zero Trust](https://one.dash.cloudflare.com/)
2. Navigate to Networks → Tunnels
3. Create a tunnel and copy the token
4. Configure the tunnel to route traffic to `http://caddy:80`

### Build & Deploy

```bash
# 1. Build JAR (required before Docker build)
./gradlew bootJar

# 2. Build image and deploy
docker compose up -d --build

# Or separately:
# docker compose build
# docker compose up -d
```

### Operations

```bash
# Logs
docker compose logs -f
docker compose logs -f sillog
docker compose logs -f caddy

# Restart
docker compose restart

# Stop
docker compose down

# Rebuild and redeploy
./gradlew bootJar && docker compose up -d --build
```

### Content Sync Webhook (GitHub Actions)

In content repository, create `.github/workflows/notify.yml`:

```yaml
name: Notify Server
on:
  push:
    branches: [main]
jobs:
  notify:
    runs-on: ubuntu-latest
    steps:
      - run: |
          curl -X POST \
            -H "Sillog-Webhook-Api-Key: ${{ secrets.SILLOG_WEBHOOK_API_KEY }}" \
            https://<domain>/api/webhook/content-sync
```
