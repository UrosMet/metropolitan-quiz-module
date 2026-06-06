INSERT INTO app_users (id, full_name, role)
VALUES
    (1, 'Marko Markovic', 'TEACHER'),
    (2, 'Jovan Jovanovic', 'STUDENT'),
    (3, 'Ana Anic', 'STUDENT');

INSERT INTO quizzes (
    id,
    teacher_id,
    title,
    description,
    opens_at,
    closes_at,
    status
)
VALUES (
           1,
           1,
           'Demo Java Quiz',
           'Basic demo quiz for local testing.',
           now() - interval '1 day',
           now() + interval '7 days',
           'PUBLISHED'
       );

INSERT INTO questions (id, quiz_id, text, points, type, position)
VALUES
    (1, 1, 'Which keyword is used to create a class in Java?', 5, 'SINGLE_CHOICE', 1),
    (2, 1, 'Which of the following are valid Java access modifiers?', 10, 'MULTIPLE_CHOICE', 2);

INSERT INTO answer_options (id, question_id, text, is_correct, position)
VALUES
    (1, 1, 'class', true, 1),
    (2, 1, 'function', false, 2),
    (3, 1, 'define', false, 3),

    (4, 2, 'public', true, 1),
    (5, 2, 'private', true, 2),
    (6, 2, 'protected', true, 3),
    (7, 2, 'internal', false, 4);

SELECT setval('app_users_id_seq', (SELECT MAX(id) FROM app_users));
SELECT setval('quizzes_id_seq', (SELECT MAX(id) FROM quizzes));
SELECT setval('questions_id_seq', (SELECT MAX(id) FROM questions));
SELECT setval('answer_options_id_seq', (SELECT MAX(id) FROM answer_options));