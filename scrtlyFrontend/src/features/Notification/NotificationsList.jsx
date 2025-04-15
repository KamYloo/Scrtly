import React from 'react'
import {MdCancel} from "react-icons/md";
import {deleteNotification} from "../../Redux/NotificationService/Action.js";
import {useDispatch} from "react-redux";

function NotificationsList({ notifications }) {
    const dispatch = useDispatch();

    const deleteNotificationHandler = (id) => {
        dispatch(deleteNotification(id));
    };

    return (
        <div className='notificationsBox'>
            <h5 className='notificationsHeading'>Notifications</h5>
            <hr/>
            <ul className='notificationsList'>
                {notifications && notifications.length > 0 ? (
                    notifications.map((notif, index) => (
                        <li key={index} className='notificationsListItem'>
                            <i onClick={() => deleteNotificationHandler(notif.id)}>
                                <MdCancel/>
                            </i>
                            <img src={notif.post.image || ''} alt=""/>
                            <div className='notificationData'>
                                <p>{notif.message}</p>
                                <span>
                                  {notif.createdDate && (() => {
                                      const created = new Date(notif.createdDate);
                                      const updated = notif.updatedDate ? new Date(notif.updatedDate) : null;
                                      return updated && updated.getTime() !== created.getTime()
                                          ? updated.toLocaleString()
                                          : created.toLocaleString();
                                  })()}
                                </span>

                            </div>
                        </li>
                    ))
                ) : (
                    <p>No notifications</p>
                )}

            </ul>
        </div>
    )
}

export {NotificationsList}