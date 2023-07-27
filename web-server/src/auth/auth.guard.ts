import {CanActivate, ExecutionContext, Injectable, UnauthorizedException} from '@nestjs/common';
import {Reflector} from "@nestjs/core";
import {FastifyRequest} from 'fastify';
import {JwtService} from "@nestjs/jwt";
import constants from "../constants";

@Injectable()
export class AuthGuard implements CanActivate {
    constructor(private reflector: Reflector, private jwtService: JwtService) {}

    async canActivate(context: ExecutionContext): Promise<boolean> {
        const roles = this.reflector.get<string[]>('roles', context.getHandler());
        if (!roles) {
            return true;
        }
        const request = context.switchToHttp().getRequest();
        const token = AuthGuard.extractTokenFromHeader(request);
        if (!token) {
            throw new UnauthorizedException();
        }
        try {
            request.user = await this.jwtService.verifyAsync(
                token,
                {
                    secret: constants.JWT_SECRET
                }
            );
        } catch {
            throw new UnauthorizedException();
        }

        const rv = !(
            !request.user.isEnabled
            || (!request.user.isAdmin && roles.includes('admin'))
        );
        return rv;

    }

    private static extractTokenFromHeader(request: FastifyRequest): string | undefined {
        const [type, token] = request.headers.authorization?.split(' ') ?? [];
        return type === 'Bearer' ? token : undefined;
    }
}
