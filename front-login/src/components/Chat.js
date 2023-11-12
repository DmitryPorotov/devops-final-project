import CardContent from "@mui/material/CardContent";
import Card from "@mui/material/Card";
import React, {useContext, useEffect, useRef, useState} from "react";
import Paper from "@mui/material/Paper";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import {serverIsDeadHandler} from "../Pages/common/GlobalErrorHandlers";
import {AuthContext, WsContext} from "../App";

const Chat = ({lobbyId}) => {
    const [isInit, setIsInit] = useState(false);
    const [message, setMessage] = useState('');
    const [chatMessages, setChatMessages] = useState([]);
    const scrollToRef = useRef();
    const auth = useContext(AuthContext);
    const ws = useContext(WsContext);

    useEffect(() => {
        if (!ws.lobbyData) return ;
        const receiveMessage = (message) => {
            if (message.type === 'chat') {
                const time = new Date();
                message.ts = `[${String(time.getHours()).padStart(2, '0')}:${String(time.getMinutes()).padStart(2, '0')}:${String(time.getSeconds()).padStart(2, '0')}]`;
                setChatMessages([...chatMessages, message]);

                setTimeout(() => {
                    scrollToRef.current?.scrollIntoView({behavior: "smooth", block: "end", inline: "nearest"})
                }, 0)
            }
        };
        ws.websocket.onMessage(lobbyId,receiveMessage);
        return () => ws.websocket.offMessage(receiveMessage);
    }, [chatMessages, ws, lobbyId]);


    useEffect(() => {
        const doInit = async () => {
            await ws.websocket.init(JSON.parse(window.sessionStorage.getItem('_user')).id);
            await ws.websocket.subscribe(lobbyId);
            setChatMessages([]);
        };

        if (!isInit) {
            doInit();
            setIsInit(true);
        }
    }, [setIsInit, isInit, lobbyId, ws.websocket]);

    const handleSubmit = (event) => {
        event.preventDefault();
        event.stopPropagation();
        if (!message) return;
        try {
            ws.websocket.send({
                userId: ws.websocket.playerId,
                type: 'chat',
                lobbyId,
                body: {
                    to: ws.lobbyData.sendTo?.length ? [...ws.lobbyData.sendTo, ws.websocket.playerId] : [],
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
        switch (msg.body.type) {
            case 'message':
                if (msg.body.to?.length) {
                    return `${header} [to ${msg.body.to
                        .reduce((a,c) => {
                            const pl = ws.lobbyData.participants.find(p => p.id === c);
                            if (pl && c !== ws.websocket.playerId) {
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
        <Paper variant={"outlined"}>
            <Card sx={{minWidth: 100, flexGrow:5, maxHeight: '12rem', overflowY: 'auto'}}>
                {!!chatMessages.length
                 &&
                    chatMessages.map((msg,i) => {
                            if (i !== chatMessages.length - 1) {
                               return <CardContent key={`msg-${i}`} sx={{maxHeight: "30rem", overflowY: "auto"}}>
                                   {buildMessageBody(msg)}
                                </CardContent>
                            } else {
                                return <CardContent ref={scrollToRef} key={`msg-${i}`} sx={{maxHeight: "30rem", overflowY: "auto"}}>
                                    {buildMessageBody(msg)}
                                </CardContent>
                            }
                        }
                    )

                }
            </Card>
            <Card sx={{margin: '.8rem .2rem .3rem .2rem', paddingTop: '.4rem'}}>
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
        </Paper>
    );
};

export default Chat;