### ER dijagram baze



<p align="center">

&#x20; <img src="./docs/er-diagram.png" alt="ER dijagram baze" width="900">

</p>



### Objasnjenje modela

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

