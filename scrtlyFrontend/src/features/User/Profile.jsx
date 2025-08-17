import React, {useCallback, useEffect, useRef, useState} from 'react'
import "../../Styles/Profile.css"
import { ProfileInfo } from './ProfileInfo.jsx'
import {Post} from "../Discover/Post.jsx";
import {useParams} from "react-router-dom";
import Spinner from "../../Components/Spinner.jsx";
import ErrorOverlay from "../../Components/ErrorOverlay.jsx";
import {useGetPostsByUserQuery} from "../../Redux/services/postApi.js";
import throttle from "lodash.throttle";

function Profile() {
    const [selectedPost, setSelectedPost] = useState(false)
    const [postDetail, setPostDetail] = useState(null)

    const size = 9;

    // pagination state
    const [page, setPage] = useState(0);
    const [allPosts, setAllPosts] = useState([]);

    const {nickName} = useParams();
    const {
        data: postsPage = { content: [] },
        isLoading,
        isError,
        error,
        isFetching,
    } = useGetPostsByUserQuery({ nickName, page, size }, {
        skip: !nickName,
    });

    useEffect(() => {
        const newBatch = postsPage.content;
        if (!Array.isArray(newBatch) || newBatch.length === 0) return;

        if (page === 0) {
            setAllPosts(newBatch);
            return;
        }

        setAllPosts(prev => {
            // filter out duplicates
            const toAdd = newBatch.filter(p => !prev.some(x => x.id === p.id));
            if (toAdd.length === 0) return prev;
            return [...prev, ...toAdd];
        });
    }, [postsPage.content, page]);

    const listRef = useRef();
    const onScroll = useCallback(
        throttle(() => {
            const el = listRef.current;
            if (!el || isFetching || postsPage.content.length < size) return;
            if (el.scrollHeight - el.scrollTop - el.clientHeight < 100) {
                setPage(p => p + 1);
            }
        }, 200),
        [isFetching, postsPage.content.length]
    );

    useEffect(() => {
        const el = listRef.current;
        if (!el) return;
        el.addEventListener('scroll', onScroll);
        return () => el.removeEventListener('scroll', onScroll);
    }, [onScroll]);

    const togglePost = (post = null) => {
        setPostDetail(post)
        setSelectedPost((prev) => !prev);
    };

    if (isLoading && page === 0) {
        return <Spinner />;
    }
    if (isError) {
        return <ErrorOverlay error={error} />;
    }

    return (
        <div className='profileSite'>
            <ProfileInfo  />
            <hr className="lineP"/>
            <div className="posts" ref={listRef}>
                {allPosts.map((item) => (
                    <div className="post" key={item.id} onClick={() => togglePost(item)}>
                        <img src={item.image} alt=""/>
                    </div>
                ))}
            </div>
            {selectedPost && <Post post={postDetail} onClose={togglePost}/>}
        </div>
    )
}

export {Profile}