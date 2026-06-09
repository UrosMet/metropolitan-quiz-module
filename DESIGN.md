## ER dijagram baze



<p align="center">

<img src="./docs/er-diagram.png" alt="ER dijagram baze" width="900">

</p>



## Objasnjenje modela

Tabela `app_users` cuva demo korisnike sistema. Korisnik moze imati ulogu `TEACHER` ili `STUDENT`.

Tabela `quizzes` predstavlja kviz. Svaki kviz pripada jednom nastavniku preko kolone `teacher_id`. Kviz ima vremenski prozor definisan kolonama `opens_at` i `closes_at`, kao i status `DRAFT` ili `PUBLISHED`.

Tabela `questions` cuva pitanja za kviz. Jedan kviz moze imati vise pitanja. Svako pitanje ima tekst, broj poena, tip pitanja i poziciju u kvizu.

Tabela `answer_options` cuva ponudjene odgovore za svako pitanje. Polje `is_correct` oznacava da li je opcija tacna. Ovo polje se koristi samo na backend-u za ocenjivanje i ne vraca se studentu kroz API.

Tabela `submissions` predstavlja jednu studentsku predaju kviza. Svaka predaja pripada jednom studentu i jednom kvizu. Rezultat se cuva u koloni `score`.

Tabela `submission_answers` cuva pojedinacne izabrane opcije u okviru jedne predaje. Na ovaj nacin se moze rekonstruisati koje opcije je student izabrao za svako pitanje.

### Ogranicenje jedne predaje

Na tabeli `submissions` postoji unique constraint nad kolonama:

```sql
UNIQUE (quiz_id, student_id)
```

## Kljucne odluke i pretpostavke

### Simulacija autentikacije

Autentikacija je van obima zadatka, pa se korisnik simulira preko HTTP header-a:

```text
X-User-Id
X-User-Role
```

Na osnovu uloge `TEACHER` ili `STUDENT`, backend dozvoljava ili odbija pristup odredjenim endpoint-ima.

### Status kviza

Kviz se kreira u statusu `DRAFT`.

Studentima su dostupni samo kvizovi koji su:

- `PUBLISHED`
- trenutno u vremenskom prozoru izmedju `opensAt` i `closesAt`

Objavljivanje kviza je posebna akcija kako bi nastavnik prvo mogao da pripremi kviz, a zatim da ga objavi.

### Validacija pri objavljivanju

Kviz moze biti objavljen samo ako su ispunjena sledeca pravila:

- kviz ima bar jedno pitanje
- svako pitanje ima bar dve ponudjene opcije
- svako pitanje ima bar jednu tacnu opciju
- `SINGLE_CHOICE` pitanje ima tacno jednu tacnu opciju
- `opensAt` je pre `closesAt`

Ako pravila nisu ispunjena, backend vraca odgovarajucu gresku sa statusom `422 Unprocessable Entity`.

### Ocenjivanje pitanja

Za `SINGLE_CHOICE` pitanje student dobija pune poene samo ako izabere tacnu opciju. Ako izabere netacnu opciju ili ne odgovori, dobija 0 poena.

Za `MULTIPLE_CHOICE` pitanja koristi se delimicno bodovanje:

- ako student izabere sve tacne opcije i nijednu netacnu, dobija pune poene
- ako student izabere deo tacnih opcija i nijednu netacnu, dobija proporcionalne poene
- ako student izabere bilo koju netacnu opciju, dobija 0 poena za to pitanje
- ako ne izabere nista, dobija 0 poena

Ova odluka je doneta zato sto nagradjuje delimicno znanje, ali sprecava zloupotrebu gde bi student oznacio sve opcije kako bi pokusao da dobije poene.

### Zastita tacnih odgovora

Tacni odgovori se ne vracaju studentu kroz API. Polje `is_correct` postoji u bazi i koristi se samo na backend-u za automatsko ocenjivanje.

### Migracije baze

Sema baze se kreira kroz Flyway migracije. Hibernate je podesen na `ddl-auto: validate`, sto znaci da Hibernate ne menja automatski bazu, vec samo proverava da li entity klase odgovaraju semi baze.

## Lista API endpoint-a

### Nastavnik

| Metoda | Putanja | Opis |
|---|---|---|
| POST | `/api/teacher/quizzes` | Kreira novi kviz u `DRAFT` statusu sa pitanjima i opcijama. |
| GET | `/api/teacher/quizzes` | Vraca listu kvizova koje je kreirao nastavnik. |
| POST | `/api/teacher/quizzes/{quizId}/publish` | Objavljuje kviz ako su ispunjena sva poslovna pravila. |
| DELETE | `/api/teacher/quizzes/{quizId}` | Brise kviz ako je u `DRAFT` statusu. |

### Student

| Metoda | Putanja | Opis |
|---|---|---|
| GET | `/api/student/quizzes/available` | Vraca listu objavljenih kvizova koji su trenutno dostupni studentu. |
| GET | `/api/student/quizzes/{quizId}` | Otvara kviz i vraca pitanja i opcije bez tacnih odgovora. |
| POST | `/api/student/quizzes/{quizId}/submit` | Prima odgovore, racuna rezultat i cuva predaju. |
| GET | `/api/student/quizzes/{quizId}/result` | Vraca rezultat studenta za kviz koji je vec predao. |

### Dokumentacija

| Metoda | Putanja | Opis |
|---|---|---|
| GET | `/swagger-ui.html` | Swagger UI dokumentacija API-ja. |
| GET | `/v3/api-docs` | OpenAPI JSON specifikacija. |

## Testiranje

Implementirani su unit testovi za logiku ocenjivanja, ukljucujuci:

- 0 poena
- pun rezultat
- delimicni rezultat
- mesovite odgovore
- `SINGLE_CHOICE`
- `MULTIPLE_CHOICE`

Dodatno su dodati unit testovi za poslovna pravila na serveru:

- validacija pri objavljivanju kviza
- vremenski prozor za predaju kviza
- sprecavanje duple predaje

Dodat je i MockMvc integracioni test koji proverava studentski endpoint za listu dostupnih kvizova i ponasanje header autentikacije.

## Sta bih uradio sa vise vremena

Sa vise vremena bih dodao:

- paginaciju na listi kvizova
- editovanje vec kreiranog `DRAFT` kviza
- detaljniji prikaz rezultata po pitanju
- prikaz tacnih odgovora studentu
- pravu autentikaciju i autorizaciju preko Spring Security-ja i/ili JWT-a
- dodatni tip pitanja `TRUE_FALSE` (novi tip pitanja, koji bi automatski kreirao odgovore Tacno/Netacno)
- frontend je genericki, sa vise vremena bih unapredio CSS, responzivnost i pop-up poruke
- vise integracionih testova
- deployment konfiguraciju za produkciono okruzenje

Granice trenutnog resenja:

- autentikacija je simulirana preko header-a
- frontend koristi demo korisnike
- aplikacija je namenjena lokalnom pokretanju
- nije implementirano editovanje kviza nakon kreiranja







