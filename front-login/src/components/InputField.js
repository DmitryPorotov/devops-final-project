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
                    }) => {

    if (!type) type = "text";
    if (!value) value = '';

    return (
        <div className={'form-field'}>
            <TextField
                label={labelText}
                variant='filled'
                onChange={e => onChange(e.target.value)}
                value={value}
                type={type}
                name={name}
            />
            {errorMessages && errorMessages.length > 0 &&
                errorMessages.map((e, i) =>
                    <Alert severity="error" key={i}>{e}</Alert>
                )
            }
        </div>
    )
};

export default InputField;