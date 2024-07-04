import React, {createContext, useCallback, useContext, useMemo, useState} from "react";
import LoginForm from "./Pages/LoginForm";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import SignupForm from "./Pages/SignupForm";
import Lobbies from "./Pages/Lobbies";
import Lobby from "./Pages/Lobby";
import {Outlet} from "react-router";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";
import Storage_ from "./http/storage";
import HeaderBar from "./components/HeaderBar";
import AuthContextValue from "./auth-context-values.interface";
import User from './user.interface'
import ILobby from './Pages/lobby.interface'

const RoutesWrapper = () => {
    const auth = useContext(AuthContext);
    const [isSnackbarOpen, setIsSnackbarOpen] = useState<boolean>(false);
    auth.setIsSnackbarOpen = setIsSnackbarOpen;

    const handleSnackbarClose = (event, reason?) => {
        if (reason === 'clickaway') {
            return;
        }
        auth.globalError = null;
        setIsSnackbarOpen(false);
    };

    const message = (
        <Alert sx={{ width: '90%' }} severity="error">Error: {auth.globalError}</Alert>
    );

    const action = (
        <div style={{fontSize: "1.5rem", margin: "0.4rem", cursor: 'pointer'}} onClick={handleSnackbarClose}>×</div>
    );

    return (
        <>
            <HeaderBar sx={{marginBottom:"1rem"}}/>
            <Outlet />
            <div className={'login-container'}>
                {
                    auth.isLoginShown &&
                    <LoginForm/>
                }
                <Snackbar
                    open={isSnackbarOpen}
                    autoHideDuration={30000}
                    message={message}
                    action={action}
                    onClose={handleSnackbarClose}
                />
            </div>
        </>
    );
};

const router = createBrowserRouter([
    {
        path: '/',
        element: <RoutesWrapper/>,
        children: [
            {
                path: '/',
                element: <Lobbies/>
            },
            {
                path: '/signup',
                element: <SignupForm/>
            },
            {
                path: '/lobby/:id',
                element: <Lobby/>
            }
        ]
    },
],
    {
        basename: '/fwc'
    }
);

export const AuthContext = createContext<AuthContextValue>(null);

export const LobbyContext = createContext<{lobbyData: ILobby, setLobbyData: (ILobby) => void}>(null);

const App = () => {
    const [isLoginShown, setIsLoginShown] = useState<boolean>(false);
    const [lobbyData, setLobbyData] = useState<ILobby>();
    const [loggedUser, setLoggedUser] = useState<User>(() => Storage_.getUser());
    const storeUser = useCallback( async result => {
        Storage_.setUser(result);
        setLoggedUser(result)
    },[]);



    const authContextValue = useMemo<AuthContextValue>(() => ({
        storeUser,
        setIsLoginShown,
        isLoginShown,
        loggedUser,
        loginCallback: null,
        globalError: null,
        setIsSnackbarOpen: null,
    }), [storeUser, setIsLoginShown, isLoginShown, loggedUser]);

    const lobbyContextValue = {
        lobbyData,
        setLobbyData
    };

    return (
        <>
            <AuthContext.Provider value={authContextValue}>
                <LobbyContext.Provider value={lobbyContextValue}>
                    <RouterProvider router={router}/>
                </LobbyContext.Provider>
            </AuthContext.Provider>
        </>
)
};



export default App
