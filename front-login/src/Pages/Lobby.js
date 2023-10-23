import React, {useContext, useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import Api from "../http/api";
import LobbyLoginModal from "../components/LobbyLoginModal";
import {AuthContext} from "../App";
import {serverIsDeadHandler} from "./common/GlobalErrorHandlers";
import styled from "@mui/material/styles/styled";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import Divider from "@mui/material/Divider";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Chat from "../components/Chat";

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

const LobbyHeader = styled('div')(
    ({ theme }) => `
  color: ${theme.palette.text.primary};
  font-size: 34px;
  font-weight: ${theme.typography.fontWeightMedium};
`,
);


const Lobby = () => {
    let {id} = useParams();

    id = parseInt(id);

    const [lobbyData, setLobbyData] = useState();

    const [isModalOpen, setIsModalOpen] = useState(false);

    const [passwordErrors, setPasswordErrors] = useState([]);

    const auth = useContext(AuthContext);

    useEffect(() => {

        const getLobbyData = () => new Promise(async (resolve, reject) => {
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
                setIsModalOpen(true);
            } else {
                await joinLobby(id);
            }
            setLobbyData(data);
        });

        if (!lobbyData) getLobbyData();
    });

    return (
        <div>
            <LobbyHeader>Lobby: {lobbyData && lobbyData.name}</LobbyHeader>
            <div style={{display: "flex"}}>
                <div style={{flexFlow: "row", flexGrow: 2, backgroundColor: 'rgba(0,0,200,.5)'}}>
                    <Chat lobbyId={id}/>
                </div>
                <div style={{flexFlow: "row", flexGrow: 1, backgroundColor: 'rgba(200,0,0,.5)', padding:".2rem"}}>
                    <Card sx={{minWidth: 100}}>
                        <CardContent>
                            <List>
                                {
                                    lobbyData?.participants.map((cur, i) => {
                                        return (
                                            <div key={`user-${i}`}>
                                                <ListItem>
                                                    <ListItemText
                                                        primary={cur.name}
                                                        secondary={cur.id === lobbyData.owner.id ? 'owner' : null}
                                                    />
                                                </ListItem>
                                                {(i < lobbyData.participants.length - 1) &&
                                                <Divider/>
                                                }

                                            </div>
                                        )
                                    })
                                }
                            </List>
                        </CardContent>
                    </Card>
                </div>
            </div>
            {isModalOpen &&
            <LobbyLoginModal
                isOpen={isModalOpen}
                tryPassword={async p => {
                    const lobbyData = await joinLobby(id, p);
                    if (!lobbyData.statusCode) {
                        setIsModalOpen(false);
                    } else if (lobbyData.statusCode === 403) {
                        if ('string' === typeof lobbyData.message) {
                            setPasswordErrors([lobbyData.message]);
                        } else {
                            setPasswordErrors(lobbyData.message);
                        }
                    }
                }}
                passwordErrors={passwordErrors}
            />
            }

        </div>
    )
};

export default Lobby