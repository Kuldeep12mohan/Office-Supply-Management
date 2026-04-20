# Frontend on Vercel + Local Backend

This setup deploys only the frontend to Vercel and keeps the Spring Boot backend running on your machine.

## 1) Run backend locally

```bash
cd backend
mvn spring-boot:run
```

Backend URL: `http://localhost:8080`

## 2) Allow your Vercel origin in backend CORS

In `backend/src/main/resources/application.properties`, set `app.cors.allowed-origins` to include both local dev and your Vercel URL:

```properties
app.cors.allowed-origins=http://localhost:3000,http://localhost:5173,https://YOUR-VERCEL-DOMAIN.vercel.app
```

Restart backend after changing this value.

## 3) Set Vercel environment variable

In Vercel Project Settings -> Environment Variables, add:

- `VITE_API_BASE_URL` = `http://localhost:8080/api`

If backend is not on the same machine as the browser, use a network-reachable URL instead of `localhost`.

## 4) Deploy frontend

- Import this repository in Vercel
- Set project root to `frontend`
- Build command: `npm run build`
- Output directory: `dist`
- Deploy

## 5) Quick verify

- Open deployed frontend URL
- Login and load dashboard data
- Confirm requests succeed in browser network tab
- If requests fail with CORS, re-check `app.cors.allowed-origins`

