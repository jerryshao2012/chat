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
