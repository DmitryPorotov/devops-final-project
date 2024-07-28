export default interface User {
    id: number

    email?: string

    name: string

    token?: string

    house?: string

    ping?: number

    connected?: boolean
}