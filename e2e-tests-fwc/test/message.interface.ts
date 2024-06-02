export interface MessageInterface {
    userId?: number
    action?: string
    messageId: string
    time?: string
    type: 'chat' | 'action' | 'test' | 'error' | 'system';
    lobbyId?: number
    name?: string
}

export interface ChatMessageInterface extends MessageInterface {
    body?: ChatBodyInterface
    type: 'chat'
}

export interface ChatBodyInterface {
    type: keyof ChatActionsEnum
    to?: Array<number>
    body?: string | { id: number, name: string }
    lobbyName?: string
    lobbyPassword?: string
    deletePassword?: boolean
}

export interface ChatActionsEnum {
    create,
    join,
    leave,
    kick,
    message,
    error,
    edit,
    ping
}

export interface WorkerMessageInterface extends MessageInterface {
    gameId?: string
    userId?: number
    type: "action"
    action: keyof WorkerActions
    joinAs?: string
    isRandomHouses?: boolean
    time?: string
    
    status?: {created: boolean, details: unknown}
    reply?: Array<{
        to: '*' | number,
        player_action: unknown
    }>,
    gameSettings?: unknown,
    player_action?: unknown,
}

export interface WorkerActions {
    game_action,
    hello,
    save,
    list_saves,
    load,
    new_game,
    get_status,
    join_game,
    get_game_state,
    create_game,
    start_game,
}

export interface ErrorMessageInterface extends MessageInterface {
    type: 'error'
    message: string
}