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
import {WsContext} from "../App";
import {useParams} from "react-router-dom";

const PlayersList = () => {
    const ws = useContext(WsContext);

    let {id} = useParams();

    useEffect(()=>{
    }, [ws, ws.lobbyData]);

    /**
     * @type {number}
     */
    id = parseInt(id);

    const handleLeaveClick = (event) => {
        ws.websocket.send({
            userId: ws.websocket.playerId,
            type: 'chat',
            lobbyId: id,
            body: {type:'leave'}
        });
        event.stopPropagation();
    };

    const handlePMChange = (event, playerId) => {
        let sendTo = ws.lobbyData.sendTo || [];
        if (event.target.checked) {
            sendTo.push(playerId)
        } else {
            sendTo = sendTo.filter(i => i !== playerId);
        }
        ws.setLobbyData({
            ...ws.lobbyData,
            sendTo
        })
    };

    const handleKickClick = (playerId, name) => {
        //TODO add a prompt
        ws.websocket.send({
            userId: ws.websocket.playerId,
            type: 'chat',
            lobbyId: id,
            body: {type:'kick', to: [playerId]}
        })
    };

    return (
        <Card sx={{minWidth: 100}}>
            <CardContent>
                <List>
                    {
                        ws.lobbyData?.participants.map((cur, i) => {
                            return (
                                <div key={`user-${i}`}>
                                    <ListItem>
                                        <ListItemText
                                            primary={cur.name}
                                            secondary={
                                                <>
                                                    {cur.id === ws.lobbyData?.owner.id ?
                                                        <span>[lobby owner] </span> : null}
                                                    {cur.house ? <span>{cur.house}</span> : null}
                                                </>
                                            }
                                        />
                                        {
                                            cur.id !== ws.websocket.playerId &&
                                            <FormControlLabel control={
                                                <Checkbox aria-label={'private message'}
                                                          onChange={(e) => handlePMChange(e, cur.id)}/>
                                            } label="Private message"/>

                                        }
                                        {
                                            cur.id !== ws.lobbyData.owner.id &&
                                            ws.lobbyData.owner.id === ws.websocket.playerId &&
                                            <Button onClick={() => handleKickClick(cur.id, cur.name)}>Kick</Button>
                                        }
                                    </ListItem>
                                    {(i < ws.lobbyData.participants.length - 1) &&
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