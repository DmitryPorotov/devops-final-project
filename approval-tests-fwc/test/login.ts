import {LoginUserDto, send} from "./utility";

export async function login({email, password}, port = null): Promise<LoginUserDto> {
    return await send('/auth/login', JSON.stringify({
        email,
        password,
    }), null, 'POST', port) as LoginUserDto
}
