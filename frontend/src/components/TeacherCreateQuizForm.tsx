import { useState } from 'react'
import { createQuiz } from '../api/quizApi'
import type {
    CreateAnswerOptionRequest,
    CreateQuestionRequest,
    CreateQuizRequest,
    CurrentUser,
    QuestionType
} from '../types/quiz'

type Props = {
    currentUser: CurrentUser
    onQuizCreated: () => void
}

function toDateTimeLocal(date: Date) {
    const offset = date.getTimezoneOffset()
    const localDate = new Date(date.getTime() - offset * 60 * 1000)
    return localDate.toISOString().slice(0, 16)
}

function createEmptyOption(): CreateAnswerOptionRequest {
    return {
        text: '',
        correct: false
    }
}

function createEmptyQuestion(): CreateQuestionRequest {
    return {
        text: '',
        points: 5,
        type: 'SINGLE_CHOICE',
        options: [
            {
                text: '',
                correct: true
            },
            {
                text: '',
                correct: false
            }
        ]
    }
}

export function TeacherCreateQuizForm({ currentUser, onQuizCreated }: Props) {
    const [title, setTitle] = useState('')
    const [description, setDescription] = useState('')
    const [opensAt, setOpensAt] = useState(toDateTimeLocal(new Date()))
    const [closesAt, setClosesAt] = useState(
        toDateTimeLocal(new Date(Date.now() + 24 * 60 * 60 * 1000))
    )
    const [questions, setQuestions] = useState<CreateQuestionRequest[]>([
        createEmptyQuestion()
    ])

    const [loading, setLoading] = useState(false)
    const [message, setMessage] = useState<string | null>(null)
    const [error, setError] = useState<string | null>(null)

    function updateQuestion(index: number, updatedQuestion: CreateQuestionRequest) {
        setQuestions((current) =>
            current.map((question, questionIndex) =>
                questionIndex === index ? updatedQuestion : question
            )
        )
    }

    function updateQuestionType(index: number, type: QuestionType) {
        const question = questions[index]

        const options = question.options.map((option, optionIndex) => ({
            ...option,
            correct: type === 'SINGLE_CHOICE' ? optionIndex === 0 : option.correct
        }))

        updateQuestion(index, {
            ...question,
            type,
            options
        })
    }

    function updateOption(
        questionIndex: number,
        optionIndex: number,
        updatedOption: CreateAnswerOptionRequest
    ) {
        const question = questions[questionIndex]

        const nextOptions = question.options.map((option, currentOptionIndex) =>
            currentOptionIndex === optionIndex ? updatedOption : option
        )

        updateQuestion(questionIndex, {
            ...question,
            options: nextOptions
        })
    }

    function markSingleChoiceCorrect(questionIndex: number, optionIndex: number) {
        const question = questions[questionIndex]

        const nextOptions = question.options.map((option, currentOptionIndex) => ({
            ...option,
            correct: currentOptionIndex === optionIndex
        }))

        updateQuestion(questionIndex, {
            ...question,
            options: nextOptions
        })
    }

    function addQuestion() {
        setQuestions((current) => [...current, createEmptyQuestion()])
    }

    function removeQuestion(index: number) {
        setQuestions((current) => current.filter((_, questionIndex) => questionIndex !== index))
    }

    function addOption(questionIndex: number) {
        const question = questions[questionIndex]

        updateQuestion(questionIndex, {
            ...question,
            options: [...question.options, createEmptyOption()]
        })
    }

    function removeOption(questionIndex: number, optionIndex: number) {
        const question = questions[questionIndex]

        if (question.options.length <= 2) {
            setError('Pitanje mora imati najmanje dve opcije.')
            return
        }

        updateQuestion(questionIndex, {
            ...question,
            options: question.options.filter((_, currentOptionIndex) => currentOptionIndex !== optionIndex)
        })
    }

    function resetForm() {
        setTitle('')
        setDescription('')
        setOpensAt(toDateTimeLocal(new Date()))
        setClosesAt(toDateTimeLocal(new Date(Date.now() + 24 * 60 * 60 * 1000)))
        setQuestions([createEmptyQuestion()])
    }

    async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault()

        setLoading(true)
        setError(null)
        setMessage(null)

        try {
            const request: CreateQuizRequest = {
                title,
                description,
                opensAt: new Date(opensAt).toISOString(),
                closesAt: new Date(closesAt).toISOString(),
                questions
            }

            const createdQuiz = await createQuiz(currentUser, request)

            setMessage(`Kviz "${createdQuiz.title}" je uspešno kreiran kao DRAFT.`)
            resetForm()
            onQuizCreated()
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Greška pri kreiranju kviza.')
        } finally {
            setLoading(false)
        }
    }

    return (
        <section className="panel create-quiz-panel">
            <div className="section-header">
                <div>
                    <h2>Kreiranje kviza</h2>
                    <p>Unesite osnovne podatke, pitanja i ponuđene odgovore.</p>
                </div>
            </div>

            {message && <div className="success">{message}</div>}
            {error && <div className="error">{error}</div>}

            <form className="quiz-form" onSubmit={handleSubmit}>
                <div className="form-grid">
                    <label>
                        Naslov kviza
                        <input
                            value={title}
                            onChange={(event) => setTitle(event.target.value)}
                            required
                            maxLength={200}
                            placeholder="Na primer: Osnove Spring Boot-a"
                        />
                    </label>

                    <label>
                        Opis kviza
                        <textarea
                            value={description}
                            onChange={(event) => setDescription(event.target.value)}
                            rows={3}
                            placeholder="Kratak opis kviza"
                        />
                    </label>

                    <label>
                        Otvara se
                        <input
                            type="datetime-local"
                            value={opensAt}
                            onChange={(event) => setOpensAt(event.target.value)}
                            required
                        />
                    </label>

                    <label>
                        Zatvara se
                        <input
                            type="datetime-local"
                            value={closesAt}
                            onChange={(event) => setClosesAt(event.target.value)}
                            required
                        />
                    </label>
                </div>

                <div className="questions-editor">
                    <div className="editor-header">
                        <h3>Pitanja</h3>
                        <button type="button" onClick={addQuestion}>
                            Dodaj pitanje
                        </button>
                    </div>

                    {questions.map((question, questionIndex) => (
                        <article key={questionIndex} className="question-editor-card">
                            <div className="editor-header">
                                <h4>Pitanje {questionIndex + 1}</h4>

                                {questions.length > 1 && (
                                    <button
                                        type="button"
                                        className="secondary-button"
                                        onClick={() => removeQuestion(questionIndex)}
                                    >
                                        Ukloni pitanje
                                    </button>
                                )}
                            </div>

                            <div className="form-grid">
                                <label>
                                    Tekst pitanja
                                    <textarea
                                        value={question.text}
                                        onChange={(event) =>
                                            updateQuestion(questionIndex, {
                                                ...question,
                                                text: event.target.value
                                            })
                                        }
                                        rows={2}
                                        required
                                        placeholder="Unesite tekst pitanja"
                                    />
                                </label>

                                <label>
                                    Broj poena
                                    <input
                                        type="number"
                                        min={1}
                                        value={question.points}
                                        onChange={(event) =>
                                            updateQuestion(questionIndex, {
                                                ...question,
                                                points: Number(event.target.value)
                                            })
                                        }
                                        required
                                    />
                                </label>

                                <label>
                                    Tip pitanja
                                    <select
                                        value={question.type}
                                        onChange={(event) =>
                                            updateQuestionType(questionIndex, event.target.value as QuestionType)
                                        }
                                    >
                                        <option value="SINGLE_CHOICE">Jedan tačan odgovor</option>
                                        <option value="MULTIPLE_CHOICE">Više tačnih odgovora</option>
                                    </select>
                                </label>
                            </div>

                            <div className="options-editor">
                                <div className="editor-header">
                                    <h4>Opcije</h4>
                                    <button type="button" onClick={() => addOption(questionIndex)}>
                                        Dodaj opciju
                                    </button>
                                </div>

                                {question.options.map((option, optionIndex) => (
                                    <div key={optionIndex} className="option-editor-row">
                                        <input
                                            value={option.text}
                                            onChange={(event) =>
                                                updateOption(questionIndex, optionIndex, {
                                                    ...option,
                                                    text: event.target.value
                                                })
                                            }
                                            required
                                            placeholder={`Opcija ${optionIndex + 1}`}
                                        />

                                        {question.type === 'SINGLE_CHOICE' ? (
                                            <label className="correct-toggle">
                                                <input
                                                    type="radio"
                                                    name={`correct-${questionIndex}`}
                                                    checked={option.correct}
                                                    onChange={() => markSingleChoiceCorrect(questionIndex, optionIndex)}
                                                />
                                                Tačno
                                            </label>
                                        ) : (
                                            <label className="correct-toggle">
                                                <input
                                                    type="checkbox"
                                                    checked={option.correct}
                                                    onChange={(event) =>
                                                        updateOption(questionIndex, optionIndex, {
                                                            ...option,
                                                            correct: event.target.checked
                                                        })
                                                    }
                                                />
                                                Tačno
                                            </label>
                                        )}

                                        <button
                                            type="button"
                                            className="secondary-button"
                                            onClick={() => removeOption(questionIndex, optionIndex)}
                                        >
                                            Ukloni
                                        </button>
                                    </div>
                                ))}
                            </div>
                        </article>
                    ))}
                </div>

                <div className="quiz-actions">
                    <button type="submit" disabled={loading}>
                        {loading ? 'Kreiranje...' : 'Kreiraj kviz'}
                    </button>
                </div>
            </form>
        </section>
    )
}