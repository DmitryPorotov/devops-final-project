import {Body, Controller, Get, Param, Post, UseGuards} from '@nestjs/common';
import {Roles} from "../../auth/roles.decorator";
import {AuthGuard} from "../../auth/auth.guard";
import {GamesService} from "./games.service";

@Roles('admin')
@UseGuards(AuthGuard)
@Controller('admin/games')
export class GamesController {
    constructor(private readonly gamesService: GamesService) {
    }
    @Get(':id')
    async state(@Param('id') id: string) {
        return await this.gamesService.getState(id);
    }
    @Post(':id')
    async save(@Param('id') id: string, @Body() {name}: {name: string}) {
        return await this.gamesService.saveGame(id, name);
    }
}
