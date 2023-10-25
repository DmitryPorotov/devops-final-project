import React, {useState} from "react";
import Modal from "@mui/material/Modal";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import InputField from "./InputField";
import Button from "@mui/material/Button";
import Checkbox from "@mui/material/Checkbox";
import FormControlLabel from "@mui/material/FormControlLabel";

const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 400,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
};


const LobbyEditModal = ({isOpen, oldName, oldPassword, handleClose, updateSettings, errors}) => {

    const [password, setPassword] = useState(oldPassword);
    const [name, setName] = useState(oldName);
    const [deletePassword, setDeletePassword] = useState(false);

    const handleDelPasswordChange = (event) => {
        setDeletePassword(event.target.checked);
    };

    return (
        <Modal
            open={isOpen}
            onClose={handleClose}
        >
            <form onSubmit={(e) => {e.preventDefault(); updateSettings({password, name, deletePassword})}}>
                <Box sx={style}>
                    <Typography id="modal-modal-title" variant="h6" component="h2">
                        Edit lobby
                    </Typography>
                    <InputField
                        value={name}
                        name={'lobby_name'}
                        labelText={'Lobby Name'}
                        onChange={setName}
                        errorMessages={errors.name}
                    >
                    </InputField>
                    <InputField
                        value={password}
                        name={'lobby_password'}
                        type={'password'}
                        labelText={'Password'}
                        onChange={setPassword}
                        errorMessages={errors.password}
                    >
                    </InputField>
                    <div>
                        <FormControlLabel control={
                            <Checkbox aria-label={'delete password'} onChange={(e) => handleDelPasswordChange(e)}/>
                        } label="Delete password" />
                    </div>
                    <Button type={'submit'}>Update</Button>
                </Box>
            </form>
        </Modal>
    );
};


export default LobbyEditModal
