import Box from "@mui/material/Box";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton";
import React, {useContext} from "react";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import MenuIcon from '@mui/icons-material/Menu';
import {useNavigate} from "react-router-dom";
import Storage from "../http/storage"
import {AuthContext} from "../App";


const HeaderBar = ({sx}) => {
    const navigate =  useNavigate();
    const auth = useContext(AuthContext);

    const handleLogoutClick = () => {
        Storage.deleteUser();
        navigate("/", {replace :true});
    };

    return (
        <Box sx={{ flexGrow: 1 }} style={sx}>
            <AppBar position="static">
                <Toolbar>
                    {/*<IconButton*/}
                    {/*    size="large"*/}
                    {/*    edge="start"*/}
                    {/*    color="inherit"*/}
                    {/*    aria-label="menu"*/}
                    {/*    sx={{ mr: 2 }}*/}
                    {/*>*/}
                    {/*    <MenuIcon />*/}
                    {/*</IconButton>*/}
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        {auth.loggedUser && <>
                            Welcome {auth.loggedUser.name}
                        </>}

                    </Typography>
                    <Button color="inherit" onClick={handleLogoutClick}>Logout</Button>
                </Toolbar>
            </AppBar>
        </Box>
    );
};

export default HeaderBar;