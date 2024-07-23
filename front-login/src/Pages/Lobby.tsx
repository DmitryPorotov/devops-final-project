import React, {useCallback, useContext, useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import Api from "../http/api";
import LobbyLoginModal from "../components/LobbyLoginModal";
import {AuthContext, LobbyContext} from "../App";
import serverIsDeadHandler from "./common/GlobalErrorHandlers";
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
import Websocket from "../http/websocket";

/**
 * @param {{participants: Array.<{id:number,name:string,ping:string,connected:boolean}>}} data
 */
const amParticipating = async ({participants}) => {
    const myId = Storage_.getUser().id;
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

function unusedHousesToArray(unusedHouses: {[k:string]: boolean}): string[] {
    const retVal = [];
    for (let k in unusedHouses) {
        if (unusedHouses[k]) {
            retVal.push(k)
        }
    }
    return retVal;
}

const Lobby = () => {
    const {id: idStr} = useParams();


    const id = parseInt(idStr);

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

    const lobbyCtx = useContext(LobbyContext);

    const navigate =  useNavigate();

    const broadcastLobbyEdit = (s) => {
        try {
            const body : {type: 'edit', lobbyName: string, deletePassword: boolean, password?: string }
                = {type: 'edit', lobbyName: s.name, deletePassword: !!s.deletePassword };
            if (s.password) {
                body.password = '*';
            }
            Websocket.send({
                userId: Websocket.playerId,
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

    const afterChatInitSetMissedMessages = useCallback(() => {
        if (!isInitChat) {
            setIsInitChat(true);
            return missedMessages;
        }
        else return []
    }, [isInitChat, missedMessages]);

    const setHouseIfMe = useCallback( (player) => {
        if ((player.userId === Websocket.playerId) && player.house) {
            Storage_.setHouseForLobby(id, player.house);
            return true;
        }
    }, [id]);

    useEffect(() => {
        if (!isInit) return ;
        const receiveMessage = async (message) => {
            if (!isInitChat) {
                missedMessages.push(message);
            }
            if (message.type === 'chat') {
                switch (message.body.type) {
                    case 'join':
                        if (lobbyCtx.lobbyData && !lobbyCtx.lobbyData.participants.find(p => p.id === message.userId)) {
                            lobbyCtx.setLobbyData({
                                ...lobbyCtx.lobbyData,
                                participants: [...lobbyCtx.lobbyData.participants, {id: message.userId, name: message.name, connected: true}]
                            })
                        }
                        break;
                    case 'leave':
                        if (message.userId === Websocket.playerId) {
                            navigate('/');
                        }
                        else {
                            const participants = lobbyCtx.lobbyData.participants.filter(p => p.id !== message.userId);
                            let newOwner;
                            if (message.userId === lobbyCtx.lobbyData.owner.id) {
                                const response = await Api.get(`/lobby/${id}`);
                                const data = await response.json();
                                newOwner = data.owner;

                            }
                            lobbyCtx.setLobbyData({
                                ...lobbyCtx.lobbyData,
                                participants,
                                owner: newOwner || lobbyCtx.lobbyData.owner
                            })
                        }
                        break;
                    case 'kick':
                        if (message.body.to[0] === Websocket.playerId) {
                            if (message.body.body) {
                                window.alert(message.body.body);
                            }
                            navigate('/');
                        }
                        else {
                            const participants = lobbyCtx.lobbyData.participants.filter(p => p.id !== message.body.to[0]);
                            lobbyCtx.setLobbyData({
                                ...lobbyCtx.lobbyData,
                                participants,
                            })
                        }
                        break;
                    case 'edit':
                        lobbyCtx.setLobbyData({
                            ...lobbyCtx.lobbyData,
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
                                    const participant = lobbyCtx.lobbyData.participants.find(p_ => p_.id === p.userId);
                                    if (participant) {
                                        (participant.house = p.house);
                                    }
                                    else {
                                        lobbyCtx.lobbyData.participants.push(p);
                                    }

                                    unusedHouses[p.house] = p.userId === Websocket.playerId;
                                    !joined && (joined = setHouseIfMe(p));
                                }
                                if (joined) {
                                    setCanSelectHouse(false);
                                    setAlreadyJoined(true);
                                    // !gameWindowRef && (gameWindowRef = window.open(`/lobby/${id}/game/`, `lobby${id}`));
                                } else {
                                    setCanSelectHouse(true);
                                    setAlreadyJoined(false);
                                }
                                setUnusedHouseOptions({
                                    ...houses,
                                    ...unusedHouses,
                                });
                                lobbyCtx.setLobbyData({
                                    ...lobbyCtx.lobbyData
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
                            const participant = lobbyCtx.lobbyData.participants.find(p_ => p_.id === p.userId);
                            setHouseIfMe(p) && setCanSelectHouse(false);
                            participant && (participant.house = p.house);
                            unusedHouses[p.house] = p.userId === Websocket.playerId;
                        }
                        setUnusedHouseOptions({
                            ...houses,
                            ...unusedHouses,
                        });
                        lobbyCtx.setLobbyData({
                            ...lobbyCtx.lobbyData
                        });
                        break;
                }
            }
            else if (message.type === 'system') {
                console.log(message)
                const p = lobbyCtx.lobbyData?.participants.find(pr => pr.id === message.userId);
                if (p) {
                    if (message.body.type === 'error') {
                        p.connected = false;
                    } else if (message.body.type === 'ping') {
                        p.ping = message.body.body;
                    }
                }
                lobbyCtx.setLobbyData({
                    ...lobbyCtx.lobbyData
                })
            }
        };
        Websocket.onMessage(id, receiveMessage);
        return () => Websocket.offMessage(receiveMessage);
    }, [isInit, lobbyCtx, id, navigate, isInitChat, missedMessages, setHouseIfMe]);

    useEffect(() => {
        const getLobbyData = () => new Promise(async (resolve) => {
            const storedUser = Storage_.getUser();
            if (!storedUser) {
                navigate('/');
                return ;
            }
            setIsInit(true);
            await Websocket.init(storedUser.id);
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
            }
            else {
                const response = await joinLobby(id);
                if (response.statusCode === 409) {
                    navigate('/');
                    auth.globalError = response.message;
                    auth.setIsSnackbarOpen(true);
                }
            }
            lobbyCtx.setLobbyData(data);
            await Websocket.init(storedUser.id);
            await Websocket.subscribe(id);
            setTimeout(() => {
                Websocket.send({
                    action: 'get_status',
                    type: "action",
                });
            }, 1)
        });
        if (!isInit) getLobbyData();
    }, [isLoginModalOpen, setIsLoginModalOpen, lobbyCtx, auth, isInit, id, navigate]);

    const createGame = () => {
        Websocket.send({
            userId: Websocket.playerId,
            lobbyId: id,
            type: 'action',
            action: 'create_game',
            isRandomHouses: false,
        });
    };

    const joinGame = async () => {
        Websocket.send({
            type: 'action',
            action: 'join_game',
            name: Storage_.getUser().name,
            joinAs: lobbyCtx.lobbyData.participants.find((c) => c.id === Websocket.playerId).house
        });
        setAlreadyJoined(true);
        // !gameWindowRef && (gameWindowRef = window.open(`/lobby/${id}/game/`, `lobby${id}`));
    };


    const houseSelectionChanged = (e) => {
        setCanJoin(e.target.value !== 'none');
        const me = lobbyCtx.lobbyData?.participants.find((c) => c.id === Websocket.playerId);
        if (e.target.value === 'none')
            delete me.house;
        else me.house = e.target.value;
        lobbyCtx.setLobbyData({
            ...lobbyCtx.lobbyData,
        });
    };

    const botsButtonClick = (e) => {
        Websocket.send({
            type: 'action',
            action: 'fill_with_bots',
            player_action: {
                houseTypes: unusedHousesToArray(unusedHouseOptions)
            }
        });
    };

    return (
        <div>
            <LobbyHeader>Lobby: {lobbyCtx.lobbyData?.name}
                {
                    lobbyCtx.lobbyData?.owner.id === Websocket.playerId &&
                        <Button onClick={()=>{setIsSettingsModalOpen(true)}}>Edit lobby</Button>
                }
            </LobbyHeader>
            <div style={{display: "flex"}}>
                <div style={{flexFlow: "row", flexGrow: alreadyJoined ? 10: 2}}>
                    <div style={{flexFlow:"column", display:"flex"}}>
                    {alreadyJoined && <iframe title={"defold-frame"} style={{width:"100%", border:"none", flexGrow:3, minHeight:"50vh", height:"calc(97.5vh - 372px)"}} src={`/lobby/${id}/game/index.html`}/>}
                    {!isLoginModalOpen && <Chat style={{flexGrow:1}} lobbyId={id} afterInitGetMissedMessages={afterChatInitSetMissedMessages}/>}
                    </div>
                </div>
                <div style={{flexFlow: "row", flexGrow: 1, padding:".2rem"}}> 
                    <Card sx={{minWidth: 100}} style={{marginBottom: ".5rem"}}>
                        <CardContent style={{display:"flex", justifyContent: "space-between"}}>
                            <Select
                                disabled={!canSelectHouse}
                                onChange={houseSelectionChanged}
                                value={lobbyCtx.lobbyData?.participants.find((c) => c.id === Websocket.playerId)?.house || 'none'}
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
                            {lobbyCtx.lobbyData?.owner.id === Websocket.playerId && !canJoin && !gameCreated &&
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
                    {
                        lobbyCtx.lobbyData?.owner.id === Websocket.playerId &&
                        <Card sx={{minWidth: 100}} style={{marginBottom: ".5rem"}}>
                            <CardContent>
                                <Button onClick={botsButtonClick}>Fill with bots</Button>
                            </CardContent>
                        </Card>
                    }
                    <PlayersList id={id} hasJoinedGame={alreadyJoined}/>
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
                        lobbyCtx.setLobbyData(lobbyData);
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
                        oldName={lobbyCtx.lobbyData.name}
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
                                lobbyCtx.setLobbyData({
                                    ...lobbyCtx.lobbyData,
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