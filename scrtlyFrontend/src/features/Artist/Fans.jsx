import React, {useCallback, useEffect, useRef, useState} from 'react'
import { FaUser } from "react-icons/fa";
import Verification from "../../assets/check.png";
import {useNavigate} from "react-router-dom";
import defaultAvatar from "../../assets/user.jpg";
import throttle from "lodash.throttle";
import {useGetArtistFansQuery} from "../../Redux/services/artistApi.js";
import {debounce} from "lodash";
import {SearchBox} from "../../Components/SearchBox.jsx";


// eslint-disable-next-line react/prop-types
function Fans({artistId}) {
    const navigate = useNavigate();

    const size = 9;
    const [page, setPage] = useState(0);
    const [fans, setFans] = useState([]);
    const seenIdsRef = useRef(new Set());
    const [totalPages, setTotalPages] = useState(undefined);

    const [query, setQuery] = useState('');
    const [debouncedQuery, setDebouncedQuery] = useState('');

    const debouncerRef = useRef(null);
    useEffect(() => {
        debouncerRef.current = debounce((q) => {
            // reset pagination and list when query changes
            setDebouncedQuery(q);
            setPage(0);
            setFans([]);
            seenIdsRef.current = new Set();
            setTotalPages(undefined);
        }, 400);
        return () => {
            if (debouncerRef.current && debouncerRef.current.cancel) debouncerRef.current.cancel();
        };
    }, []);

    const { data, isFetching } = useGetArtistFansQuery(
        { artistId, page, size, query: debouncedQuery },
        { skip: !artistId }
    );

    useEffect(() => {
        setPage(0);
        setFans([]);
        seenIdsRef.current = new Set();
        setTotalPages(undefined);
        setQuery('');
        setDebouncedQuery('');
    }, [artistId]);

    useEffect(() => {
        if (!data) return;

        const payload = data;
        const incoming = Array.isArray(payload.content) ? payload.content : [];

        if (typeof payload.totalPages === "number") setTotalPages(payload.totalPages);

        if (incoming.length === 0) return;

        setFans(prev => {
            if (page === 0) {
                const next = [];
                const seen = new Set();
                for (const f of incoming) {
                    if (f && f.id != null && !seen.has(f.id)) {
                        next.push(f);
                        seen.add(f.id);
                    }
                }
                seenIdsRef.current = new Set([...seen]);
                return next;
            }

            const next = [...prev];
            const seen = seenIdsRef.current;
            for (const f of incoming) {
                if (f && f.id != null && !seen.has(f.id)) {
                    next.push(f);
                    seen.add(f.id);
                }
            }
            seenIdsRef.current = seen;
            return next;
        });
    }, [data, page]);

    const handleScroll = useCallback(
        throttle(() => {
            if (isFetching || !artistId) return;
            if (typeof totalPages === "number" && page >= totalPages - 1) return;

            const scrollY = window.scrollY || window.pageYOffset;
            const innerHeight = window.innerHeight;
            const docHeight = document.documentElement.offsetHeight;

            if (innerHeight + scrollY >= docHeight - 300) {
                setPage(p => p + 1);
            }
        }, 300),
        [isFetching, page, totalPages, artistId]
    );

    useEffect(() => {
        window.addEventListener("scroll", handleScroll);
        return () => {
            if (handleScroll.cancel) handleScroll.cancel();
            window.removeEventListener("scroll", handleScroll);
        };
    }, [handleScroll]);

    const onSearchChange = (e) => {
        const v = e.target.value;
        setQuery(v);
        if (debouncerRef.current) debouncerRef.current(v);
    };

    return (
        <div className='fans'>
            <SearchBox onSearchChange={onSearchChange} value={query} placeholder={"Search fans..."} />
            <div className="users">
                {fans?.map((item) => (
                    <div className="user" key={item.id} onClick={() => navigate(`/profile/${item.nickName}`)}>
                        <i className="push"><FaUser/></i>
                        <div className="imgPic">
                            <img src={item?.profilePicture || defaultAvatar} alt=""/>
                            <img className='check' src={Verification} alt=""/>
                        </div>
                        <p>{item?.fullName}</p>
                    </div>
                ))}
            </div>
        </div>
    )
}

export {Fans}