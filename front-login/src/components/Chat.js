import CardContent from "@mui/material/CardContent";
import Card from "@mui/material/Card";
import React, {useEffect, useState} from "react";
import Websocket from "../http/websocket";

const Chat = ({lobbyId}) => {
    const [isInit,setIsInit] = useState(false);

    useEffect(() => {
        const doInit = async () => {
            await Websocket.init(window.sessionStorage.getItem('_user').id);
            // debugger
            Websocket.subscribe(lobbyId);
        };

        if (!isInit) {
            doInit();
            setIsInit(true);
        }
    }, [setIsInit, isInit, lobbyId]);


    return (
        <Card sx={{minWidth: 100}}>
            <CardContent sx={{maxHeight:"30rem", overflowY:"auto"}}>
            </CardContent>
        </Card>
    );
};

export default Chat;