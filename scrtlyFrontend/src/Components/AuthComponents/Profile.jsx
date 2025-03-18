import React, {useEffect, useState} from 'react'
import "../../Styles/Profile.css"
import { ProfileInfo } from './ProfileInfo'
import {useDispatch, useSelector} from "react-redux";
import {getPostsByUser} from "../../Redux/Post/Action.js";
import {BASE_API_URL} from "../../config/api.js";
import {Post} from "../DiscoverComponents/Post.jsx";
import {useParams} from "react-router-dom";
import Spinner from "../Spinner.jsx";
import ErrorAlert from "../ErrorAlert.jsx";

function Profile() {
    const [selectedPost, setSelectedPost] = useState(false)
    const [postDetail, setPostDetail] = useState(null)
    const dispatch = useDispatch()
    const {nickName} = useParams();

    const {post} = useSelector(state => state);

    const togglePost = (post = null) => {
        setPostDetail(post)
        setSelectedPost((prev) => !prev);
    };

    useEffect(() => {
        dispatch(getPostsByUser(nickName))
    }, [dispatch,nickName, post.likedPost, post.createdPost, post.deletedPost])


    if (post.loading) {
        return <Spinner />;
    }
    if (post.error) {
        return <ErrorAlert message={post.error} />;
    }

    return (
        <div className='profileSite'>
            <ProfileInfo  />
            <hr className="lineP"/>
            <div className="posts">
                {post.posts.content.map((item) => (
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