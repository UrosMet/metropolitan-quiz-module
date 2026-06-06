CREATE TABLE app_users (
                           id BIGSERIAL PRIMARY KEY,
                           full_name VARCHAR(150) NOT NULL,
                           role VARCHAR(20) NOT NULL CHECK (role IN ('TEACHER', 'STUDENT')),
                           created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE quizzes (
                         id BIGSERIAL PRIMARY KEY,
                         teacher_id BIGINT NOT NULL REFERENCES app_users(id),
                         title VARCHAR(200) NOT NULL,
                         description TEXT,
                         opens_at TIMESTAMPTZ NOT NULL,
                         closes_at TIMESTAMPTZ NOT NULL,
                         status VARCHAR(20) NOT NULL CHECK (status IN ('DRAFT', 'PUBLISHED')),
                         created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                         updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE questions (
                           id BIGSERIAL PRIMARY KEY,
                           quiz_id BIGINT NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
                           text TEXT NOT NULL,
                           points INT NOT NULL CHECK (points > 0),
                           type VARCHAR(30) NOT NULL CHECK (type IN ('SINGLE_CHOICE', 'MULTIPLE_CHOICE')),
                           position INT NOT NULL
);

CREATE TABLE answer_options (
                                id BIGSERIAL PRIMARY KEY,
                                question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
                                text TEXT NOT NULL,
                                is_correct BOOLEAN NOT NULL DEFAULT false,
                                position INT NOT NULL
);

CREATE TABLE submissions (
                             id BIGSERIAL PRIMARY KEY,
                             quiz_id BIGINT NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
                             student_id BIGINT NOT NULL REFERENCES app_users(id),
                             score INT NOT NULL CHECK (score >= 0),
                             submitted_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                             CONSTRAINT uq_submission_quiz_student UNIQUE (quiz_id, student_id)
);

CREATE TABLE submission_answers (
                                    id BIGSERIAL PRIMARY KEY,
                                    submission_id BIGINT NOT NULL REFERENCES submissions(id) ON DELETE CASCADE,
                                    question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
                                    option_id BIGINT NOT NULL REFERENCES answer_options(id) ON DELETE CASCADE,

                                    CONSTRAINT uq_submission_answer UNIQUE (submission_id, question_id, option_id)
);

CREATE INDEX idx_quizzes_status_time
    ON quizzes(status, opens_at, closes_at);

CREATE INDEX idx_questions_quiz_id
    ON questions(quiz_id);

CREATE INDEX idx_answer_options_question_id
    ON answer_options(question_id);

CREATE INDEX idx_submissions_student_id
    ON submissions(student_id);