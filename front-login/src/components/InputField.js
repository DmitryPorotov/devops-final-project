import React from "react";
import TextField from "@mui/material/TextField";
import Alert from "@mui/material/Alert";

const InputField = ({
                        labelText,
                        onChange,
                        value,
                        errorMessages,
                        type,
                        name,
                        isAutofocus,
                    }) => {

    if (!type) type = "text";
    if (!value) value = '';

    return (
        <div style={{margin:".5rem 0"}}>
            <TextField
                label={labelText}
                variant='filled'
                onChange={e => onChange(e.target.value)}
                value={value}
                type={type}
                name={name}
                autoFocus={isAutofocus}
            />
            {errorMessages && errorMessages.length > 0 &&
                errorMessages.map((e, i) =>
                    <Alert style={{margin:".2rem 0"}} severity="error" key={i}>{e}</Alert>
                )
            }
        </div>
    )
};

export default InputField;