import {ErrorHandler, Injectable, Injector} from "@angular/core";
import {MatSnackBar} from "@angular/material/snack-bar";
import {HttpErrorResponse} from "@angular/common/http";
import {ErrorService} from "./errorservice.service";
import {LoggingService} from "./logging.service";

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {

  constructor(private injector: Injector, private snackBar: MatSnackBar) { }

  handleError(error: Error | HttpErrorResponse) {
    let message;
    let stackTrace;
    const errorService = this.injector.get(ErrorService);
    const logger = this.injector.get(LoggingService);
    if (error instanceof HttpErrorResponse) {
      message = errorService.getServerMessage(error);
      stackTrace = errorService.getServerStack(error);
    } else {
      message = errorService.getClientMessage(error);
      stackTrace = errorService.getClientStack(error);
    }
    this.snackBar.open("An error occurred, please try again later", 'x', {panelClass: ['error']});
    logger.logError(message, stackTrace);
  }
}
