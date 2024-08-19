import {Controller, UseGuards} from '@nestjs/common';
import {Roles} from "../auth/roles.decorator";
import {AuthGuard} from "../auth/auth.guard";

@Roles('admin')
@UseGuards(AuthGuard)
@Controller('admin')
export class AdminController {

}
