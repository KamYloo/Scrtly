import React, {useCallback, useEffect, useRef, useState} from 'react';
import throttle from 'lodash/throttle';
import EmojiPicker from 'emoji-picker-react';
import {FaCamera, FaImage, FaInfoCircle, FaMicrophone, FaPhoneAlt} from "react-icons/fa";
import {BsCameraVideoFill, BsEmojiSmileFill} from "react-icons/bs";
import {useDispatch} from "react-redux";
import {formatDistanceToNow} from 'date-fns';
import {Client} from '@stomp/stompjs';
import SockJS from 'sockjs-client/dist/sockjs';
import {useNavigate} from 'react-router-dom';
import defaultAvatar from '../../assets/user.jpg';
import {BASE_API_URL} from '../../Redux/api.js';
import {useGetCurrentUserQuery} from '../../Redux/services/authApi.js';
import {chatMessageApi, useGetMessagesByChatQuery} from '../../Redux/services/chatMessageApi.js';

function Chat({ chat, onBack }) {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const [open, setOpen] = useState(false);
    const [text, setText] = useState("");
    const [stompClient, setStompClient] = useState(null);
    const [editingMessageId, setEditingMessageId] = useState(null);
    const [editingMessageText, setEditingMessageText] = useState("");

    const [page, setPage] = useState(0);
    const [allMessages, setAllMessages] = useState([]);

    const { data: reqUser } = useGetCurrentUserQuery(null, {
        skip: !localStorage.getItem('isLoggedIn'),
    });

    const { data: pageData, isFetching } = useGetMessagesByChatQuery(
        { chatId: chat.id, page },
        { skip: !chat?.id }
    );

    const containerRef = useRef(null);
    const endRef = useRef(null);
    const prevScrollRef = useRef(0);

    useEffect(() => {
        if (!pageData) return;
        const msgs = pageData.content.slice().reverse();

        setAllMessages(prev => {
            const mapById = new Map();
            prev.forEach(m => mapById.set(m.id, m));
            msgs.forEach(m => mapById.set(m.id, m));

            return Array.from(mapById.values())
                .sort((a, b) => new Date(a.createDate) - new Date(b.createDate));
        });
    }, [pageData]);


    useEffect(() => {
        setPage(0);
        setAllMessages([]);
    }, [chat.id]);

    useEffect(() => {
        const c = containerRef.current;
        if (!c) return;
        if (page === 0) {
            endRef.current?.scrollIntoView({ behavior: 'smooth' });
        } else {
            const newHeight = c.scrollHeight;
            c.scrollTop = newHeight - prevScrollRef.current;
        }
    }, [allMessages, page]);

    const handleScroll = useCallback(
        throttle(e => {
            const { scrollTop, scrollHeight } = e.target;
            if (
                scrollTop < 100 &&
                pageData &&
                !pageData.last &&
                !isFetching
            ) {
                prevScrollRef.current = scrollHeight;
                setPage(p => p + 1);
            }
        }, 300),
        [pageData, isFetching]
    );

    const formatTimeAgo = timestamp =>
        formatDistanceToNow(new Date(timestamp), { addSuffix: true });

    const onMessageReceive = ({ body }) => {
        let msg;
        try { msg = JSON.parse(body); } catch { return; }

        dispatch(
            chatMessageApi.util.updateQueryData(
                'getMessagesByChat',
                { chatId: chat.id, page: 0 },
                draft => {
                    switch (msg.status) {
                        case 'EDITED': {
                            const idx = draft.content.findIndex(m => m.id === msg.id);
                            if (idx !== -1) draft.content[idx] = msg;
                            break;
                        }
                        case 'DELETED': {
                            draft.content = draft.content.filter(m => m.id !== msg.id);
                            draft.totalElements -= 1;
                            break;
                        }
                        default: {
                            draft.content.unshift(msg);
                            draft.totalElements += 1;
                        }
                    }
                }
            )
        );

        setAllMessages(prev => {
            const idx = prev.findIndex(m => m.id === msg.id);
            if (msg.status === 'DELETED') return prev.filter(m => m.id !== msg.id);
            if (idx !== -1) {
                const copy = [...prev];
                copy[idx] = msg;
                return copy;
            }
            return [...prev, msg].sort((a, b) => new Date(a.createDate) - new Date(b.createDate));
        });

        const c = containerRef.current;
        if (c && c.scrollHeight - c.scrollTop - c.clientHeight < 50) {
            setTimeout(() => endRef.current?.scrollIntoView({ behavior: 'smooth' }), 100);
        }
    };

    useEffect(() => {
        if (!chat?.id) return;
        const client = new Client({
            webSocketFactory: () => new SockJS(`${BASE_API_URL}/ws`),
            reconnectDelay: 5000,
            onConnect: () => client.subscribe(
                `/exchange/chat.exchange/room.${chat.id}`,
                onMessageReceive
            ),
            onStompError: frame => console.error('STOMP error:', frame.headers['message'], frame.body),
        });
        client.activate();
        setStompClient(client);
        return () => client.deactivate();
    }, [chat.id, dispatch]);

    const handleSend = () => {
        if (!text.trim() || !stompClient?.connected) return;
        stompClient.publish({
            destination: `/app/chat/sendMessage/${chat.id}`,
            body: JSON.stringify({ message: text }),
        });
        setText('');
    };

    const startEditing = msg => {
        setEditingMessageId(msg.id);
        setEditingMessageText(msg.messageText);
    };
    const handleEdit = () => {
        if (!editingMessageText.trim() || !stompClient?.connected) return;
        stompClient.publish({
            destination: `/app/chat/editMessage/${chat.id}`,
            body: JSON.stringify({ id: editingMessageId, message: editingMessageText }),
        });
        setEditingMessageId(null);
        setEditingMessageText('');
    };

    const handleDelete = id => {
        if (!window.confirm('Are you sure you want to delete this message?')) return;
        stompClient.publish({
            destination: `/app/chat/deleteMessage/${chat.id}`,
            body: JSON.stringify({ messageId: id }),
        });
    };

    const otherPerson = chat.participants?.find(u => u.id !== reqUser?.id);

    return (
        <div className="chat">
            <div className="top">
                <div className="user">
                    {onBack && <button className="backBtn" onClick={onBack}>Back</button>}
                    {chat?.participants?.length === 2 ? (
                        <>
                            <img
                                src={otherPerson?.profilePicture || defaultAvatar}
                                alt={otherPerson?.fullName}
                                onClick={() => navigate(`/profile/${otherPerson?.nickName}`)}
                            />
                            <div className="userData">
                                <span>{otherPerson?.fullName}</span>
                                <p>{otherPerson?.description || ''}</p>
                            </div>
                        </>
                    ) : (
                        <div className="userData">
                            <span>{chat.chatRoomName}</span>
                        </div>
                    )}
                </div>
                <div className="icons">
                    <i><FaPhoneAlt /></i>
                    <i><BsCameraVideoFill /></i>
                    <i><FaInfoCircle /></i>
                </div>
            </div>

            <div className="center" ref={containerRef} onScroll={handleScroll}>
                {allMessages.map((item, idx) => (
                    <div
                        key={`${item.id}-${idx}`}
                        className={item.user.id === reqUser?.id ? 'messageOwn' : 'message'}
                    >
                        <img
                            src={item.user.profilePicture || defaultAvatar}
                            alt={item.user.fullName}
                            onClick={() => navigate(`/profile/${item.user.nickName}`)}
                        />
                        <div className="text">
                            <p><strong>{item.user.nickName}:</strong>
                                {editingMessageId === item.id ? (
                                    <input type="text" className="editMessageInput"
                                           value={editingMessageText}
                                           onChange={e => setEditingMessageText(e.target.value)}
                                           onKeyPress={e => e.key === 'Enter' && handleEdit()}/>
                                ) : (
                                    <> {item.messageText}</>
                                )}
                            </p>
                            <div className="info">
                                <span>{item.lastModifiedDate ? formatTimeAgo(item.lastModifiedDate) : formatTimeAgo(item.createDate)}</span>
                                {item.user.id === reqUser?.id && (
                                    <>
                                        {editingMessageId === item.id ? (
                                            <>
                                                <button onClick={handleEdit}>Zapisz</button>
                                                <button onClick={() => setEditingMessageId(null)}>Anuluj</button>
                                            </>
                                        ) : (
                                            <>
                                                <button onClick={() => startEditing(item)}>Edytuj</button>
                                                <button onClick={() => handleDelete(item.id)}>Usuń</button>
                                            </>
                                        )}
                                    </>
                                )}
                            </div>
                        </div>
                    </div>
                ))}
                <div ref={endRef}/>
            </div>

            <div className="bottom">
                <div className="icons">
                    <i><FaImage/></i>
                    <i><FaCamera/></i>
                    <i><FaMicrophone/></i>
                </div>
                <input
                    type="text" placeholder="Write message..."
                    value={text} onChange={e => setText(e.target.value)}
                    onKeyPress={e => e.key === 'Enter' && handleSend()}
                />
                <div className="emoji">
                    <i onClick={() => setOpen(o => !o)}><BsEmojiSmileFill /></i>
                    {open && (
                        <div className="emojiPickerWrapper">
                            <EmojiPicker onEmojiClick={(_, em) => { setText(t => t + em.emoji); setOpen(false); }} />
                        </div>
                    )}
                </div>
                <button className="sendButton" onClick={handleSend}>Send</button>
            </div>
        </div>
    );
}

export { Chat };
