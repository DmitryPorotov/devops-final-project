import Storage from "./storage";


class Api {
    static protocol = window.envVars.protocol + ':';
    static baseUrl = '//' + (window.envVars.host ? window.envVars.host : window.location.hostname);
    static port = ':' + window.envVars.port;
    static apiPrefix = '/fwc/api/v1';

    static async headers(isLoggedIn){
        const headers = {"Content-Type": "application/json"};
        const user = Storage.getUser();
        if (isLoggedIn && user) {
            headers['Authorization'] = 'Bearer ' + user.token;
        }
        return headers;
    };

    /**
     *
     * @param {string} url
     * @param {?string=} body
     * @param {boolean=} isLoggedIn
     */
    static async get(url, body, isLoggedIn = true) {
        return Api.doFetch(url, body, 'GET', isLoggedIn);
    }

    /**
     *
     * @param {string} url
     * @param {?string=} body
     * @param {boolean=} isLoggedIn
     */
    static async post(url, body, isLoggedIn = true) {
        return Api.doFetch(url, body, 'POST', isLoggedIn);
    }
    /**
     *
     * @param {string} url
     * @param {?string=} body
     * @param {boolean=} isLoggedIn
     */
    static async delete_(url, body, isLoggedIn = true) {
        return Api.doFetch(url, body, 'DELETE', isLoggedIn);
    }

    /**
     *
     * @param {string} url
     * @param {?string=} body
     * @param {boolean=} isLoggedIn
     */
    static async patch(url, body, isLoggedIn = true) {
        return Api.doFetch(url, body, 'PATCH', isLoggedIn);
    }

    /**
     *
     * @param {string} url
     * @param {?string} body
     * @param {string} method
     * @param {boolean=} isLoggedIn
     */
    static async doFetch(url, body, method, isLoggedIn = true) {
        const init = {
            method,
            headers: await Api.headers(isLoggedIn)
        };
        if (body) init.body = body;
        const response = await fetch(Api.protocol + Api.baseUrl + Api.port + Api.apiPrefix + url, init);

        // if (response.status === 401) {
        //     // const callbackPtr = {func: null};
        //     const event = new CustomEvent('needs_login');
        //     // event.callbackPtr = callbackPtr;
        //     window.document.body.dispatchEvent(event);
        //     // await callbackPtr.func();
        //     // return await fetch(Api.baseUrl + url, init);
        // }

        return response;
    }
}

export default Api