export interface WorkerMessageInterface {
    gameId: string
    userId?: number
    type: "action",
    action: string,
    status?: {created: boolean, details: unknown},
    reply?: Array<{
        to: '*' | number,
        player_action: unknown
    }>
}
