# Brief
The project is an implementation of the table game “Game of Thrones”, which gave the project its bootleg name “Fun With Chairs” or FWC for short. It’s a tabletop strategy game for 6 players.
The project consists of a web front end (React) for logging in and starting a game or joining an existing one. The game GUI itself is written in Lua using the Defold game engine. It is compiled into WebAssembly and embedded into the web page. On the back end there is Nest.JS web server which is used for HTTP and WebSockets. The server side of the game logic runs on a separate server process written in Scala3. The Nest.JS server is not concerned with the game logic. Nest.JS only does authentication and sends messages it receives through WebSocket from clients down to the game logic server. The communication between Nest.JS and the game logic server is done through Redis pub/sub. There are 2 types of messages from clients that Nest.JS handles through WebSockets; (1) messages to the game logic server, (2) chat messages to other clients.

# Sub-projects

## web-server
This is the web server to which the users connect. It uses Nest.js with Fastify. It handles login and games browsing via HTTP.
It handles chats and game communication via WebSockets. 

## game-logic-core
This is a worker written in Scala. It calculates the game logic. It communicates with the web-server using Redis pub/sub.

## front-login
This is the login, games browser and chat interfaces built in React.

## front-defold
This is the game GUI build in Defold game engine. The code written in Lua.

## bots-di
This is the bots worker. Currently bots make random moves. In the future I want to train a model in PyTorch to play the game.
That's why this project is written in Python.

## tools
This project contains 2 tools; (1) for initializing the dev environment, it's called _init_, and (2) the project to build the prod environment called _builder_ 
These are written in Python.

# Onboarding

## Requirements

- docker
- docker-compose
- python > 3.7 <= 3.11

## Initialization

1. Run the _./tools/init/init_ script. It should download all dependencies and seed the DB.
2. Run the _test_ script, it will run the docker-compose to start all relevant sub-projects plus Nginx, Redis and MySQL.
3. For Defold development download the Defold studio https://defold.com/download/
4. To see the game GUI inside the web page (and not just in Defold studio) you need to build it using the builder tool
_./tools/builder/build -o defold_. The nGinx server will pick it up after restart.