export const serverIsDeadHandler = (e, auth) => {
    if (e.message?.includes('Fail') && e.message?.includes('fetch')) {
        auth.globalError = 'Server is not responding';
        auth.setIsSnackbarOpen(true);

    }
};