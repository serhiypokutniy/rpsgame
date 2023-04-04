<script lang="ts" src="./GameComponent.ts"></script>
<style src="./GameComponent.css"></style>
<template>
  <div>

    <div class="score">
      <h1>Rock, Paper, Scissors</h1>
      <b-button disabled variant="primary" class="scoreboard">Score: {{scores.player}} | {{scores.computer}}</b-button>
      <b-button pill  variant="info" v-on:click="reset()">Reset</b-button>
    </div>

    <div>
      <b-card-group class="select-weapon-board">
        <b-card title="You" class="mb-2" style="margin-right: 15px">
          <b-card-text class="label">
            <b-link disabled href="#">
              <Transition name="slide-fade" mode="out-in">
                <i :key="playerChoice" :class="`fa fa-hand-${playerChoice.toLowerCase()}-o`"></i>
              </Transition>
            </b-link>
          </b-card-text>
        </b-card>
        <b-card title="Computer" class="mb-2">
          <b-card-text class="label">
            <b-link href="#foo">
              <Transition name="slide-fade" mode="out-in">
                <i :key="computerChoice" :class="`fa fa-hand-${computerChoice.toLowerCase()}-o`"></i>
              </Transition>
            </b-link>
          </b-card-text>
        </b-card>
      </b-card-group>
    </div>

    <div class="select-weapon-board">
      <b-card class="mb-2">
        <b-card-text id="result">
          <Transition name="slide-fade" mode="out-in">
            <div :key="winner">{{winner ? RESULTS[winner] : RESULTS[NEW]}}</div>
          </Transition>
        </b-card-text>
        <i v-for="w in WEAPONS"
           v-on:click="play(w)" v-bind:key="w"
           :id="`idWeapon${w}`" :class="`fa fa-hand-${w.toLocaleLowerCase()}-o select-weapon`" style="font-size: 7em"></i>
      </b-card>
    </div>

    <div class="statistics">
      <span>Played by all: {{playedByAll}}</span>
      <span>Played by you: {{playedByPlayer}}</span>
      <span>Total players: {{totalPlayers}}</span>
    </div>

  </div>
</template>
