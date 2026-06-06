# Metropolitan Quiz Module

Mini full-stack modul za rad sa kvizovima u LMS sistemu.

Projekat se sastoji iz dva dela:

```text
metropolitan-quiz-module/
├── backend/
└── frontend/
```

---

## Tehnologije

### Backend

- Java 21
- Spring Boot 3
- PostgreSQL
- Flyway
- Spring Data JPA
- Maven

### Frontend

- React
- Vite
- TypeScript

---

## Preduslovi

Pre pokretanja potrebno je instalirati:

- Java 21
- PostgreSQL ili Docker Desktop
- Node.js
- npm

Provera verzija:

```bash
java -version
node -v
npm -v
```

---

# Pokretanje backend-a

## 1. Pokretanje PostgreSQL baze

Ako se koristi Docker, prvo pokrenuti Docker Desktop, a zatim iz `backend` foldera pokrenuti:

```bash
cd backend
docker compose up -d
```

Baza koristi sledeće podatke:

```text
Database: quiz_lms
Username: quiz_user
Password: quiz_pass
Port: 5432
```

Ako se koristi lokalno instaliran PostgreSQL, potrebno je napraviti bazu `quiz_lms` i korisnika `quiz_user`.

---

## 2. Pokretanje backend aplikacije

Iz root foldera projekta ući u backend folder:

```bash
cd backend
```

Na Windows-u pokrenuti:

```bash
.\mvnw.cmd spring-boot:run
```

Backend se pokreće na:

```text
http://localhost:8080
```

---

## 3. Pokretanje backend testova

Iz root foldera projekta:

```bash
cd backend
.\mvnw.cmd clean test
```

---

## 4. Swagger dokumentacija

Kada je backend pokrenut, Swagger dokumentacija je dostupna na:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON je dostupan na:

```text
http://localhost:8080/v3/api-docs
```

---

# Pokretanje frontend-a

## 1. Instalacija dependency-ja

Iz root foldera projekta ući u frontend folder:

```bash
cd frontend
npm install
```

## 2. Pokretanje frontend aplikacije

```bash
npm run dev
```

Frontend se pokreće na:

```text
http://localhost:5173
```

---

# Pokretanje cele aplikacije

Za rad aplikacije potrebno je da istovremeno rade:

1. PostgreSQL baza
2. Backend na portu `8080`
3. Frontend na portu `5173`

Redosled pokretanja:

## Terminal 1 - baza i backend

```bash
cd backend
docker compose up -d
.\mvnw.cmd spring-boot:run
```

## Terminal 2 - frontend

```bash
cd frontend
npm install
npm run dev
```

Zatim otvoriti:

```text
http://localhost:5173
```

---

# Demo korisnici

Autentikacija je simulirana preko HTTP header-a.

U aplikaciji postoje demo korisnici:

```text
Nastavnik:
X-User-Id: 1
X-User-Role: TEACHER

Student 1:
X-User-Id: 2
X-User-Role: STUDENT

Student 2:
X-User-Id: 3
X-User-Role: STUDENT
```

Frontend automatski koristi ove demo korisnike kroz izbor korisnika u interfejsu.