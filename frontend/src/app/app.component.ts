import {Component} from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import {Title} from "@angular/platform-browser";
import {CookieService} from 'ngx-cookie-service';
import {v4 as uuidv4} from 'uuid';
import {Game} from "./game.model";
import {animate, state, style, transition, trigger} from "@angular/animations";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  animations: [
      trigger('fadeSlideInOut', [
          transition(':enter', [
            style({ opacity: 0, transform: 'translateY(10px)' }),
            animate('1000ms', style({ opacity: 1, transform: 'translateY(0)' })),
          ]),
          transition(':leave', [
            animate('1000ms', style({ opacity: 0, transform: 'translateY(10px)' })),
          ]),
      ]),
      trigger('changeColorAnimation', [
          state('DEFAULT', style({ color: '#3f51b5' })),
          state('ACTIVE', style({ color: 'black' })),
          transition('* => *', animate('0.8s ease-in-out')),
    ])
  ]
})
export class AppComponent {

  constructor(private http: HttpClient, private cookie: CookieService, private titleService:Title) {
    this.titleService.setTitle("Rock, Paper, Scissors Game");
    this.init();
  }
  readonly BACKEND_PATH_PLAY = 'http://localhost:8080/api/play';
  readonly BACKEND_PATH_RESET = 'http://localhost:8080/api/reset';
  readonly BACKEND_PATH_INIT = 'http://localhost:8080/api/init';
  readonly PLAYER_ID_PARAM = 'playerId';
  readonly PLAYER_CHOICE_PARAM = 'playerChoice';
  readonly NEW = 'NEW';
  readonly WEAPONS = [
    'PEACE',
    'ROCK',
    'PAPER',
    'SCISSORS'
  ];
  readonly RESULTS = {
    'NEW': 'Select your weapon',
    'TIE': 'It is a tie, select your weapon for a new try',
    'PLAYER': 'You win! Select your weapon for another new try',
    'COMPUTER': 'You lose! Select your weapon for another try'
  }
  scores = {'computer' : 0, 'player' : 0}
  playerChoice = this.WEAPONS[0];
  computerChoice  = this.WEAPONS[0];
  winner = null;
  playedByAll = 0;
  playedByPlayer = 0;
  totalPlayers = 0;

  private init(): void {
    let sessionId = this.createSessionIdIfAbsent();
    let params = new HttpParams();
    params = params.set(this.PLAYER_ID_PARAM, sessionId);
    this.http.get<Game>(this.BACKEND_PATH_INIT, {params: params})
      .subscribe((response => {
        this.updateStatistics(response);
      }));
  }

  //Functions called by the player:

  play(weapon: string): void {
    let sessionId = this.createSessionIdIfAbsent();
    this.playerChoice = weapon;
    this.winner = null; //hide text for a new selection
    let params = new HttpParams();
    params = params.set(this.PLAYER_ID_PARAM, sessionId);
    params = params.set(this.PLAYER_CHOICE_PARAM, weapon);
    this.http.post<Game>(this.BACKEND_PATH_PLAY, null, {params: params})
      .subscribe((response => {
        this.winner = response.winner;
        this.computerChoice = response.computerChoice;
        this.updateStatistics(response);
    }));
  }

  reset(): void {
    let sessionId = this.createSessionIdIfAbsent();
    let params = new HttpParams();
    params = params.set(this.PLAYER_ID_PARAM, sessionId);
    this.http.post(this.BACKEND_PATH_RESET, null, {params: params})
      .subscribe(() => {
        this.scores.player = 0;
        this.scores.computer = 0;
      });
  }

  // Auxiliary functions:

  private createSessionIdIfAbsent(): string {
    let sessionId = this.cookie.get(this.PLAYER_ID_PARAM);
    if(!sessionId){
      sessionId = uuidv4();
      this.cookie.set(this.PLAYER_ID_PARAM, sessionId);
    }
    return sessionId;
  }

  private updateStatistics(response: Game): void {
    this.scores.player = response.player.lastWonScore;
    this.scores.computer = response.player.lastLostScore;
    this.playedByAll = response.player.playedByAll;
    this.playedByPlayer = response.player.timesPlayed;
    this.totalPlayers = response.player.distinctPlayers;
  }
}
