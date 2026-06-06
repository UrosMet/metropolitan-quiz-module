\## ER dijagram baze



<p align="center">

&#x20; <img src="./docs/er-diagram.png" alt="ER dijagram baze" width="900">

</p>



\### ObjaÅ¡njenje modela



Tabela `app_users` Äuva demo korisnike sistema. Korisnik moÅ¾e imati ulogu `TEACHER` ili `STUDENT`.



Tabela `quizzes` predstavlja kviz. Svaki kviz pripada jednom nastavniku preko kolone `teacher_id`. Kviz ima vremenski prozor definisan kolonama `opens_at` i `closes_at`, kao i status `DRAFT` ili `PUBLISHED`.



Tabela `questions` Äuva pitanja za kviz. Jedan kviz moÅ¾e imati viÅ¡e pitanja. Svako pitanje ima tekst, broj poena, tip pitanja i poziciju u kvizu.



Tabela `answer_options` Äuva ponuÄ‘ene odgovore za svako pitanje. Polje `is_correct` oznaÄava da li je opcija taÄna. Ovo polje se koristi samo na backend-u za ocenjivanje i ne vraÄ‡a se studentu kroz API.



Tabela `submissions` predstavlja jednu studentsku predaju kviza. Svaka predaja pripada jednom studentu i jednom kvizu. Rezultat se Äuva u koloni `score`.



Tabela `submission_answers` Äuva pojedinaÄne izabrane opcije u okviru jedne predaje. Na ovaj naÄin se moÅ¾e rekonstruisati koje opcije je student izabrao za svako pitanje.



\### OgraniÄenje jedne predaje



Na tabeli `submissions` postoji unique constraint nad kolonama:



```sql

UNIQUE (quiz_id, student_id)


