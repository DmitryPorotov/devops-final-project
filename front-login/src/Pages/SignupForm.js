import React, {useState} from "react";
import InputField from "../components/InputField";
import BaseLoginSignupForm from "./common/BaseLoginSignupForm";
import {Link} from "react-router-dom";


const SignupForm = () => {

    /**
     * @type {{getBody(): string, getEmailAndPassword: function, onFailure(*): void, url: string, onSuccess(*): void}}
     */
    const fetchOptions = {
        url: '/auth/signup',
        getBody() {
            return JSON.stringify({
                name,
                ...this.getEmailAndPassword()
            });
        },
        onSuccess(result) {
            window.sessionStorage.setItem('_token', result.token)
        },
        onFailure(messages) {
            setNameErrors(messages.n);
        },
        getEmailAndPassword: null
    };
    const [name, setName] = useState('');
    const [nameErrors, setNameErrors] = useState([]);
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
            fetchOptions={fetchOptions}
            link={<Link to={'/'}>To login</Link>}
        />
    );
};

export default SignupForm;