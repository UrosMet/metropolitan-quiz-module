\# Metropolitan Quiz Module



Mini full-stack modul za rad sa kvizovima u LMS sistemu, urađen kao praktični zadatak za poziciju Junior Full-Stack Developer na Metropolitan univerzitetu.



Aplikacija omogućava nastavniku da kreira i objavi kviz, a studentu da vidi dostupne kvizove, otvori kviz, preda odgovore i pregleda rezultat.



Trenutno je implementiran backend deo aplikacije. Frontend će biti dodat kasnije u folderu `frontend`.



\---



\## Tehnologije



\### Backend



\- Java 21

\- Spring Boot 3.x

\- Spring Web

\- Spring Data JPA

\- Bean Validation

\- Flyway

\- PostgreSQL

\- JUnit 5

\- AssertJ

\- Maven Wrapper



\### Frontend



\- React 19

\- Vite

\- TypeScript



Frontend još nije implementiran u trenutnoj fazi.



\---



\## Struktura projekta



```text

metropolitan-quiz-module/

├── backend/

│   ├── src/

│   ├── pom.xml

│   ├── mvnw

│   ├── mvnw.cmd

│   ├── docker-compose.yml

│   └── ...

│

├── frontend/

│   └── .gitkeep

│

├── README.md

└── DESIGN.md

```



\---



\## Preduslovi



Za pokretanje backend aplikacije potrebno je:



\- Java 21

\- PostgreSQL

\- Maven Wrapper, koji je već uključen u projekat

\- Git

\- opciono Docker Desktop, ako se baza pokreće preko Docker Compose-a



Provera Java verzije:



```bash

java -version

```



Očekivano je da bude Java 21.



Provera PostgreSQL-a:



```bash

psql --version

```



Provera Docker-a, ako se koristi Docker:



```bash

docker --version

docker compose version

```



\---



\## Pokretanje baze



Backend očekuje PostgreSQL bazu sa sledećim podacima:



```text

Host: localhost

Port: 5432

Database: quiz\_lms

Username: quiz\_user

Password: quiz\_pass

```



Postoje dve opcije za pokretanje baze.



\---



\## Opcija A: PostgreSQL preko Docker Compose-a



Iz root foldera projekta ući u backend folder:



```bash

cd backend

```



Pokrenuti PostgreSQL:



```bash

docker compose up -d

```



Proveriti da li container radi:



```bash

docker ps

```



Očekivani container:



```text

metropolitan\_quiz\_postgres

```



\---



\## Opcija B: Lokalno instaliran PostgreSQL



Ako se koristi lokalno instaliran PostgreSQL, potrebno je ručno napraviti korisnika i bazu.



Ući u PostgreSQL kao `postgres` korisnik:



```bash

psql -U postgres

```



Zatim izvršiti:



```sql

CREATE USER quiz\_user WITH PASSWORD 'quiz\_pass';



CREATE DATABASE quiz\_lms OWNER quiz\_user;



GRANT ALL PRIVILEGES ON DATABASE quiz\_lms TO quiz\_user;

```



Izlaz iz PostgreSQL konzole:



```sql

\\q

```



Provera konekcije:



```bash

psql -U quiz\_user -d quiz\_lms -h localhost

```



Password:



```text

quiz\_pass

```



\---



\## Backend konfiguracija



Konfiguracija backend aplikacije nalazi se u fajlu:



```text

backend/src/main/resources/application.yml

```



Backend koristi konekciju:



```yaml

spring:

&#x20; datasource:

&#x20;   url: jdbc:postgresql://localhost:5432/quiz\_lms

&#x20;   username: quiz\_user

&#x20;   password: quiz\_pass

```



Flyway automatski izvršava migracije prilikom pokretanja aplikacije.



Hibernate je podešen na:



```yaml

ddl-auto: validate

```



To znači da Hibernate ne menja bazu automatski, već samo proverava da li entity klase odgovaraju šemi baze koju pravi Flyway.



\---



\## Flyway migracije



Migracije se nalaze u:



```text

backend/src/main/resources/db/migration/

```



Trenutne migracije:



```text

V1\_\_init\_schema.sql

V2\_\_seed\_demo\_data.sql

V3\_\_use\_decimal\_scores.sql

```



Migracije rade sledeće:



\- `V1\_\_init\_schema.sql` kreira glavne tabele.

\- `V2\_\_seed\_demo\_data.sql` ubacuje demo korisnike i demo kviz.

\- `V3\_\_use\_decimal\_scores.sql` menja rezultat iz celog broja u decimalni format zbog delimičnog bodovanja kod `MULTIPLE\_CHOICE` pitanja.



\---



\## Pokretanje backend-a



Iz root foldera projekta:



```bash

cd backend

```



Pokretanje testova na Windows-u:



```bash

mvnw.cmd clean test

```



Pokretanje testova na Linux/macOS-u:



```bash

./mvnw clean test

```



Pokretanje aplikacije na Windows-u:



```bash

mvnw.cmd spring-boot:run

```



Pokretanje aplikacije na Linux/macOS-u:



```bash

./mvnw spring-boot:run

```



Backend se pokreće na:



```text

http://localhost:8080

```



\---



\## Demo korisnici



Autentikacija je van obima zadatka.



Identitet korisnika se simulira preko HTTP header-a:



```http

X-User-Id

X-User-Role

```



Demo korisnici ubačeni su kroz Flyway seed migraciju.



\### Nastavnik



```http

X-User-Id: 1

X-User-Role: TEACHER

```



\### Student 1



```http

X-User-Id: 2

X-User-Role: STUDENT

```



\### Student 2



```http

X-User-Id: 3

X-User-Role: STUDENT

```



Ako header-i nedostaju, API vraća `401 Unauthorized`.



Ako korisnik nema odgovarajuću ulogu, API vraća `403 Forbidden`.



\---



\## Implementirani backend endpoint-i



\### Teacher



```http

POST /api/teacher/quizzes

```



Kreiranje kviza u `DRAFT` statusu.



```http

POST /api/teacher/quizzes/{quizId}/publish

```



Objavljivanje kviza uz validaciju poslovnih pravila.



\### Student



```http

GET /api/student/quizzes/available

```



Lista dostupnih objavljenih kvizova koji su trenutno u vremenskom prozoru.



```http

GET /api/student/quizzes/{quizId}

```



Otvaranje kviza za rad bez informacija o tačnim odgovorima.



```http

POST /api/student/quizzes/{quizId}/submit

```



Predaja odgovora i automatsko računanje rezultata.



```http

GET /api/student/quizzes/{quizId}/result

```



Pregled sopstvenog rezultata za urađen kviz.



\---



\## Poslovna pravila



Aplikacija sprovodi sledeća pravila na backend-u:



\- kviz se kreira kao `DRAFT`

\- samo validan kviz može biti objavljen

\- student vidi samo objavljene kvizove koji su trenutno otvoreni

\- student ne može da otvori draft kviz

\- student ne može da preda kviz van vremenskog prozora

\- student može samo jednom da preda isti kviz

\- tačni odgovori se ne vraćaju studentu pre predaje

\- rezultat se automatski računa na serveru

\- dupla predaja je sprečena aplikativnom proverom i unique constraint-om u bazi



\---



\## Pravila ocenjivanja



\### SINGLE\_CHOICE



Za `SINGLE\_CHOICE` pitanje važi:



\- student dobija pune poene ako izabere tačnu opciju

\- student dobija `0` poena ako izabere netačnu opciju

\- student dobija `0` poena ako ne odgovori



\### MULTIPLE\_CHOICE



Za `MULTIPLE\_CHOICE` pitanja koristi se delimično bodovanje.



Pravilo:



\- ako student izabere sve tačne opcije i nijednu netačnu, dobija pune poene

\- ako student izabere deo tačnih opcija i nijednu netačnu, dobija proporcionalne poene

\- ako student izabere bilo koju netačnu opciju, dobija `0` poena za to pitanje

\- ako ne izabere ništa, dobija `0` poena



Ovo pravilo sprečava studenta da označi sve opcije radi dobijanja poena, ali ipak nagrađuje delimično znanje ako nije izabrana netačna opcija.



\---



\## Testovi



Pokretanje testova na Windows-u:



```bash

cd backend

mvnw.cmd clean test

```



Pokretanje testova na Linux/macOS-u:



```bash

cd backend

./mvnw clean test

```



Trenutno postoje unit testovi za `GradingService`.



Testovima je pokriveno:



\- `SINGLE\_CHOICE` tačan odgovor

\- `SINGLE\_CHOICE` netačan odgovor

\- `SINGLE\_CHOICE` bez odgovora

\- `MULTIPLE\_CHOICE` pun rezultat

\- `MULTIPLE\_CHOICE` delimičan rezultat

\- `MULTIPLE\_CHOICE` sa netačnom opcijom

\- `MULTIPLE\_CHOICE` bez odgovora

\- mešovit kviz

\- maksimalan broj poena



\---



\## HTTP status kodovi



Aplikacija koristi sledeće HTTP status kodove:



```text

200 OK - uspešan zahtev

201 Created - uspešno kreiran kviz

400 Bad Request - neispravan request format ili validacija ulaza

401 Unauthorized - nedostaju X-User-Id ili X-User-Role header-i

403 Forbidden - korisnik nema odgovarajuću ulogu

404 Not Found - resurs nije pronađen

409 Conflict - student je već predao kviz

422 Unprocessable Entity - poslovno pravilo nije ispunjeno

```



\---



\## Napomene



\- Autentikacija je simulirana preko HTTP header-a, prema zahtevu zadatka.

\- Ne koristi se Spring Security, JWT, login ili registracija.

\- Baza se modeluje kroz Flyway migracije.

\- Hibernate `ddl-auto` je podešen na `validate`.

\- Tačni odgovori se ne vraćaju kroz student endpoint-e.

\- Rezultati se čuvaju kao decimalni brojevi zbog delimičnog bodovanja.

\- Dupla predaja je sprečena kroz `UNIQUE (quiz\_id, student\_id)` constraint.

\- Frontend će biti dodat u posebnoj fazi u folderu `frontend`.

