import { useState } from 'react'
import { TeacherCreateQuizForm } from './components/TeacherCreateQuizForm'
import './App.css'
import {
  deleteDraftQuiz,
  getAvailableQuizzes,
  getQuizResult,
  getTeacherQuizzes,
  openQuiz,
  publishQuiz,
  submitQuiz
} from './api/quizApi'
import type {
  CurrentUser,
  QuizResponse,
  QuizResultResponse,
  QuizSubmitResponse,
  QuizSummaryResponse,
  TeacherQuizSummaryResponse
} from './types/quiz'

const demoUsers: CurrentUser[] = [
  {
    id: 1,
    role: 'TEACHER',
    label: 'Nastavnik - Marko Marković'
  },
  {
    id: 2,
    role: 'STUDENT',
    label: 'Student - Jovan Jovanović'
  },
  {
    id: 3,
    role: 'STUDENT',
    label: 'Student - Ana Anić'
  }
]

function formatDate(value: string) {
  return new Date(value).toLocaleString('sr-RS')
}

function formatScore(value: number) {
  return Number(value).toFixed(2)
}

function formatQuestionType(type: string) {
  if (type === 'SINGLE_CHOICE') {
    return 'Jedan tačan odgovor'
  }

  if (type === 'MULTIPLE_CHOICE') {
    return 'Više tačnih odgovora'
  }

  return type
}

function formatQuizStatus(status: string) {
  if (status === 'DRAFT') {
    return 'Nacrt'
  }

  if (status === 'PUBLISHED') {
    return 'Objavljen'
  }

  return status
}

function formatUserRole(role: string) {
  if (role === 'TEACHER') {
    return 'Nastavnik'
  }

  if (role === 'STUDENT') {
    return 'Student'
  }

  return role
}

function App() {
  const [currentUser, setCurrentUser] = useState<CurrentUser>(demoUsers[0])

  const [teacherQuizzes, setTeacherQuizzes] = useState<TeacherQuizSummaryResponse[]>([])
  const [availableQuizzes, setAvailableQuizzes] = useState<QuizSummaryResponse[]>([])
  const [openedQuiz, setOpenedQuiz] = useState<QuizResponse | null>(null)

  const [selectedOptionsByQuestionId, setSelectedOptionsByQuestionId] = useState<Record<number, number[]>>({})
  const [submitResult, setSubmitResult] = useState<QuizSubmitResponse | null>(null)
  const [savedResult, setSavedResult] = useState<QuizResultResponse | null>(null)

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function handleLoadTeacherQuizzes() {
    setLoading(true)
    setError(null)

    try {
      const result = await getTeacherQuizzes(currentUser)
      setTeacherQuizzes(result)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Greška pri učitavanju kvizova')
    } finally {
      setLoading(false)
    }
  }

  async function handlePublishQuiz(quizId: number) {
    setLoading(true)
    setError(null)

    try {
      await publishQuiz(currentUser, quizId)

      const result = await getTeacherQuizzes(currentUser)
      setTeacherQuizzes(result)
    } catch (err) {
      const publishError = err instanceof Error
          ? err.message
          : 'Greška pri objavljivanju kviza'

      try {
        await deleteDraftQuiz(currentUser, quizId)

        const result = await getTeacherQuizzes(currentUser)
        setTeacherQuizzes(result)

        setError(
            `Kviz nije mogao da se objavi, pa je nacrt automatski uklonjen.\nRazlog: ${publishError}`
        )
      } catch (deleteErr) {
        const deleteError = deleteErr instanceof Error
            ? deleteErr.message
            : 'Nacrt nije mogao automatski da se ukloni.'

        setError(`${publishError}\n${deleteError}`)
      }
    } finally {
      setLoading(false)
    }
  }

  async function handleLoadAvailableQuizzes() {
    setLoading(true)
    setError(null)
    setOpenedQuiz(null)
    setSelectedOptionsByQuestionId({})
    setSubmitResult(null)
    setSavedResult(null)

    try {
      const result = await getAvailableQuizzes(currentUser)
      setAvailableQuizzes(result)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Greška pri učitavanju dostupnih kvizova')
    } finally {
      setLoading(false)
    }
  }

  async function handleOpenQuiz(quizId: number) {
    setLoading(true)
    setError(null)
    setSelectedOptionsByQuestionId({})
    setSubmitResult(null)
    setSavedResult(null)

    try {
      const result = await openQuiz(currentUser, quizId)
      setOpenedQuiz(result)

      try {
        const existingResult = await getQuizResult(currentUser, quizId)
        setSavedResult(existingResult)
      } catch {
        // Student još nije predao ovaj kviz.
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Greška pri otvaranju kviza')
    } finally {
      setLoading(false)
    }
  }

  async function handleSubmitQuiz() {
    if (!openedQuiz) {
      return
    }

    setLoading(true)
    setError(null)
    setSubmitResult(null)
    setSavedResult(null)

    try {
      const request = {
        answers: openedQuiz.questions.map((question) => ({
          questionId: question.id,
          optionIds: selectedOptionsByQuestionId[question.id] || []
        }))
      }

      const result = await submitQuiz(currentUser, openedQuiz.id, request)
      setSubmitResult(result)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Greška pri predaji kviza')
    } finally {
      setLoading(false)
    }
  }

  async function handleLoadResult() {
    if (!openedQuiz) {
      return
    }

    setLoading(true)
    setError(null)
    setSavedResult(null)

    try {
      const result = await getQuizResult(currentUser, openedQuiz.id)
      setSavedResult(result)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Greška pri učitavanju rezultata')
    } finally {
      setLoading(false)
    }
  }

  function handleUserChange(userId: number) {
    const selectedUser = demoUsers.find((user) => user.id === userId)

    if (!selectedUser) {
      return
    }

    setCurrentUser(selectedUser)
    setTeacherQuizzes([])
    setAvailableQuizzes([])
    setOpenedQuiz(null)
    setSelectedOptionsByQuestionId({})
    setSubmitResult(null)
    setSavedResult(null)
    setError(null)
  }

  function handleSingleChoiceChange(questionId: number, optionId: number) {
    setSelectedOptionsByQuestionId((current) => ({
      ...current,
      [questionId]: [optionId]
    }))
  }

  function handleMultipleChoiceChange(questionId: number, optionId: number, checked: boolean) {
    setSelectedOptionsByQuestionId((current) => {
      const currentSelected = current[questionId] || []

      const nextSelected = checked
          ? [...currentSelected, optionId]
          : currentSelected.filter((id) => id !== optionId)

      return {
        ...current,
        [questionId]: nextSelected
      }
    })
  }

  return (
      <main className="app">
        <section className="hero">
          <p className="eyebrow">Metropolitan LMS</p>
          <h1>Quiz Module</h1>
          <p>
            Mini aplikacija za kreiranje, objavljivanje i rešavanje kvizova.
          </p>
        </section>

        <section className="panel">
          <label htmlFor="user-select">Demo korisnik</label>
          <select
              id="user-select"
              value={currentUser.id}
              onChange={(event) => handleUserChange(Number(event.target.value))}
          >
            {demoUsers.map((user) => (
                <option key={user.id} value={user.id}>
                  {user.label} ({formatUserRole(user.role)})
                </option>
            ))}
          </select>
        </section>

        {error && (
            <div className="error">
              {error}
            </div>
        )}

        {loading && (
            <div className="info">
              Učitavanje...
            </div>
        )}

        {currentUser.role === 'TEACHER' && (
            <>
              <TeacherCreateQuizForm
                  currentUser={currentUser}
                  onQuizCreated={handleLoadTeacherQuizzes}
              />

              <section className="panel">
                <div className="section-header">
                  <div>
                    <h2>Nastavnik</h2>
                    <p>Pregled kvizova i objavljivanje nacrta kvizova.</p>
                  </div>

                  <button onClick={handleLoadTeacherQuizzes}>
                    Učitaj moje kvizove
                  </button>
                </div>

                <div className="card-grid">
                  {teacherQuizzes.map((quiz) => (
                      <article key={quiz.id} className="card">
                        <div className="card-top">
                          <h3>{quiz.title}</h3>

                          <span className={`badge ${quiz.status.toLowerCase()}`}>
                      {formatQuizStatus(quiz.status)}
                    </span>
                        </div>

                        <p>{quiz.description || 'Nema opisa.'}</p>

                        <dl>
                          <div>
                            <dt>Otvara se</dt>
                            <dd>{formatDate(quiz.opensAt)}</dd>
                          </div>

                          <div>
                            <dt>Zatvara se</dt>
                            <dd>{formatDate(quiz.closesAt)}</dd>
                          </div>

                          <div>
                            <dt>Broj pitanja</dt>
                            <dd>{quiz.questionCount}</dd>
                          </div>
                        </dl>

                        {quiz.status === 'DRAFT' && (
                            <button onClick={() => handlePublishQuiz(quiz.id)}>
                              Objavi kviz
                            </button>
                        )}
                      </article>
                  ))}
                </div>
              </section>
            </>
        )}

        {currentUser.role === 'STUDENT' && (
            <section className="panel">
              <div className="section-header">
                <div>
                  <h2>Student</h2>
                  <p>Dostupni kvizovi za trenutno izabranog studenta.</p>
                </div>

                <button onClick={handleLoadAvailableQuizzes}>
                  Učitaj dostupne kvizove
                </button>
              </div>

              <div className="card-grid">
                {availableQuizzes.map((quiz) => (
                    <article key={quiz.id} className="card">
                      <h3>{quiz.title}</h3>
                      <p>{quiz.description || 'Nema opisa.'}</p>

                      <dl>
                        <div>
                          <dt>Otvara se</dt>
                          <dd>{formatDate(quiz.opensAt)}</dd>
                        </div>

                        <div>
                          <dt>Zatvara se</dt>
                          <dd>{formatDate(quiz.closesAt)}</dd>
                        </div>
                      </dl>

                      <button onClick={() => handleOpenQuiz(quiz.id)}>
                        Otvori kviz
                      </button>
                    </article>
                ))}
              </div>

              {openedQuiz && (
                  <section className="opened-quiz">
                    <div className="opened-quiz-header">
                      <div>
                        <h2>{openedQuiz.title}</h2>
                        <p>{openedQuiz.description}</p>
                      </div>

                      {!submitResult && !savedResult && (
                          <button onClick={handleLoadResult}>
                            Prikaži moj rezultat
                          </button>
                      )}
                    </div>

                    {submitResult ? (
                        <div className="result-box">
                          <h3>Kviz je uspešno predat</h3>
                          <p>
                            Rezultat: <strong>{formatScore(submitResult.score)}</strong> /{' '}
                            <strong>{formatScore(submitResult.maxScore)}</strong>
                          </p>
                          <p>Predato: {formatDate(submitResult.submittedAt)}</p>
                        </div>
                    ) : savedResult ? (
                        <>
                          <div className="info">
                            Već ste predali ovaj kviz. Ponovna predaja nije dozvoljena.
                          </div>

                          <div className="result-box">
                            <h3>Sačuvan rezultat</h3>
                            <p>
                              Rezultat: <strong>{formatScore(savedResult.score)}</strong> /{' '}
                              <strong>{formatScore(savedResult.maxScore)}</strong>
                            </p>
                            <p>Predato: {formatDate(savedResult.submittedAt)}</p>
                          </div>
                        </>
                    ) : (
                        <>
                          {openedQuiz.questions.map((question) => (
                              <article key={question.id} className="question-card">
                                <h3>
                                  {question.position}. {question.text}
                                </h3>

                                <p>
                                  Tip: <strong>{formatQuestionType(question.type)}</strong> | Poeni:{' '}
                                  <strong>{question.points}</strong>
                                </p>

                                <div className="options-list">
                                  {question.options.map((option) => {
                                    const selected = selectedOptionsByQuestionId[question.id] || []

                                    if (question.type === 'SINGLE_CHOICE') {
                                      return (
                                          <label key={option.id} className="option-row">
                                            <input
                                                type="radio"
                                                name={`question-${question.id}`}
                                                checked={selected.includes(option.id)}
                                                onChange={() => handleSingleChoiceChange(question.id, option.id)}
                                            />
                                            <span>{option.text}</span>
                                          </label>
                                      )
                                    }

                                    return (
                                        <label key={option.id} className="option-row">
                                          <input
                                              type="checkbox"
                                              checked={selected.includes(option.id)}
                                              onChange={(event) =>
                                                  handleMultipleChoiceChange(
                                                      question.id,
                                                      option.id,
                                                      event.target.checked
                                                  )
                                              }
                                          />
                                          <span>{option.text}</span>
                                        </label>
                                    )
                                  })}
                                </div>
                              </article>
                          ))}

                          <div className="quiz-actions">
                            <button onClick={handleSubmitQuiz}>
                              Predaj kviz
                            </button>
                          </div>
                        </>
                    )}
                  </section>
              )}
            </section>
        )}
      </main>
  )
}

export default App