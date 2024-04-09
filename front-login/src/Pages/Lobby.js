import React, {useContext, useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import Api from "../http/api";
import LobbyLoginModal from "../components/LobbyLoginModal";
import {AuthContext, WsContext} from "../App";
import {serverIsDeadHandler} from "./common/GlobalErrorHandlers";
import styled from "@mui/material/styles/styled";
import Select from "@mui/material/Select";
import MenuItem from "@mui/material/MenuItem";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Chat from "../components/Chat";
import Button from "@mui/material/Button";
import LobbyEditModal from "../components/LobbyEditModal";
import Storage_ from "../http/storage";
import PlayersList from "../components/PlayersList";

/**
 * @param {{participants: Array.<{id:number,name:string}>}} data
 */
const amParticipating = async ({participants}) => {
    const myId = (await Storage_.getUser()).id;
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

const houses = {
    'moose': true,
    'kraken': true,
    'wolf': true,
    'rose': true,
    'pufferfish': true,
    'lion': true,
};

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

    const [gameCreated, setGameCreated] = useState(false);

    const [canJoin, setCanJoin] = useState(false);

    const [canSelectHouse, setCanSelectHouse] = useState(false);

    const [alreadyJoined, setAlreadyJoined] = useState(false);

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

    const [unusedHouseOptions, setUnusedHouseOptions] = useState({...houses});

    const afterChatInitSetMissedMessages = () => {
        if (!isInitChat) {
            setIsInitChat(true);
            return missedMessages;
        }
        else return []
    };

    let gameWindowRef;

    const setHouseIfMe = (player) => {
        if (player.userId === ws.websocket.playerId && player.house) {
            Storage.setHouseForLobby(id, player.house).then();
            return true;
        }
    };

    useEffect(() => {
        if (!isInit) return ;
        const receiveMessage = async (message) => {
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
                            let newOwner;
                            if (message.userId === ws.lobbyData.owner.id) {
                                const response = await Api.get(`/lobby/${id}`);
                                const data = await response.json();
                                newOwner = data.owner;

                            }
                            ws.setLobbyData({
                                ...ws.lobbyData,
                                participants,
                                owner: newOwner || ws.lobbyData.owner
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
                switch (message.action) {
                    case "create_game":
                        setGameCreated(true);
                        setCanSelectHouse(true);
                        break;
                    case "get_status":
                        setGameCreated(message.status.created);
                        if (message.status.created) {
                            if (message.status.details.gameSettings.players) {
                                const unusedHouses = {};
                                let joined;
                                for (const p of message.status.details.gameSettings.players) {
                                    const participant = ws.lobbyData.participants.find(p_ => p_.id === p.userId);
                                    participant.house = p.house;
                                    unusedHouses[p.house] = p.userId === ws.websocket.playerId;
                                    !joined && (joined = setHouseIfMe(p));
                                }
                                if (joined) {
                                    setCanSelectHouse(false);
                                    setAlreadyJoined(true);
                                    !gameWindowRef && (gameWindowRef = window.open(`/lobby/${id}/game/`, `lobby${id}`));
                                } else {
                                    setCanSelectHouse(true);
                                    setAlreadyJoined(false);
                                }
                                setUnusedHouseOptions({
                                    ...houses,
                                    ...unusedHouses,
                                });
                                ws.setLobbyData({
                                    ...ws.lobbyData
                                });
                            }
                            else {
                                setCanSelectHouse(true);
                            }
                        }
                        break;
                    case "join_game":
                        const unusedHouses = {};
                        for (const p of message.gameSettings.players) {
                            const participant = ws.lobbyData.participants.find(p_ => p_.id === p.userId);
                            setHouseIfMe(p) && setCanSelectHouse(false);
                            participant.house = p.house;
                            unusedHouses[p.house] = p.userId === ws.websocket.playerId;
                        }
                        setUnusedHouseOptions({
                            ...houses,
                            ...unusedHouses,
                        });
                        ws.setLobbyData({
                            ...ws.lobbyData
                        });
                        break;
                }
            }
        };
        ws.websocket.onMessage(id, receiveMessage);
        return () => ws.websocket.offMessage(receiveMessage);
    }, [isInit, ws, id, navigate]);

    useEffect(() => {
        const getLobbyData = () => new Promise(async (resolve) => {
            const storedUser = await Storage_.getUser();
            if (!storedUser) {
                navigate('/');
                return ;
            }
            setIsInit(true);
            await ws.websocket.init(storedUser.id);
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

            if (data.password && !(await amParticipating(data))) {
                setIsLoginModalOpen(true);
            } else {
                await joinLobby(id);
            }
            ws.setLobbyData(data);
            await ws.websocket.init(storedUser.id);
            await ws.websocket.subscribe(id);
            setTimeout(() => {
                ws.websocket.send({
                    action: 'get_status',
                    type: "action",
                });
            }, 1)
        });
        if (!isInit) getLobbyData();
    }, [isLoginModalOpen, setIsLoginModalOpen, ws, auth, isInit, id]);

    const createGame = () => {
        ws.websocket.send({
            userId: ws.websocket.playerId,
            lobbyId: id,
            type: 'action',
            action: 'create_game',
            isRandomHouses: false,
        });
    };

    const joinGame = async () => {
        ws.websocket.send({
            type: 'action',
            action: 'join_game',
            name: (await Storage.getUser()).name,
            joinAs: ws.lobbyData.participants.find((c) => c.id === ws.websocket.playerId).house
        });
        setAlreadyJoined(true);
        !gameWindowRef && (gameWindowRef = window.open(`/lobby/${id}/game/`, `lobby${id}`));
    };


    const houseSelectionChanged = (e) => {
        setCanJoin(e.target.value !== 'none');
        const me = ws.lobbyData?.participants.find((c) => c.id === ws.websocket.playerId);
        if (e.target.value === 'none')
            delete me.house;
        else me.house = e.target.value;
        ws.setLobbyData({
            ...ws.lobbyData,
        });
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
                    <Card sx={{minWidth: 100}} style={{marginBottom: ".5rem"}}>
                        <CardContent style={{display:"flex", justifyContent: "space-between"}}>
                            <Select
                                disabled={!canSelectHouse}
                                onChange={houseSelectionChanged}
                                value={ws.lobbyData?.participants.find((c) => c.id === ws.websocket.playerId)?.house || 'none'}
                                style={{minWidth: 220}}
                                defaultValue={'none'}
                            >
                                <MenuItem value={'none'}>Please select a house...</MenuItem>
                                {unusedHouseOptions.moose && <MenuItem value={'moose'}>Moose</MenuItem>}
                                {unusedHouseOptions.kraken && <MenuItem value={'kraken'}>Kraken</MenuItem>}
                                {unusedHouseOptions.wolf && <MenuItem value={'wolf'}>Wolf</MenuItem>}
                                {unusedHouseOptions.rose && <MenuItem value={'rose'}>Rose</MenuItem>}
                                {unusedHouseOptions.pufferfish && <MenuItem value={'pufferfish'}>Puffer fish</MenuItem>}
                                {unusedHouseOptions.lion && <MenuItem value={'lion'}>Lion</MenuItem>}
                            </Select>
                            {ws.lobbyData?.owner.id === ws.websocket.playerId && !canJoin && !gameCreated &&
                                <Button
                                    onClick={createGame}
                                >Create Game</Button>
                            }
                            {
                                gameCreated &&
                                    <Button disabled={!canJoin || alreadyJoined} onClick={joinGame}>Join</Button>
                            }
                        </CardContent>
                    </Card>
                    <PlayersList id={id}/>
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
                handleClose={()=>{setIsLoginModalOpen(false); navigate('/')}}
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
                        handleClose={()=> setIsSettingsModalOpen(false)}
                    />
            }
        </div>
    )
};

export default Lobby