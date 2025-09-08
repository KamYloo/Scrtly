import React, {useCallback, useEffect, useRef, useState} from 'react'
import {FaCirclePlay} from "react-icons/fa6";
import "../../Styles/AlbumsView&&ArtistsView.css"
import {useNavigate, useParams} from "react-router-dom";
import {useGetArtistAlbumsQuery} from "../../Redux/services/albumApi.js";
import Spinner from "../../Components/Spinner.jsx";
import ErrorOverlay from "../../Components/ErrorOverlay.jsx";
import throttle from "lodash.throttle";
import {debounce} from "lodash";
import {SearchBox} from "../../Components/SearchBox.jsx";

function ArtistAlbums() {
    const { artistId } = useParams();
    const navigate = useNavigate();

    const size = 9;
    const [page, setPage] = useState(0);
    const [albums, setAlbums] = useState([]);
    const seenIdsRef = useRef(new Set());
    const [totalPages, setTotalPages] = useState(undefined);

    const [query, setQuery] = useState('');
    const [debouncedQuery, setDebouncedQuery] = useState('');

    const debouncerRef = useRef(null);
    useEffect(() => {
        debouncerRef.current = debounce((q) => {
            setDebouncedQuery(q);
            setPage(0);
            setAlbums([]);
            seenIdsRef.current = new Set();
            setTotalPages(undefined);
        }, 400);
        return () => {
            if (debouncerRef.current && debouncerRef.current.cancel) debouncerRef.current.cancel();
        };
    }, []);

    const { data, isFetching, isLoading, isError, error } = useGetArtistAlbumsQuery(
        { artistId, page, size, query: debouncedQuery },
        { skip: !artistId }
    );

    useEffect(() => {
        setPage(0);
        setAlbums([]);
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

        setAlbums(prev => {
            if (page === 0) {
                const next = [];
                const seen = new Set();
                for (const a of incoming) {
                    if (a && a.id != null && !seen.has(a.id)) {
                        next.push(a);
                        seen.add(a.id);
                    }
                }
                seenIdsRef.current = new Set([...seen]);
                return next;
            }

            const next = [...prev];
            const seen = seenIdsRef.current;
            for (const a of incoming) {
                if (a && a.id != null && !seen.has(a.id)) {
                    next.push(a);
                    seen.add(a.id);
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

    if (isLoading) {
        return <Spinner />;
    }

    if (isError) {
        return <ErrorOverlay error={error} />;
    }

    return (
        <div className='artistAlbums'>
            <SearchBox onSearchChange={onSearchChange} value={query} placeholder={"Search albums..."} />
            <div className="albums">
                { albums.map((item) => (
                    <div className="album" key={item.id} onClick={() => navigate(`/album/${item.id}`)}>
                        <i className="play"><FaCirclePlay/></i>
                        <img src={item?.albumImage} alt=""/>
                        <span>{item?.artist.pseudonym}</span>
                        <p>{item?.title}</p>
                    </div>
                ))}
            </div>
        </div>
    )
}

export {ArtistAlbums}