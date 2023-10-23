import React, {useContext} from "react";
import BaseLoginSignupForm from "./common/BaseLoginSignupForm";
import {Link, /*useNavigate*/} from "react-router-dom";
import {AuthContext} from "../App";
import {createPortal} from "react-dom";

const LoginForm = () => {
    // const navigate = useNavigate();
    const auth = useContext(AuthContext);

    const fetchOptions = {
        url: '/auth/login',
        getBody() {
            return JSON.stringify(getEmailAndPassword.func());
        },
        onSuccess(result) {
            auth.storeUser(result);
            if (auth.loginCallback) auth.loginCallback();
        }
    };
    const getEmailAndPassword = {};
    return (
    createPortal(
        <div style={{
            zIndex:"10",
            backgroundColor:"rgba(200,200,200,.3)",
            top:0,right:0,left:0,bottom:0, position:"fixed",
            display:"flex",
            justifyContent:"center",
            alignItems:"center"
        }}>
          <BaseLoginSignupForm
              formTitle={"Login"}
              buttonText={"Login"}
              moreFields={<></>}
              returnMailAndPassword={getEmailAndPassword}
              fetchOptions={fetchOptions}
              link={
                      <Link onClick={()=>auth.setIsLoginShown(false)} to={'/signup'}>To sign up</Link>
                  }
          />
        </div>
    , window.document.body)
    )
};

export default LoginForm;