import {CanActivate, ExecutionContext, ForbiddenException, Injectable, UnauthorizedException} from '@nestjs/common';
import {Reflector} from "@nestjs/core";
import {FastifyRequest} from 'fastify';
import {JwtService} from "@nestjs/jwt";
import constants from "../constants";
import {LoginUserDto} from "../user/dto/login-user.dto";

@Injectable()
export class AuthGuard implements CanActivate {
    constructor(private reflector: Reflector, private jwtService: JwtService) {}

    async canActivate(context: ExecutionContext): Promise<boolean> {
        const actionRoles = this.reflector.get<string[]>('roles', context.getHandler());
        const controllerRoles = this.reflector.get<string[]>('roles', context.getClass());
        const rolesToUse = actionRoles?.length ? actionRoles : controllerRoles;
        if (!rolesToUse) {
            return true;
        }
        const request = context.switchToHttp().getRequest();
        const token = AuthGuard.extractToken(request);
        if (!token) {
            throw new UnauthorizedException();
        }
        request.user = await this.getUser(token);

        if (!request.user.isEnabled) {
            throw new ForbiddenException('This account is not validated by the admin. Please contact the admin for validation.')
        }

        return request.user.isAdmin || !rolesToUse.includes('admin');
    }

    async getUser(token: string): Promise<LoginUserDto | null> {
        try {
            return await this.jwtService.verifyAsync(
                token,
                {
                    secret: constants.JWT_SECRET
                }
            );
        } catch {
            throw new UnauthorizedException();
        }
    }

    private static extractToken(request: FastifyRequest): string | undefined {
        const [type, token] = request.headers.authorization?.split(' ') ?? [];
        if (type === 'Bearer') return token;
        else return request.query['_token'];
    }
}
