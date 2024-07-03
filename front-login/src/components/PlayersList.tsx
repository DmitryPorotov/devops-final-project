import CardContent from "@mui/material/CardContent";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";
import Button from "@mui/material/Button";
import Divider from "@mui/material/Divider";
import Card from "@mui/material/Card";
import React, {useContext, useEffect} from "react";
import {LobbyContext} from "../App";
import {useParams} from "react-router-dom";
import Websocket from "../http/websocket";

const PlayersList = ({hasJoinedGame}: {hasJoinedGame: boolean, id?: string | number}) => {
    const lobbyCtx = useContext(LobbyContext);

    const {id: idStr} = useParams();

    const id = parseInt(idStr);

    useEffect(()=>{
    }, [lobbyCtx, lobbyCtx.lobbyData]);

    const handleLeaveClick = (event) => {
        Websocket.send({
            userId: Websocket.playerId,
            type: 'chat',
            lobbyId: id,
            body: {type:'leave'}
        });
        event.stopPropagation();
    };

    const handlePMChange = (event, playerId) => {
        let sendTo = lobbyCtx.lobbyData.sendTo || [];
        if (event.target.checked) {
            sendTo.push(playerId)
        } else {
            sendTo = sendTo.filter(i => i !== playerId);
        }
        lobbyCtx.setLobbyData({
            ...lobbyCtx.lobbyData,
            sendTo
        })
    };

    const handleKickClick = (playerId, name) => {
        //TODO add a prompt
        Websocket.send({
            userId: Websocket.playerId,
            type: 'chat',
            lobbyId: id,
            body: {type:'kick', to: [playerId]}
        })
    };

    return (
        <Card sx={{minWidth: 90, height:"calc(97.5vh - 224px)"}}>
            <CardContent>
                <List>
                    {
                        lobbyCtx.lobbyData?.participants.map((cur, i) => {
                            return (
                                <div key={`user-${i}`}>
                                    <ListItem>
                                        <ListItemText
                                            primary={cur.name}
                                            secondary={
                                                <>
                                                    {cur.id === lobbyCtx.lobbyData?.owner.id ?
                                                        <span>[lobby owner] </span> : null}
                                                    {cur.house ? <span>{cur.house}</span> : null}
                                                    {cur.ping ? <span> {cur.ping}ms</span> : ''}
                                                </>
                                            }
                                        />
                                        {
                                            cur.id !== Websocket.playerId &&
                                            <FormControlLabel control={
                                                <Checkbox aria-label={'private message'}
                                                          onChange={(e) => handlePMChange(e, cur.id)}/>
                                            } label={hasJoinedGame ? "PM":"Private message"}/>

                                        }
                                        {
                                            cur.id !== lobbyCtx.lobbyData.owner.id &&
                                            lobbyCtx.lobbyData.owner.id === Websocket.playerId &&
                                            <Button onClick={() => handleKickClick(cur.id, cur.name)}>Kick</Button>
                                        }
                                    </ListItem>
                                    {(i < lobbyCtx.lobbyData.participants.length - 1) &&
                                    <Divider/>
                                    }
                                </div>
                            )
                        })
                    }
                </List>
                <Button onClick={handleLeaveClick}>Leave</Button>
            </CardContent>
        </Card>
    );
};

export default PlayersList;