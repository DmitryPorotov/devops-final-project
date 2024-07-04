export default interface FetchOptionsInterface {
    url: string
    getBody: () => string
    onSuccess: (result: any) => void
    onFailure?: (messages: {[k in ValidationErrorFields]:string[]}) => void
    getEmailAndPassword?: () => {email: string, password: string}
}

export type ValidationErrorFields = 'name' | 'email' | 'password'