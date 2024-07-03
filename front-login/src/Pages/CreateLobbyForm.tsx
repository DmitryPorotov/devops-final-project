import {createPortal} from "react-dom";
import React, {useState} from "react";
import InputField from "../components/InputField";
import Button from "@mui/material/Button";
import Alert from "@mui/material/Alert";
import Card from "@mui/material/Card";
import Api from "../http/api";

const CreateLobbyForm = ({onSuccess}) => {

    const [name, setName] = useState('');
    const [password, setPassword] = useState('');

    const [nameErrors, setNameErrors] = useState([]);
    const [passwordErrors, setPasswordErrors] = useState([]);
    const [globalErrors, setGlobalErrors] = useState([]);

    const handleSubmit = async (event) => {
        event.preventDefault();
        event.stopPropagation();

        try {
            const payload: {
                name: string,
                password?: string
            } = {name};
            if (password) {
                payload.password = password
            }
            const response = await Api.post('/lobby', JSON.stringify(payload));
            const result = await response.json();

            const statusCode = '' + result.statusCode;
            if (statusCode.startsWith('4') || statusCode.startsWith('5')) {
                setNameErrors([]);
                setPasswordErrors([]);
                setGlobalErrors([]);
                if ('string' === typeof result.message) {
                    setGlobalErrors([result.message]);
                }
                else {
                    setNameErrors(result.message.name);
                    setPasswordErrors(result.message.password);
                }
            } else {
                setGlobalErrors([]);
                setPasswordErrors([]);
                onSuccess(result);
            }
        } catch (e) {
            setGlobalErrors(['Failed to call the server.']);
        }
    };

    return createPortal(
        <div style={{
            zIndex:"10",
            backgroundColor:"rgba(200,200,200,.3)",
            top:0,right:0,left:0,bottom:0, position:"fixed",
            display:"flex",
            justifyContent:"center",
            alignItems:"center"
        }}>
            <Card variant="outlined" style={{maxWidth: "25rem",padding:"3%"}}>
                <form className={'the-form'} onSubmit={handleSubmit}>
                    <h2>Create new lobby</h2>
                    <InputField
                        labelText={'Lobby name'}
                        onChange={setName}
                        value={name}
                        name={"name"}
                        errorMessages={nameErrors}
                    />
                    <InputField
                        onChange={setPassword}
                        value={password}
                        labelText={'Password'}
                        name={'password'}
                        errorMessages={passwordErrors}
                        type={"password"}
                    />
                    <div className={'form-field'}>
                        <Button variant='contained' type={'submit'}>Create</Button>
                    </div>
                    {
                        globalErrors && globalErrors.length > 0 &&
                        globalErrors.map((e, i) =>
                            <Alert key={i} severity={'error'}>{e}</Alert>
                        )
                    }
                </form>
            </Card>
        </div>
        , window.document.body
    );
};


export default CreateLobbyForm;