const ws = {
    /**
     * @type WebSocket
     */
    webSocket: null,
    isInit: false,
    playerId: -1,
    init(port, url, playerId, token) {
        if (this.isInit) {
            port.postMessage('opened');
            return
        }
        this.isInit = true;
        this.playerId = playerId;

        this.makeSocket(url, token)
    },
    makeSocket(url, token) {
        const errorCb = (message) => {
            console.log(message);
            this.webSocket.removeEventListener('message', messageHandler);
            this.webSocket.removeEventListener('error', errorCb);
            this.webSocket.removeEventListener('close', errorCb);
            this.webSocket = null;
            setTimeout(()=>this.makeSocket(url, token), 1000)
        };
        try {
            this.webSocket = new WebSocket(url + '?token=' + token);
        } catch (e) {
            console.log(e);
            errorCb(e.message || 'Could not connect to web socket.')
        }

        const messageHandler = (message) => {
            const msg = JSON.parse(message.data);
            for (const p of this.ports) {
                if (p.lobbyId == msg.lobbyId || p.lobbyId == msg.gameId) {
                    p.postMessage(msg);
                }
            }
        };
        this.webSocket.addEventListener('message', messageHandler);

        this.webSocket.addEventListener('error', errorCb);

        this.webSocket.addEventListener('close', errorCb);

        this.webSocket.onopen = () => {
            for (const p of this.ports) {
                p.postMessage('opened');
            }
        }
    },
    send(port, obj) {
        obj.userId = this.playerId;
        obj.lobbyId = port.lobbyId;
        this.webSocket.send(JSON.stringify(obj))
    },
    setLobbyId(port, lobbyId) {
        port.lobbyId = lobbyId;
    },
    subscribe(port) {
        this.webSocket.send(
            JSON.stringify({
                userId: this.playerId,
                type: 'chat',
                lobbyId: port.lobbyId,
                body: {
                    to: [],
                    type: 'join',
                    body: ''
                }
            })
        )
    },
    ports: [],
};

onconnect = (e) => {
    console.log('port opened');
    const port = e.ports[0];
    ws.ports.push(port);

    port.onmessage = (e) => {
            ws[e.data.action](port, ...(e.data.args || []))
    };
};
