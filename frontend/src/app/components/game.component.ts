import {Component} from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import {Title} from "@angular/platform-browser";
import {CookieService} from 'ngx-cookie-service';
import {v4 as uuidv4} from 'uuid';
import {animate, state, style, transition, trigger} from "@angular/animations";
import type {Game} from '../../generated'
import {environment} from "../../environments/environment.prod";
import {constants} from "../constants";


@Component({
  selector: 'app-root',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.css'],
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
export class GameComponent {

  constructor(private http: HttpClient, private cookie: CookieService, private titleService:Title) {
    this.titleService.setTitle("Rock, Paper, Scissors Game");
    this.init();
  }
  readonly NEW = constants.NEW;
  readonly WEAPONS = constants.WEAPONS;
  readonly RESULTS = constants.RESULTS;
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
    params = params.set(constants.PLAYER_ID_PARAM, sessionId);
    this.http.get<Game>(environment.BACKEND_PATH_INIT, {params: params})
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

    params = params.set(constants.PLAYER_ID_PARAM, sessionId);
    params = params.set(constants.PLAYER_CHOICE_PARAM, weapon);
    this.http.post<Game>(environment.BACKEND_PATH_PLAY, null, {params: params})
      .subscribe((response => {
        this.winner = 'NEW';
        if(response.playerWins == undefined){
          this.winner = 'TIE';
        } else {
          this.winner = response.playerWins ? 'PLAYER' : 'COMPUTER';
        }
        this.computerChoice = response.computerChoice;
        this.updateStatistics(response);
    }));
  }

  reset(): void {
    let sessionId = this.createSessionIdIfAbsent();
    let params = new HttpParams();
    params = params.set(constants.PLAYER_ID_PARAM, sessionId);
    this.http.post(environment.BACKEND_PATH_RESET, null, {params: params})
      .subscribe(() => {
        this.scores.player = 0;
        this.scores.computer = 0;
      });
  }

  // Auxiliary functions:

  private createSessionIdIfAbsent(): string {
    let sessionId = this.cookie.get(constants.PLAYER_ID_PARAM);
    if(!sessionId){
      sessionId = uuidv4();
      this.cookie.set(constants.PLAYER_ID_PARAM, sessionId);
    }
    return sessionId;
  }

  private updateStatistics(response: Game): void {
    this.scores.player = response.lastWonScore;
    this.scores.computer = response.lastLostScore;
    this.playedByAll = response.playedByAll;
    this.playedByPlayer = response.timesPlayed;
    this.totalPlayers = response.distinctPlayers;
  }
}
