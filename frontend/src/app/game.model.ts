export interface Game {
  gameId: string;
  computerChoice: string;
  playerChoice: string;
  winner: string;
  player: {
    playerId: string,
    lastUpdated: string,
    timesPlayed: number,
    lastWonScore: number,
    lastLostScore: number,
    distinctPlayers: number,
    playedByAll: number
  },

}
