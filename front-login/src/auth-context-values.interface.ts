import User from "./user.interface";

export default interface AuthContextValue {
    storeUser: (User) => Promise<void>,
    setIsLoginShown: (boolean) => void,
    isLoginShown: boolean,
    loggedUser: User,
    loginCallback: () => any,
    globalError: string,
    setIsSnackbarOpen: (boolean) => void,
}