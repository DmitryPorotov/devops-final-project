
class Api {
    static protocol = 'http:';
    static baseUrl = '//localhost:3001';
    static apiPrefix = '/api/v1';

    static headers(isLoggedIn){
        const headers = {"Content-Type": "application/json"};
        if (isLoggedIn && window.localStorage.getItem('_user')) {
            headers['Authorization'] = 'Bearer ' + JSON.parse(window.localStorage.getItem('_user')).token;
        }
        return headers;
    };

    /**
     *
     * @param {string} url
     * @param {?string=} body
     * @param {boolean=} isLoggedIn
     */
    static get(url, body, isLoggedIn = true) {
        return Api.doFetch(url, body, 'GET', isLoggedIn);
    }

    /**
     *
     * @param {string} url
     * @param {?string=} body
     * @param {boolean=} isLoggedIn
     */
    static post(url, body, isLoggedIn = true) {
        return Api.doFetch(url, body, 'POST', isLoggedIn);
    }
    /**
     *
     * @param {string} url
     * @param {?string=} body
     * @param {boolean=} isLoggedIn
     */
    static delete_(url, body, isLoggedIn = true) {
        return Api.doFetch(url, body, 'DELETE', isLoggedIn);
    }

    /**
     *
     * @param {string} url
     * @param {?string=} body
     * @param {boolean=} isLoggedIn
     */
    static patch(url, body, isLoggedIn = true) {
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
            headers: Api.headers(isLoggedIn)
        };
        if (body) init.body = body;
        const response = await fetch(Api.protocol + Api.baseUrl + Api.apiPrefix + url, init);

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