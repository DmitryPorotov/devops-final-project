import React, {useContext, useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import Api from "../http/api";
import LobbyLoginModal from "../components/LobbyLoginModal";
import {AuthContext, WsContext} from "../App";
import {serverIsDeadHandler} from "./common/GlobalErrorHandlers";
import styled from "@mui/material/styles/styled";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import Divider from "@mui/material/Divider";
import Select from "@mui/material/Select";
import MenuItem from "@mui/material/MenuItem";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Chat from "../components/Chat";
import Button from "@mui/material/Button";
import Checkbox from "@mui/material/Checkbox";
import FormControlLabel from "@mui/material/FormControlLabel";
import LobbyEditModal from "../components/LobbyEditModal";

/**
 * @param {{participants: Array.<{id:number,name:string}>}} data
 */
const amParticipating = ({participants}) => {
    const myId = JSON.parse(window.sessionStorage.getItem('_user')).id;
    return participants.reduce((acc, cur) => {
        if (cur.id === myId) acc = true;
        return acc;
    }, false)
};
/**
 *
 * @param {string} id
 * @param {string=} password
 * @returns {Promise<any>}
 */
const joinLobby = async (id, password= '1111') => {
    const body = password ? JSON.stringify({password}) : '{}';
    const response = await Api.patch(`/lobby/${id}/join`, body);
    return await response.json();
};

const editLobby = async (id, settings) => {
    const body = JSON.stringify(settings);
    const response = await Api.patch(`/lobby/${id}`, body);
    return await response.json();
};

const LobbyHeader = styled('div')(
    ({ theme }) => `
  color: ${theme.palette.text.primary};
  font-size: 34px;
  font-weight: ${theme.typography.fontWeightMedium};
`,
);


const Lobby = () => {
    let {id} = useParams();

    /**
     * @type {number}
     */
    id = parseInt(id);

    const [isInit, setIsInit] = useState(false);

    const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);

    const [isSettingsModalOpen, setIsSettingsModalOpen] = useState(false);

    const [lobbySettingsErrors, setLobbySettingsErrors] = useState({});

    const [passwordErrors, setPasswordErrors] = useState([]);

    const auth = useContext(AuthContext);

    const ws = useContext(WsContext);

    const navigate =  useNavigate();

    const broadcastLobbyEdit = (s) => {
        try {
            const body = {type: 'edit', lobbyName: s.name, deletePassword: !!s.deletePassword };
            if (s.password) {
                body.password = '*';
            }
            ws.websocket.send({
                userId: ws.websocket.playerId,
                type: 'chat',
                lobbyId: id,
                body
            });
        } catch (e) {
            serverIsDeadHandler(e, auth);
        }
    };

    const [isInitChat, setIsInitChat] = useState(false);

    const [missedMessages] = useState([]);

    const afterChatInitSetMissedMessages = () => {
        if (!isInitChat) {
            setIsInitChat(true);
            return missedMessages;
        }
        else return []
    };


    useEffect(() => {
        if (!isInit) return ;
        const receiveMessage = (message) => {
            if (!isInitChat) {
                missedMessages.push(message);
            }
            if (message.type === 'chat') {
                switch (message.body.type) {
                    case 'join':
                        if (ws.lobbyData && !ws.lobbyData.participants.find(p => p.id === message.userId)) {
                            ws.setLobbyData({
                                ...ws.lobbyData,
                                participants: [...ws.lobbyData.participants, {id: message.userId, name: message.name}]
                            })
                        }
                        break;
                    case 'leave':
                        if (message.userId === ws.websocket.playerId) {
                            navigate('/');
                        }
                        else {
                            const participants = ws.lobbyData.participants.filter(p => p.id !== message.userId);
                            ws.setLobbyData({
                                ...ws.lobbyData,
                                participants,
                                owner: participants.length === 1 ? participants[0] : ws.lobbyData.owner
                            })
                        }
                        break;
                    case 'kick':
                        if (message.body.to[0] === ws.websocket.playerId) {
                            if (message.body.body) {
                                window.alert(message.body.body);
                            }
                            navigate('/');
                        }
                        else {
                            const participants = ws.lobbyData.participants.filter(p => p.id !== message.body.to[0]);
                            ws.setLobbyData({
                                ...ws.lobbyData,
                                participants,
                            })
                        }
                        break;
                    case 'edit':
                        ws.setLobbyData({
                            ...ws.lobbyData,
                            name: message.body.lobbyName
                        });
                        break;
                    default:
                        break;
                }
            } else if (message.type === 'action') {

            }
        };
        ws.websocket.onMessage(id, receiveMessage);
        return () => ws.websocket.offMessage(receiveMessage);
    }, [isInit, ws, id, navigate]);

    useEffect(() => {
        const getLobbyData = () => new Promise(async (resolve) => {
            setIsInit(true);
            await ws.websocket.init(JSON.parse(window.sessionStorage.getItem('_user')).id);
            let data;
            try {
                const response = await Api.get(`/lobby/${id}`);
                data = await response.json();
                if (data.statusCode === 401) {
                    auth.setIsLoginShown(true);
                    auth.loginCallback = () => {
                        auth.setIsLoginShown(false);
                        return getLobbyData();
                    }
                } else {
                    resolve(data);
                }
            } catch (e) {
                serverIsDeadHandler(e, auth);
                return;
            }

            if (data.password && !amParticipating(data)) {
                setIsLoginModalOpen(true);
            } else {
                await joinLobby(id);
            }
            ws.setLobbyData(data);
        });
        if (!isInit) getLobbyData();
    }, [isLoginModalOpen, setIsLoginModalOpen, ws, auth, isInit, id]);

    const handleLeaveClick = (event) => {
        ws.websocket.send({
            userId: ws.websocket.playerId,
            type: 'chat',
            lobbyId: id,
            body: {type:'leave'}
        });
        event.stopPropagation();
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

    return (
        <div>
            <LobbyHeader>Lobby: {ws.lobbyData?.name}
                {
                    ws.lobbyData?.owner.id === ws.websocket.playerId &&
                        <Button onClick={()=>{setIsSettingsModalOpen(true)}}>Edit lobby</Button>
                }
            </LobbyHeader>
            <div style={{display: "flex"}}>
                <div style={{flexFlow: "row", flexGrow: 2}}>
                    {!isLoginModalOpen && <Chat lobbyId={id} afterInitGetMissedMessages={afterChatInitSetMissedMessages}/>}
                </div>
                <div style={{flexFlow: "row", flexGrow: 1, padding:".2rem"}}>
                    {
                        ws.lobbyData?.owner.id === ws.websocket.playerId &&
                            <Card sx={{minWidth: 100}} style={{marginBottom: ".5rem"}}>
                                <CardContent>
                                    <Select defaultValue={'moose'} autoWidth={true}>
                                        <MenuItem value={'moose'}>Moose</MenuItem>
                                        <MenuItem value={'kraken'}>Kraken</MenuItem>
                                        <MenuItem value={'wolf'}>Wolf</MenuItem>
                                        <MenuItem value={'rose'}>Rose</MenuItem>
                                        <MenuItem value={'pufferfish'}>Puffer fish</MenuItem>
                                        <MenuItem value={'lion'}>Lion</MenuItem>
                                    </Select>
                                    <Button>Create Game</Button>
                                </CardContent>
                            </Card>
                    }
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
                                                        secondary={cur.id === ws.lobbyData.owner.id ? 'owner' : null}
                                                    />
                                                    {
                                                        cur.id !== ws.websocket.playerId &&
                                                        <FormControlLabel control={
                                                            <Checkbox aria-label={'private message'} onChange={(e) => handlePMChange(e, cur.id)}/>
                                                        } label="Private message" />

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
                </div>
            </div>
            {isLoginModalOpen &&
            <LobbyLoginModal
                isOpen={isLoginModalOpen}
                tryPassword={async p => {
                    const lobbyData = await joinLobby(id, p);
                    setPasswordErrors([]);
                    if (!lobbyData.statusCode) {
                        setIsLoginModalOpen(false);
                        ws.setLobbyData(lobbyData);
                    } else if (String(lobbyData.statusCode).startsWith('4')) {
                        if ('string' === typeof lobbyData.message) {
                            setPasswordErrors([lobbyData.message]);
                        } else {
                            setPasswordErrors(lobbyData.message.password);
                        }
                    }
                }}
                passwordErrors={passwordErrors}
            />
            }
            {
                isSettingsModalOpen &&
                    <LobbyEditModal
                        isOpen={isSettingsModalOpen}
                        errors={lobbySettingsErrors}
                        oldName={ws.lobbyData.name}
                        oldPassword={''}
                        updateSettings={async s => {
                            if (!s.password) {
                                delete s.password;
                            }
                            if (!s.deletePassword) {
                                delete s.deletePassword;
                            }
                            const lobbyData = await editLobby(id, s);
                            setLobbySettingsErrors({});
                            if (!lobbyData.statusCode) {
                                setIsSettingsModalOpen(false);
                                ws.setLobbyData({
                                    ...ws.lobbyData,
                                    name: lobbyData.name
                                });
                                broadcastLobbyEdit(s)
                            } else if (String(lobbyData.statusCode).startsWith('4')) {
                                setLobbySettingsErrors(lobbyData.message);
                            }
                        }}
                    />
            }
        </div>
    )
};

export default Lobby