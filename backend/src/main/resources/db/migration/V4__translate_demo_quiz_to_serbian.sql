UPDATE quizzes
SET
    title = 'Demo Java kviz',
    description = 'Osnovni demo kviz za lokalno testiranje.'
WHERE id = 1;

UPDATE questions
SET
    text = 'Koja ključna reč se koristi za kreiranje klase u Javi?'
WHERE id = 1;

UPDATE questions
SET
    text = 'Koji od sledećih pojmova su validni modifikatori pristupa u Javi?'
WHERE id = 2;

UPDATE answer_options
SET text = 'class'
WHERE id = 1;

UPDATE answer_options
SET text = 'function'
WHERE id = 2;

UPDATE answer_options
SET text = 'define'
WHERE id = 3;

UPDATE answer_options
SET text = 'public'
WHERE id = 4;

UPDATE answer_options
SET text = 'private'
WHERE id = 5;

UPDATE answer_options
SET text = 'protected'
WHERE id = 6;

UPDATE answer_options
SET text = 'internal'
WHERE id = 7;