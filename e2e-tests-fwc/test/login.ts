import {LoginUserDto, send} from "./utility";

export async function login({email, password}): Promise<LoginUserDto> {
    return await send('/auth/login', JSON.stringify({
        email,
        password,
    })) as LoginUserDto
}
