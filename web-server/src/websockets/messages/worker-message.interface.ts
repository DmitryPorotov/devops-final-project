export interface WorkerMessageInterface {
    gameId: string
    userId?: number
    type: "action"
    action: string
    time?: string
    message?: string
    status?: {created: boolean, details: unknown}
    reply?: Array<{
        to: '*' | number,
        player_action: unknown
    }>
}
