import React, { useState, useEffect, useCallback, useRef } from "react";
import { MdCancel } from "react-icons/md";
import { useDeleteNotificationMutation, useGetNotificationsQuery } from "../../Redux/services/notificationApi.js";
import throttle from "lodash.throttle";
import toast from "react-hot-toast";

export function NotificationsList() {
    const [page, setPage] = useState(0);
    const [allNotifications, setAllNotifications] = useState([]);
    const { data, isLoading, isFetching, isError } = useGetNotificationsQuery(
        { page, size: 10 },
        { skip: false }
    );
    const [deleteNotification] = useDeleteNotificationMutation();
    const containerRef = useRef();

    useEffect(() => {
        if (data?.notifications) {
            setAllNotifications((prev) => {
                if (page === 0) return data.notifications;
                const existingIds = new Set(prev.map(n => n.id));
                const newOnes = data.notifications.filter(n => !existingIds.has(n.id));
                return [...prev, ...newOnes];
            });
        }
    }, [data, page]);

    const onScroll = useCallback(
        throttle(() => {
            const el = containerRef.current;
            if (!el || isFetching || data?.last) return;
            if (el.scrollHeight - el.scrollTop - el.clientHeight < 100) {
                setPage(prev => prev + 1);
            }
        }, 300),
        [isFetching, data?.last]
    );

    useEffect(() => {
        const el = containerRef.current;
        if (!el) return;
        el.addEventListener("scroll", onScroll);
        return () => el.removeEventListener("scroll", onScroll);
    }, [onScroll]);

    const deleteHandler = async (id) => {
        try {
            await deleteNotification(id).unwrap();
            toast.success("Notification deleted");
            setAllNotifications((prev) => prev.filter((n) => n.id !== id));
        } catch (err) {
            toast.error(err?.data?.businessErrornDescription || "Failed to delete");
        }
    };

    if (isError) return <p>Error loading notifications</p>;

    return (
        <div className="notificationsBox">
            <h5 className="notificationsHeading">Notifications</h5>
            <hr />
            <ul className="notificationsList" ref={containerRef}>
                {allNotifications.length > 0 ? (
                    allNotifications.map((notif) => (
                        <li key={notif.id} className="notificationsListItem">
                            <i onClick={() => deleteHandler(notif.id)}>
                                <MdCancel />
                            </i>
                            <img src={notif.post.image || ""} alt="" />
                            <div className="notificationData">
                                <p>{notif.message}</p>
                                <span>
                                    {(() => {
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
                ) : isLoading ? (
                    <p>Loading...</p>
                ) : (
                    <p>No notifications</p>
                )}
            </ul>
            {isFetching && !isLoading && <p>Loading more…</p>}
            {data?.last && <p className="endOfList">You’re all caught up!</p>}
        </div>
    );
}
