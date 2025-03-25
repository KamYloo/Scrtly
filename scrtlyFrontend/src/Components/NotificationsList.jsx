import React from 'react'
import {MdCancel} from "react-icons/md";

function NotificationsList({ notifications }) {
    return (
        <div className='notificationsBox'>
            <h5 className='notificationsHeading'>Notifications</h5>
            <hr/>
            <ul className='notificationsList'>
                {notifications && notifications.length > 0 ? (
                    notifications.map((notif, index) => (
                        <li key={index} className='notificationsListItem'>
                            <i><MdCancel /></i>
                            <img src={notif.post.image || ''} alt=""/>
                            <div className='notificationData'>
                                <p>{notif.message}</p>
                                <span>{notif.createdDate ? new Date(notif.createdDate).toLocaleString() : ""}</span>
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