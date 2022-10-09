# Realtime Chat application using Kafka, SpringBoot, ReactJS, and WebSockets - FrontEnd Development in ReactJS
We would create a simple chat page with list of messages and a text field at the bottom of page to send the messages to Kafka backend.

## Create React App
We will use [Create React App](https://github.com/facebook/create-react-app) to quickstart the app.

```![2022-10-08_12-20-18](https://user-images.githubusercontent.com/1479717/194730482-6d80e6d0-0f9d-4639-9e23-7d3ee5299b4d.gif)

npm install --g create-react-app
create-react-app kafka-chat-ui
cd kafka-chat-ui
```

## Install dependencies
* axios
* socketjs
* react-stomp
* material-ui
* moment

```
npm install sockjs-client react-stomp material-ui axios moment
```
You can refer documentation of material-ui [here](https://material-ui.com/getting-started/installation/).

In the project directory, you can run:

```
npm start
```
Runs the app in the development mode.<br />
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.<br />
You will also see any lint errors in the console.

*Copy the CSS style*

Copy the css style from [here](https://raw.githubusercontent.com/jerryshao2012/chat/master/kafka-chat-ui/src/styles/App.css) paste it in the `App.css` file.

Next add the below changes to `App.js`

*App.js*
```javascript
import React, {useState} from 'react';
import SockJsClient from 'react-stomp';
import './styles/App.css';
import Input from './components/Input/Input';
import LoginForm from './components/LoginForm';
import Messages from './components/Messages/Messages';
import chatAPI from './services/chatapi';
import {randomColor} from './utils/common';

const SOCKET_URL = 'http://localhost:8081/ws/v1/chat';

const App = () => {
    const [messages, setMessages] = useState([])
    const [user, setUser] = useState(null)

    let onConnected = () => {
        console.log("Connected...")
    }

    let onDisconnected = () => {
        console.log("Disconnected!")
    }

    let onMessageReceived = (msg) => {
        console.log('New message received', msg);
        setMessages(messages.concat(msg));
    }

    let onSendMessage = (msgText) => {
        chatAPI.sendMessage(user.username, msgText).then(res => {
            console.log('Sent', res);
        }).catch(err => {
            console.log('Error occurred while sending message to api', err);
        })
    }

    let handleLoginSubmit = (username) => {
        console.log(username, " Logged in...");

        setUser({
            username: username,
            color: randomColor()
        })

    }

    return (
        <div className="App">
            {!!user ?
                (
                    <>
                        <SockJsClient
                            url={SOCKET_URL}
                            topics={['/topic/group']}
                            onConnect={onConnected}
                            onDisconnect={onDisconnected}
                            onMessage={msg => onMessageReceived(msg)}
                            debug={true}
                        />
                        <Messages
                            messages={messages}
                            currentUser={user}
                        />
                        <Input onSendMessage={onSendMessage}/>
                    </>
                ) :
                <LoginForm onSubmit={handleLoginSubmit}/>
            }
        </div>
    )
}

export default App;

```
Here we are using **SocketJsClient** from `react-stomp` to connect to the WebSocket.

Alternatively, you can also use SockJS from `sockjs-client` to create a `stompclient` and connect to the WebSocket.

Next, we need to create Messages Child Component which would show the list of messages.

```javascript
import React from 'react'
import Moment from 'moment'

const Messages = ({messages, currentUser}) => {

    let renderMessage = (message) => {
        const {sender, content, timestamp, color} = message;
        const messageFromMe = currentUser.username === message.sender;
        const className = messageFromMe ? "Messages-message currentUser" : "Messages-message";
        return (
            <li className={className} key={message.timestamp}>
                <span
                    className="avatar"
                    style={{backgroundColor: color}}
                />
                <div className="Message-content">
                    <div className="table">
                        <div className="username">
                            {sender}
                        </div>
                        <div className="timestamp">
                            {Moment(timestamp).format('YYYY-MM-DD HH:mm:ss')}
                        </div>
                    </div>
                    <div className="text">{content}</div>
                </div>
            </li>
        );
    };

    return (
        <ul className="messages-list">
            {messages.map(msg => renderMessage(msg))}
        </ul>
    )
}

export default Messages
``` 

*LoginForm.js*

```javascript
import React, {useState} from 'react';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';

const LoginForm = ({onSubmit}) => {

    const [username, setUsername] = useState("");
    let handleUserNameChange = event => setUsername(event.target.value);

    let handleSubmit = () => {
        onSubmit(username);
    }

    return (
        <div>
            <TextField
                label="Type your username"
                placeholder="Username"
                onChange={handleUserNameChange}
                margin="normal"
                onKeyDown={event => {
                    if (event.key === 'Enter') {
                        handleSubmit();
                    }
                }}
            />
            <br/>
            <Button variant="contained" color="primary" onClick={handleSubmit}>
                Login
            </Button>

        </div>
    )
}

export default LoginForm
```
## Final Result

Open the application in multiple windows and send a message in one window.All the other browser window should show the sent messages.

![Demo](https://user-images.githubusercontent.com/1479717/194717492-e6b3092f-662a-41b3-a91c-c44cfdf9b299.gif)

We are using SockJS to listen to the messages, which are sent from the server-side WebSocket.

### SourceCode

You can find the complete source code in my [Github](https://github.com/jerryshao2012/chat) page.

## Other Available Scripts

### `npm test`

Launches the test runner in the interactive watch mode.<br />
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder.<br />
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.<br />
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can’t go back!**

If you aren’t satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point you’re on your own.

You don’t have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn’t feel obligated to use this feature. However we understand that this tool wouldn’t be useful if you couldn’t customize it when you are ready for it.

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).

### Code Splitting

This section has moved here: https://facebook.github.io/create-react-app/docs/code-splitting

### Analyzing the Bundle Size

This section has moved here: https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size

### Making a Progressive Web App

This section has moved here: https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app

### Advanced Configuration

This section has moved here: https://facebook.github.io/create-react-app/docs/advanced-configuration

### Deployment

This section has moved here: https://facebook.github.io/create-react-app/docs/deployment

### `npm run build` fails to minify

This section has moved here: https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify
