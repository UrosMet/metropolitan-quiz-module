import type {
    CreateQuizRequest,
    CurrentUser,
    QuizResponse,
    QuizResultResponse,
    QuizSubmitResponse,
    QuizSummaryResponse,
    SubmitQuizRequest,
    TeacherQuizSummaryResponse
} from '../types/quiz'

function translateApiError(message: string) {
    if (message === 'Quiz cannot be published') {
        return 'Kviz nije moguće objaviti.'
    }

    if (message === 'opensAt must be before closesAt') {
        return 'Vreme otvaranja mora biti pre vremena zatvaranja.'
    }

    if (message === 'Quiz must contain at least one question') {
        return 'Kviz mora imati najmanje jedno pitanje.'
    }

    if (message.includes('must contain at least two options')) {
        return message.replace(
            /Question (\d+) must contain at least two options/,
            'Pitanje $1 mora imati najmanje dve opcije.'
        )
    }

    if (message.includes('must have at least one correct option')) {
        return message.replace(
            /Question (\d+) must have at least one correct option/,
            'Pitanje $1 mora imati najmanje jedan tačan odgovor.'
        )
    }

    if (message.includes('of type SINGLE_CHOICE must have exactly one correct option')) {
        return message.replace(
            /Question (\d+) of type SINGLE_CHOICE must have exactly one correct option/,
            'Pitanje $1 tipa "Jedan tačan odgovor" mora imati tačno jednu tačnu opciju.'
        )
    }

    if (message === 'Student has already submitted this quiz') {
        return 'Već ste predali ovaj kviz. Ponovna predaja nije dozvoljena.'
    }

    if (message === 'Quiz is not open yet') {
        return 'Kviz još uvek nije otvoren.'
    }

    if (message === 'Quiz is already closed') {
        return 'Kviz je zatvoren.'
    }

    if (message === 'Quiz is not available') {
        return 'Kviz nije dostupan.'
    }

    if (message === 'Quiz not found') {
        return 'Kviz nije pronađen.'
    }

    if (message === 'Result not found for this student and quiz') {
        return 'Rezultat za ovaj kviz nije pronađen.'
    }

    if (message === 'Only draft quizzes can be deleted') {
        return 'Samo nacrti kvizova mogu biti obrisani.'
    }

    if (message === 'You can delete only your own quizzes') {
        return 'Možete obrisati samo svoje kvizove.'
    }

    return message
}

function backendNotRunningMessage() {
    return 'Backend server nije pokrenut. Pokreni backend aplikaciju na portu 8080.'
}

async function parseResponseBody(response: Response) {
    const text = await response.text()

    if (!text) {
        return null
    }

    try {
        return JSON.parse(text)
    } catch {
        return {
            message: text
        }
    }
}

async function apiRequest<T>(
    path: string,
    user: CurrentUser,
    options: RequestInit = {}
): Promise<T> {
    const headers = new Headers(options.headers)

    headers.set('X-User-Id', String(user.id))
    headers.set('X-User-Role', user.role)

    if (options.body && !headers.has('Content-Type')) {
        headers.set('Content-Type', 'application/json')
    }

    let response: Response

    try {
        response = await fetch(path, {
            ...options,
            headers
        })
    } catch {
        throw new Error(backendNotRunningMessage())
    }

    const data = await parseResponseBody(response)

    if (!response.ok) {
        if (
            response.status === 502 ||
            response.status === 503 ||
            response.status === 504
        ) {
            throw new Error(backendNotRunningMessage())
        }

        const translatedErrors = Array.isArray(data?.errors)
            ? data.errors.map((error: string) => translateApiError(error))
            : []

        if (translatedErrors.length > 0) {
            throw new Error(translatedErrors.join('\n'))
        }

        const message = data?.message
            ? translateApiError(data.message)
            : `Zahtev nije uspeo. Status: ${response.status}`

        throw new Error(message)
    }

    return data as T
}

export function createQuiz(user: CurrentUser, request: CreateQuizRequest) {
    return apiRequest<QuizResponse>(
        '/api/teacher/quizzes',
        user,
        {
            method: 'POST',
            body: JSON.stringify(request)
        }
    )
}

export function getTeacherQuizzes(user: CurrentUser) {
    return apiRequest<TeacherQuizSummaryResponse[]>(
        '/api/teacher/quizzes',
        user
    )
}

export function publishQuiz(user: CurrentUser, quizId: number) {
    return apiRequest<QuizResponse>(
        `/api/teacher/quizzes/${quizId}/publish`,
        user,
        {
            method: 'POST'
        }
    )
}

export function deleteDraftQuiz(user: CurrentUser, quizId: number) {
    return apiRequest<void>(
        `/api/teacher/quizzes/${quizId}`,
        user,
        {
            method: 'DELETE'
        }
    )
}

export function getAvailableQuizzes(user: CurrentUser) {
    return apiRequest<QuizSummaryResponse[]>(
        '/api/student/quizzes/available',
        user
    )
}

export function openQuiz(user: CurrentUser, quizId: number) {
    return apiRequest<QuizResponse>(
        `/api/student/quizzes/${quizId}`,
        user
    )
}

export function submitQuiz(
    user: CurrentUser,
    quizId: number,
    request: SubmitQuizRequest
) {
    return apiRequest<QuizSubmitResponse>(
        `/api/student/quizzes/${quizId}/submit`,
        user,
        {
            method: 'POST',
            body: JSON.stringify(request)
        }
    )
}

export function getQuizResult(user: CurrentUser, quizId: number) {
    return apiRequest<QuizResultResponse>(
        `/api/student/quizzes/${quizId}/result`,
        user
    )
}