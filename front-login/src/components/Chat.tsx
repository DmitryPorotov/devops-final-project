import CardContent from "@mui/material/CardContent";
import Card from "@mui/material/Card";
import React, {useContext, useEffect, useRef, useState} from "react";
import Paper from "@mui/material/Paper";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import serverIsDeadHandler from "../Pages/common/GlobalErrorHandlers";
import {AuthContext, LobbyContext} from "../App";
import Websocket from "../http/websocket";

const Chat = ({lobbyId, afterInitGetMissedMessages, style}) => {
    const [message, setMessage] = useState('');
    const [chatMessages, setChatMessages] = useState([]);
    const scrollToRef = useRef<HTMLDivElement>();
    const auth = useContext(AuthContext);
    const lobbyCtx = useContext(LobbyContext);

    const formatDate = (time) => `[${String(time.getHours()).padStart(2, '0')}:${String(time.getMinutes()).padStart(2, '0')}:${String(time.getSeconds()).padStart(2, '0')}]`;

    useEffect(() => {
        if (!lobbyCtx.lobbyData) return ;
        const receiveMessage = async (message) => {
            if (message.type === 'chat') {
                message.ts = formatDate(new Date());
                setChatMessages([...chatMessages, message]);
                setTimeout(() => {
                    scrollToRef.current?.scrollIntoView({behavior: "smooth", block: "end", inline: "nearest"})
                }, 0)
            }
            else if (message.type === 'error') {
                console.warn(message);
            }
        };
        Websocket.onMessage(lobbyId, receiveMessage);
        const messages = afterInitGetMissedMessages();
        if (messages.length) {
            for (let i = 0; i < messages.length; i++) {
                messages[i].ts = formatDate(messages[i].time ? new Date(messages[i].time): new Date());
            }
            setChatMessages(messages);
            setTimeout(() => {
                scrollToRef.current?.scrollIntoView({behavior: "smooth", block: "end", inline: "nearest"})
            }, 1)
        }

        return () => Websocket.offMessage(receiveMessage);
    }, [chatMessages, lobbyCtx, lobbyId, afterInitGetMissedMessages]);

    const handleSubmit = (event) => {
        event.preventDefault();
        event.stopPropagation();
        if (!message) return;
        try {
            Websocket.send({
                userId: Websocket.playerId,
                type: 'chat',
                lobbyId,
                body: {
                    to: lobbyCtx.lobbyData.sendTo?.length ? [...lobbyCtx.lobbyData.sendTo, Websocket.playerId] : [],
                    type: 'message',
                    body: message
                }
            });
        } catch (e) {
            serverIsDeadHandler(e, auth);
        } finally {
            setMessage('');
        }
    };

    const buildMessageBody = msg => {
        const header = `${msg.ts} ${msg.name}`;
        if (msg.type === 'chat')
            switch (msg.body.type) {
                case 'message':
                    if (msg.body.to?.length) {
                        return `${header} [to ${msg.body.to
                            .reduce((a,c) => {
                                const pl = lobbyCtx.lobbyData.participants.find(p => p.id === c);
                                if (pl && c !== msg.userId) {
                                    a.push(pl.name)
                                }
                                return a;
                            }, []).join(', ')}] : ${msg.body.body}`
                    } else {
                        return `${header} : ${msg.body.body}`;
                    }
                case 'join':
                    return `${header} joins.`;
                case 'kick':
                    return `${header} is kicked.`;
                case 'leave':
                    return `${header} leaves.`;
                case 'edit':
                    return `${header} changed lobby name to ${msg.body.lobbyName}`;
                default:
                    return '';
            }
    };


    return (
        <Paper variant={"outlined"} style={style}>
            <Card>
            <CardContent sx={{padding:"8px", ":last-child": {paddingBottom:"12px"}}}>
            <Card sx={{minWidth: 100, flexGrow:5, maxHeight: '10rem', overflowY: 'auto'}}>
                {!!chatMessages.length
                 &&
                    chatMessages.map((msg,i) => {
                            if (i !== chatMessages.length - 1) {
                               return <CardContent key={`msg-${i}`} sx={{maxHeight: "30rem", padding:"3px", overflowY: "auto"}}>
                                   {buildMessageBody(msg)}
                                </CardContent>
                            }
                            else {
                                return <CardContent ref={scrollToRef} key={`msg-${i}`} sx={{maxHeight: "30rem", padding:"3px", overflowY: "auto", ":last-child": {paddingBottom:"5px"}}}>
                                    {buildMessageBody(msg)}
                                </CardContent>
                            }
                        }
                    )
                }
            </Card>
            <Card sx={{margin: '.4rem .2rem .3rem .2rem', paddingTop: '.4rem'}}>
                    <form style={{display: 'flex'}} onSubmit={handleSubmit} autoComplete={'off'}>
                        <TextField
                            sx={{flexGrow:4}}
                            label="message"
                            variant="outlined"
                            onChange={e => setMessage(e.target.value)}
                            value={message}
                        />
                        <Button sx={{flexGrow:1}} type={'submit'}>Send</Button>
                    </form>

            </Card>
            </CardContent>
            </Card>
        </Paper>
    );
};

export default Chat;