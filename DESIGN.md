\## ER dijagram baze



```mermaid

erDiagram

&#x20;   APP\_USERS ||--o{ QUIZZES : creates

&#x20;   APP\_USERS ||--o{ SUBMISSIONS : submits



&#x20;   QUIZZES ||--o{ QUESTIONS : contains

&#x20;   QUIZZES ||--o{ SUBMISSIONS : has



&#x20;   QUESTIONS ||--o{ ANSWER\_OPTIONS : has

&#x20;   QUESTIONS ||--o{ SUBMISSION\_ANSWERS : answered\_for



&#x20;   ANSWER\_OPTIONS ||--o{ SUBMISSION\_ANSWERS : selected\_as



&#x20;   SUBMISSIONS ||--o{ SUBMISSION\_ANSWERS : contains



&#x20;   APP\_USERS {

&#x20;       BIGINT id PK

&#x20;       VARCHAR full\_name

&#x20;       VARCHAR role

&#x20;       TIMESTAMP created\_at

&#x20;   }



&#x20;   QUIZZES {

&#x20;       BIGINT id PK

&#x20;       BIGINT teacher\_id FK

&#x20;       VARCHAR title

&#x20;       TEXT description

&#x20;       TIMESTAMP opens\_at

&#x20;       TIMESTAMP closes\_at

&#x20;       VARCHAR status

&#x20;       TIMESTAMP created\_at

&#x20;       TIMESTAMP updated\_at

&#x20;   }



&#x20;   QUESTIONS {

&#x20;       BIGINT id PK

&#x20;       BIGINT quiz\_id FK

&#x20;       TEXT text

&#x20;       INTEGER points

&#x20;       VARCHAR type

&#x20;       INTEGER position

&#x20;   }



&#x20;   ANSWER\_OPTIONS {

&#x20;       BIGINT id PK

&#x20;       BIGINT question\_id FK

&#x20;       TEXT text

&#x20;       BOOLEAN is\_correct

&#x20;       INTEGER position

&#x20;   }



&#x20;   SUBMISSIONS {

&#x20;       BIGINT id PK

&#x20;       BIGINT quiz\_id FK

&#x20;       BIGINT student\_id FK

&#x20;       NUMERIC score

&#x20;       TIMESTAMP submitted\_at

&#x20;   }



&#x20;   SUBMISSION\_ANSWERS {

&#x20;       BIGINT id PK

&#x20;       BIGINT submission\_id FK

&#x20;       BIGINT question\_id FK

&#x20;       BIGINT option\_id FK

&#x20;   }

```



\### Objašnjenje modela



Tabela `app\_users` čuva demo korisnike sistema. Korisnik može imati ulogu `TEACHER` ili `STUDENT`.



Tabela `quizzes` predstavlja kviz. Svaki kviz pripada jednom nastavniku preko kolone `teacher\_id`. Kviz ima vremenski prozor definisan kolonama `opens\_at` i `closes\_at`, kao i status `DRAFT` ili `PUBLISHED`.



Tabela `questions` čuva pitanja za kviz. Jedan kviz može imati više pitanja. Svako pitanje ima tekst, broj poena, tip pitanja i poziciju u kvizu.



Tabela `answer\_options` čuva ponuđene odgovore za svako pitanje. Polje `is\_correct` označava da li je opcija tačna. Ovo polje se koristi samo na backend-u za ocenjivanje i ne vraća se studentu kroz API.



Tabela `submissions` predstavlja jednu studentsku predaju kviza. Svaka predaja pripada jednom studentu i jednom kvizu. Rezultat se čuva u koloni `score`.



Tabela `submission\_answers` čuva pojedinačne izabrane opcije u okviru jedne predaje. Na ovaj način se može rekonstruisati koje opcije je student izabrao za svako pitanje.



\### Ograničenje jedne predaje



Na tabeli `submissions` postoji unique constraint nad kolonama:



```sql

UNIQUE (quiz\_id, student\_id)

