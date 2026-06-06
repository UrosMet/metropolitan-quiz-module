export type UserRole = 'TEACHER' | 'STUDENT'

export type QuizStatus = 'DRAFT' | 'PUBLISHED'

export type QuestionType = 'SINGLE_CHOICE' | 'MULTIPLE_CHOICE'

export type CurrentUser = {
    id: number
    role: UserRole
    label: string
}

export type TeacherQuizSummaryResponse = {
    id: number
    title: string
    description: string | null
    opensAt: string
    closesAt: string
    status: QuizStatus
    questionCount: number
}

export type QuizSummaryResponse = {
    id: number
    title: string
    description: string | null
    opensAt: string
    closesAt: string
}

export type AnswerOptionResponse = {
    id: number
    text: string
    position: number
}

export type QuestionResponse = {
    id: number
    text: string
    points: number
    type: QuestionType
    position: number
    options: AnswerOptionResponse[]
}

export type QuizResponse = {
    id: number
    title: string
    description: string | null
    opensAt: string
    closesAt: string
    status: QuizStatus
    questions: QuestionResponse[]
}

export type SubmitQuestionAnswerRequest = {
    questionId: number
    optionIds: number[]
}

export type SubmitQuizRequest = {
    answers: SubmitQuestionAnswerRequest[]
}

export type QuizSubmitResponse = {
    quizId: number
    studentId: number
    score: number
    maxScore: number
    submittedAt: string
}

export type QuizResultResponse = {
    quizId: number
    quizTitle: string
    studentId: number
    score: number
    maxScore: number
    submittedAt: string
}
export type CreateAnswerOptionRequest = {
    text: string
    correct: boolean
}

export type CreateQuestionRequest = {
    text: string
    points: number
    type: QuestionType
    options: CreateAnswerOptionRequest[]
}

export type CreateQuizRequest = {
    title: string
    description: string
    opensAt: string
    closesAt: string
    questions: CreateQuestionRequest[]
}
