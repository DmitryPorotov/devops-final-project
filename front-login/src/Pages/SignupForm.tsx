import React, {useState} from "react";
import InputField from "../components/InputField";
import BaseLoginSignupForm from "./common/BaseLoginSignupForm";
import {Link} from "react-router-dom";
import Storage from "../http/storage";
import {useNavigate} from 'react-router-dom';


const SignupForm = () => {
    const navigate = useNavigate();

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
            setNameErrors([]);
            Storage.setUser(result);
            navigate('/')
        },
        onFailure(messages) {
            setNameErrors(messages.name);
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