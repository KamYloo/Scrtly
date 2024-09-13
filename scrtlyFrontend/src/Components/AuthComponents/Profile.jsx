import React, {useEffect, useState} from 'react'
import "../../Styles/Profile.css"
import { ProfileInfo } from './ProfileInfo'
import {useDispatch, useSelector} from "react-redux";
import {getPostsByUser} from "../../Redux/Post/Action.js";
import {BASE_API_URL} from "../../config/api.js";
import {Post} from "../DiscoverComponents/Post.jsx";
import {useParams} from "react-router-dom";

function Profile() {
    const [selectedPost, setSelectedPost] = useState(false)
    const [postDetail, setPostDetail] = useState(null)
    const dispatch = useDispatch()
    const {userId} = useParams();

    const {post, comment } = useSelector(store => store);

    const togglePost = (post = null) => {
        setPostDetail(post)
        setSelectedPost((prev) => !prev);
    };

    useEffect(() => {
        dispatch(getPostsByUser(userId))
    }, [dispatch,userId, post.likedPost, post.createdPost, post.deletedPost, comment.createdComment,comment.deletedComment])

    return (
        <div className='profileSite'>
            <ProfileInfo  />
            <hr className="lineP"/>
            <div className="posts">
                { post.posts.map((item) => (
                    <div className="post" key={item.id} onClick={() => togglePost(item)}>
                        <img src={`${BASE_API_URL}${item.image}`} alt="" />
                    </div>
                ))}
            </div>
            {selectedPost && <Post post={postDetail} onClose={togglePost} />}
        </div>
    )
}

export { Profile }