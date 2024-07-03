import React, {createContext, useCallback, useContext, useMemo, useState} from "react";
import LoginForm from "./Pages/LoginForm";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import SignupForm from "./Pages/SignupForm";
import Lobbies from "./Pages/Lobbies";
import Lobby from "./Pages/Lobby";
import {Outlet} from "react-router";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";
import websocket from "./http/websocket";
import Storage from "./http/storage";
import HeaderBar from "./components/HeaderBar";

const RoutesWrapper = () => {
    const auth = useContext(AuthContext);
    const [isSnackbarOpen, setIsSnackbarOpen] = useState(false);
    auth.setIsSnackbarOpen = setIsSnackbarOpen;

    const handleSnackbarClose = (event, reason) => {
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

export const AuthContext = createContext({});

export const WsContext = createContext({});

const App = () => {
    const [isLoginShown, setIsLoginShown] = useState(false);
    const [lobbyData, setLobbyData] = useState();
    const [loggedUser, setLoggedUser] = useState(() => Storage.getUser());
    const storeUser = useCallback( async result => {
        Storage.setUser(result);
        setLoggedUser(result)
    },[]);



    const authContextValue = useMemo(() => ({
        storeUser,
        setIsLoginShown,
        isLoginShown,
        loggedUser,
        loginCallback: null,
        globalError: null,
        setIsSnackbarOpen: null,
    }), [storeUser, setIsLoginShown, isLoginShown, loggedUser]);

    const wsContextValue = {
        websocket,
        lobbyData,
        setLobbyData
    };

    return (
        <>
            <AuthContext.Provider value={authContextValue}>
                <WsContext.Provider value={wsContextValue}>
                    <RouterProvider router={router}/>
                </WsContext.Provider>
            </AuthContext.Provider>
        </>
)
};



export default App
