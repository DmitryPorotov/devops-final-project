/**
 * @typedef User
 * @type {object}
 * @property {number} id
 * @property {string} name
 * @property {string} token
 * @property {string} email
 */


class Storage {
    /**
     *
     * @returns {Promise<User|any>}
     */
    static getUser() {
        const userStr = window.localStorage.getItem('_user');
        if (userStr) {
            return JSON.parse(userStr);
        }
        return null;
    }

    /**
     * @param {User} user
     * @returns {Promise<void>}
     */
    static setUser(user) {
        window.localStorage.setItem('_user', JSON.stringify(user));
    }

    /**
     * @param {number} lobbyId
     * @param {string} house
     * @returns {Promise<void>}
     */
    static setHouseForLobby(lobbyId, house) {
        window.localStorage.setItem(`_lobby${lobbyId}house`, house);
    }

    static deleteUser() {
        window.localStorage.removeItem("_user");
    }
}

export default Storage