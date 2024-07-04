import Storage_ from "./storage";

class Api {
    static protocol = window.envVars.protocol + ':';
    static baseUrl = '//' + (window.envVars.host ? window.envVars.host : window.location.hostname);
    static port = ':' + window.envVars.port;
    static apiPrefix = '/fwc/api/v1';

    private static async headers(isLoggedIn) {
        const headers = {"Content-Type": "application/json"};
        const user = Storage_.getUser();
        if (isLoggedIn && user) {
            headers['Authorization'] = 'Bearer ' + user.token;
        }
        return headers;
    };

    static async get(url: string, body?: string, isLoggedIn: boolean = true) {
        return Api.doFetch(url, body, 'GET', isLoggedIn);
    }

    static async post(url: string, body?: string, isLoggedIn: boolean = true) {
        return Api.doFetch(url, body, 'POST', isLoggedIn);
    }

    static async delete_(url: string, body?: string, isLoggedIn: boolean = true) {
        return Api.doFetch(url, body, 'DELETE', isLoggedIn);
    }

    static async patch(url: string, body?: string, isLoggedIn: boolean = true) {
        return Api.doFetch(url, body, 'PATCH', isLoggedIn);
    }

    private static async doFetch(url: string, body?: string, method: string = "GET", isLoggedIn: boolean = true): Promise<Response> {
        const init: RequestInit = {
            method,
            headers: await Api.headers(isLoggedIn)
        };
        if (body) init.body = body;

        return await fetch(Api.protocol + Api.baseUrl + Api.port + Api.apiPrefix + url, init);
    }
}

export default Api