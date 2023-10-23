import React, {createContext, useCallback, useContext, useMemo, useState} from "react";
import LoginForm from "./Pages/LoginForm";
import './main.css';
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import SignupForm from "./Pages/SignupForm";
import Lobbies from "./Pages/Lobbies";
import Lobby from "./Pages/Lobby";
import {Outlet} from "react-router";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";

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

]);

export const AuthContext = createContext({});

const App = () => {
    const [isLoginShown, setIsLoginShown] = useState(false);

    const storeUser = useCallback(result => {
        window.sessionStorage.setItem('_user',  JSON.stringify(result));
    },[]);

    const contextValue = useMemo(() => ({
        storeUser,
        setIsLoginShown,
        isLoginShown,
        loginCallback: null,
        globalError: null
    }), [storeUser, setIsLoginShown, isLoginShown]);

    return (
        <>
            <AuthContext.Provider value={contextValue}>
            <h1 >
                Hello! Welcome to Table Games!
            </h1>
            <RouterProvider router={router}/>
            </AuthContext.Provider>
        </>
)
};



export default App