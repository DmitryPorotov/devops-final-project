import React from 'react';
import './App.css';
import {Grid} from "@mui/material";
import Player from "./components/player";
import User from "./logic/player"

function App({players} : {players: User[]}) {


  return (
      <Grid padding={"2rem"} container spacing={2}>
          {
              players.map((u, i) => <Grid key={`p${i}`} item xs={4}>
                  <Player player={u as User}/>
              </Grid>
              )
          }
      </Grid>
  );
}

export default App;
