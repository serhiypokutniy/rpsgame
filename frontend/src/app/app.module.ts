import {ErrorHandler, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import {GameComponent} from './components/game.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatCardModule} from "@angular/material/card";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {CookieService} from 'ngx-cookie-service';
import { HttpClientModule } from '@angular/common/http';
import {MatDividerModule} from "@angular/material/divider";
import {MatFormFieldModule} from "@angular/material/form-field";
import {FormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {GlobalErrorHandler} from './services/globalerrorhandler.service'
import {MatTooltipModule} from "@angular/material/tooltip";

@NgModule({
  declarations: [
    GameComponent,
  ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatToolbarModule,
        MatProgressSpinnerModule,
        MatIconModule,
        MatButtonModule,
        MatSnackBarModule,
        MatInputModule,
        MatDividerModule,
        MatFormFieldModule,
        FormsModule,
        MatTooltipModule
    ],
  providers: [CookieService, {provide: ErrorHandler, useClass: GlobalErrorHandler}],
  bootstrap: [GameComponent]
})
export class AppModule { }
