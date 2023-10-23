import React, {useState} from "react";
import InputField from "../components/InputField";
import BaseLoginSignupForm from "./common/BaseLoginSignupForm";
import {Link} from "react-router-dom";


const SignupForm = () => {
    const fetchOptions = {
        url: '/auth/signup',
        getBody() {
            return JSON.stringify({
                name,
                ...getEmailAndPassword.func()
            });
        },
        onSuccess(result) {
            window.sessionStorage.setItem('_token', result.token)
        },
        onFailure(messages) {
            setNameErrors(messages.n);
        }
    };
    const [name, setName] = useState('');
    const [nameErrors, setNameErrors] = useState([]);
    const getEmailAndPassword = {};
    return (
        <BaseLoginSignupForm
            formTitle={"Sign up"}
            buttonText={"Sign up"}
            moreFields={
                <InputField
                    onChange={setName}
                    labelText={'Name'}
                    value={name}
                    name={'name'}
                    errorMessages={nameErrors}
                />
            }
            returnMailAndPassword={getEmailAndPassword}
            fetchOptions={fetchOptions}
            link={<Link to={'/'}>To login</Link>}
        />
    );
};

export default SignupForm;