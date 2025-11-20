import { useState, useRef, useEffect, useCallback } from 'react';
import Hls from 'hls.js';
import { BASE_API_URL } from "../Redux/api.js";

export const useGaplessAudio = ({
                                    currentManifestUrl,
                                    nextManifestUrl,
                                    volume,
                                    autoPlay,
                                    onTrackEnd
                                }) => {
    const [isPlaying, setIsPlaying] = useState(false);
    const [activePlayer, setActivePlayer] = useState('A'); // 'A' | 'B'
    const [duration, setDuration] = useState(0);

    const audioARef = useRef(null);
    const audioBRef = useRef(null);

    const hlsARef = useRef(null);
    const hlsBRef = useRef(null);

    const loadedUrlRefA = useRef(null);
    const loadedUrlRefB = useRef(null);
    const preloadTriggeredRef = useRef(false);

    const getActiveRefs = useCallback(() => activePlayer === 'A'
        ? { audio: audioARef.current, hlsRef: hlsARef, side: 'A' }
        : { audio: audioBRef.current, hlsRef: hlsBRef, side: 'B' }, [activePlayer]);

    const getInactiveRefs = useCallback(() => activePlayer === 'A'
        ? { audio: audioBRef.current, hlsRef: hlsBRef, side: 'B' }
        : { audio: audioARef.current, hlsRef: hlsARef, side: 'A' }, [activePlayer]);

    const initializeHls = useCallback((audio, hlsRef, manifestUrl, side, shouldAutoPlay = false) => {
        if (!audio || !manifestUrl) return;

        if (side === 'A') loadedUrlRefA.current = manifestUrl;
        else loadedUrlRefB.current = manifestUrl;

        if (hlsRef.current) {
            hlsRef.current.destroy();
        }

        if (Hls.isSupported()) {
            const hls = new Hls({
                maxBufferLength: 30,
                xhrSetup: (xhr) => { xhr.withCredentials = true }
            });
            hlsRef.current = hls;

            if (!shouldAutoPlay) {
                let loadedSegments = 0;
                const SEGMENT_LIMIT = 4;

                const onFragBuffered = () => {
                    loadedSegments++;
                    if (loadedSegments >= SEGMENT_LIMIT) {
                        hls.stopLoad();
                        hls.off(Hls.Events.FRAG_BUFFERED, onFragBuffered);
                    }
                };

                hls.on(Hls.Events.FRAG_BUFFERED, onFragBuffered);
            }

            hls.attachMedia(audio);
            hls.loadSource(`${BASE_API_URL}${manifestUrl}`);

            if (shouldAutoPlay) {
                const onMeta = () => {
                    if (activePlayer === side) setDuration(audio.duration);
                    audio.removeEventListener('loadedmetadata', onMeta);
                };
                audio.addEventListener('loadedmetadata', onMeta);
            }
        } else {
            audio.src = `${BASE_API_URL}${manifestUrl}`;
        }
    }, [activePlayer]);

    useEffect(() => {
        if (!currentManifestUrl) return;
        const { audio, hlsRef, side } = getActiveRefs();

        const currentLoaded = side === 'A' ? loadedUrlRefA.current : loadedUrlRefB.current;

        if (currentLoaded === currentManifestUrl) {
            if (hlsRef.current) {
                hlsRef.current.startLoad();
            }
            if (autoPlay && audio.paused) {
                audio.play().then(() => setIsPlaying(true)).catch(() => {});
            }
            if(!isNaN(audio.duration)) {
                setDuration(audio.duration);
            }
            return;
        }

        preloadTriggeredRef.current = false;
        const inactive = getInactiveRefs();
        if (inactive.audio) {
            inactive.audio.pause();
            inactive.audio.currentTime = 0;
        }

        initializeHls(audio, hlsRef, currentManifestUrl, side, true);

        if (autoPlay) {
            setTimeout(() => {
                audio.play().then(() => setIsPlaying(true)).catch(console.error);
            }, 150);
        }
    }, [currentManifestUrl, activePlayer, autoPlay, getActiveRefs, getInactiveRefs, initializeHls]);

    useEffect(() => {
        if (!nextManifestUrl || preloadTriggeredRef.current) return;

        const checkPreload = () => {
            const { audio } = getActiveRefs();
            if (!audio) return;
            const timeLeft = audio.duration - audio.currentTime;

            if (timeLeft < 15 && timeLeft > 0) {
                preloadTriggeredRef.current = true;
                const { audio: nextAudio, hlsRef: nextHls, side } = getInactiveRefs();

                const loadedInNext = side === 'A' ? loadedUrlRefA.current : loadedUrlRefB.current;

                if (loadedInNext !== nextManifestUrl) {
                    initializeHls(nextAudio, nextHls, nextManifestUrl, side, false);
                }
            }
        };

        const interval = setInterval(checkPreload, 1000);
        return () => clearInterval(interval);
    }, [nextManifestUrl, getActiveRefs, getInactiveRefs, initializeHls]);

    useEffect(() => {
        const handleEnded = () => {
            const { audio: nextAudio, hlsRef: nextHls } = getInactiveRefs();

            if (nextAudio && nextAudio.readyState >= 2) {
                if (nextHls.current) {
                    nextHls.current.startLoad();
                }

                nextAudio.play()
                    .then(() => {
                        setActivePlayer(prev => prev === 'A' ? 'B' : 'A');
                        setIsPlaying(true);
                        if (onTrackEnd) onTrackEnd();
                    })
                    .catch(e => console.error(e));
            } else {
                if (onTrackEnd) onTrackEnd();
            }
        };

        const a = audioARef.current;
        const b = audioBRef.current;
        if (a) a.addEventListener('ended', handleEnded);
        if (b) b.addEventListener('ended', handleEnded);

        return () => {
            if (a) a.removeEventListener('ended', handleEnded);
            if (b) b.removeEventListener('ended', handleEnded);
        };
    }, [activePlayer, getInactiveRefs, onTrackEnd]);

    useEffect(() => {
        if (audioARef.current) audioARef.current.volume = volume;
        if (audioBRef.current) audioBRef.current.volume = volume;
    }, [volume]);

    const togglePlay = () => {
        const { audio } = getActiveRefs();
        if (!audio) return;
        if (isPlaying) {
            audio.pause();
            setIsPlaying(false);
        } else {
            audio.play().then(() => setIsPlaying(true)).catch(console.error);
        }
    };

    const seek = (time) => {
        const { audio } = getActiveRefs();
        if (audio) audio.currentTime = time;
    };

    const getCurrentAudio = () => activePlayer === 'A' ? audioARef.current : audioBRef.current;

    return {
        audioARef,
        audioBRef,
        activePlayer,
        isPlaying,
        duration,
        togglePlay,
        seek,
        getCurrentAudio
    };
};