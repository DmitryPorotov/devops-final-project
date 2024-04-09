import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import React from "react";
import {Button, TextField} from "@mui/material";
import User from "../logic/player";

const Player = ({player}: {player: User}) => {
  return (
    <Card>
      <CardContent>
          <h2>{player.username}</h2>
          <TextField multiline/>
          <Button>Send</Button>
      </CardContent>
    </Card>
  )
};

export default Player;