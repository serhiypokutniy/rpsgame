import {constants} from "@/constants";
import axios, {AxiosResponse} from "axios";
import {environment} from "@/environment.prod";
import Component from "vue-class-component"
import Vue from "vue";
import { v4 as uuidv4 } from 'uuid';
import {Game} from "@/generated";

@Component
export default class GameComponent extends Vue {

    readonly NEW = constants.NEW;
    readonly WEAPONS = constants.WEAPONS;
    readonly RESULTS = constants.RESULTS;
    //
    private scores = {'computer' : 0, 'player' : 0};
    public playedByAll = 0;
    private playedByPlayer = 0;
    private totalPlayers = 0;
    private playerChoice = constants.PEACE;
    private computerChoice  = constants.PEACE;
    private winner = null;

    mounted() {
        document.title = "Rock, Paper, Scissors Game";
        const request = {
            params: {
                playerId: this.createSessionIdIfAbsent()
            }
        }
        axios.get<Game>(environment.BACKEND_PATH_INIT, request)
            .then(response => this.updateStatistics(response))
            .catch(e => console.log(e));
    }

    //Functions called by the player:

   play(weapon: string): void {
        this.playerChoice = weapon;
        this.winner = null; //hide text for a new selection
        const params = new URLSearchParams();
        params.append(constants.PLAYER_ID_PARAM, this.createSessionIdIfAbsent());
        params.append(constants.PLAYER_CHOICE_PARAM, weapon);
        axios.post<Game>(environment.BACKEND_PATH_PLAY, params)
            .then(response => {
               this.winner = constants.NEW;
               if(response.data.playerWins == undefined){
                   this.winner = constants.TIE;
               } else {
                   this.winner = response.data.playerWins ? constants.PLAYER : constants.COMPUTER;
               }
               this.computerChoice = response.data.computerChoice;
               this.updateStatistics(response);
           }).catch(e => console.log(e));
    }

    reset(): void {
        const params = new URLSearchParams();
        params.append(constants.PLAYER_ID_PARAM, this.createSessionIdIfAbsent());
        axios.post<Game>(environment.BACKEND_PATH_RESET, params)
            .then(() => {
                this.scores.player = 0;
                this.scores.computer = 0;
            }).catch(e => console.log(e));
    }

    // Auxiliary functions:

    private updateStatistics(response: AxiosResponse<Game>): void {
        this.scores.player = response.data.lastWonScore;
        this.scores.computer = response.data.lastLostScore;
        this.playedByAll = response.data.playedByAll;
        this.playedByPlayer = response.data.timesPlayed;
        this.totalPlayers = response.data.distinctPlayers;
    }

    private createSessionIdIfAbsent(): string {
        let sessionId = this.$cookies.get(constants.PLAYER_ID_PARAM);
        if(!sessionId){
            sessionId = uuidv4();
            this.$cookies.set(constants.PLAYER_ID_PARAM, sessionId);
        }
        return sessionId;
    }
}
