import { ChatActionsEnum } from './chat-actions.enum';

export interface ChatMessageInterface {
  type: keyof ChatActionsEnum;
  to?: Array<number>;
  body?: string | { id: number; name: string };
  lobbyName?: string;
  lobbyPassword?: string;
  deletePassword?: boolean;
}
