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