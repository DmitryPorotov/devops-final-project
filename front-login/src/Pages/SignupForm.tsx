import React, {useState} from "react";
import InputField from "../components/InputField";
import BaseLoginSignupForm from "./common/BaseLoginSignupForm";
import {Link} from "react-router-dom";
import Storage_ from "../http/storage";
import {useNavigate} from 'react-router-dom';
import FetchOptionsInterface from "./common/fetch-options.interface";


const SignupForm = () => {
    const navigate = useNavigate();

    const fetchOptions: FetchOptionsInterface = {
        url: '/auth/signup',
        getBody() {
            return JSON.stringify({
                name,
                ...this.getEmailAndPassword()
            });
        },
        onSuccess(result) {
            setNameErrors([]);
            Storage_.setUser(result);
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