import React, {useState} from "react";
import Button from "@mui/material/Button";
import InputField from "../../components/InputField";
import Card from "@mui/material/Card";
import Alert from "@mui/material/Alert";
import Api from "../../http/api";

const AdditionalFields = ({children}) =>
    <>{children}</>;

const BaseLoginSignupForm = ({
                       moreFields,
                        link,
                        fetchOptions,
                        returnMailAndPassword,
                        formTitle,
                        buttonText,
                   }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    returnMailAndPassword.func = () => ({email, password});

    const handleSubmit = async (event) => {
        event.preventDefault();
        event.stopPropagation();
        try {
            const response = await Api.post(fetchOptions.url, fetchOptions.getBody());

            const result = await response.json();
            // console.log(result);
            const statusCode = '' + result.statusCode;
            if (statusCode.startsWith('4') || statusCode.startsWith('5')) {
                setEmailErrors([]);
                setPasswordErrors([]);
                setGlobalErrors([]);
                if ('string' === typeof result.message) {
                    setGlobalErrors([result.message]);
                }
                else {
                    setEmailErrors(result.message.email);
                    setPasswordErrors(result.message.password);
                }
                if (fetchOptions.onFailure)
                    fetchOptions.onFailure(result.message)
            } else {
                setGlobalErrors([]);
                setPasswordErrors([]);
                setEmailErrors([]);
                if (fetchOptions.onSuccess)
                    return fetchOptions.onSuccess(result);
            }
        }
        catch (e) {
            setGlobalErrors(['Failed to call the server.']);
            // throw e
        }

    };

    const [emailErrors, setEmailErrors] = useState([]);
    const [passwordErrors, setPasswordErrors] = useState([]);
    const [globalErrors, setGlobalErrors] = useState([]);

    return (
        <Card variant="outlined" style={{maxWidth: "25rem",padding:"3%"}}>
            <form className={'the-form'} onSubmit={handleSubmit}>
                <h2>{formTitle}</h2>
                <InputField
                    labelText={'E-mail'}
                    onChange={setEmail}
                    value={email}
                    name={"email"}
                    errorMessages={emailErrors}
                />
                <InputField
                    onChange={setPassword}
                    value={password}
                    labelText={'Password'}
                    name={'password'}
                    errorMessages={passwordErrors}
                    type={"password"}
                />
                <AdditionalFields>{moreFields}</AdditionalFields>
                <div className={'form-field'}>
                    <Button variant='contained' type={'submit'}>{buttonText}</Button>
                </div>
                {
                    globalErrors && globalErrors.length > 0 &&
                    globalErrors.map((e, i) =>
                        <Alert key={i} severity={'error'}>{e}</Alert>
                    )
                }
            </form>
            <AdditionalFields>
                <div style={{textAlign:"right", margin:".3rem"}}>
                    {link}
                </div>
            </AdditionalFields>
        </Card>
    )
};
export default BaseLoginSignupForm;