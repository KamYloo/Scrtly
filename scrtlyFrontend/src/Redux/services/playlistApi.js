import { apiSlice } from "../apiSlice.js";

export const playlistApi = apiSlice.injectEndpoints({
    endpoints: (builder) => ({
        createPlaylist: builder.mutation({
            query: (formData) => ({
                url: "/playLists/create",
                method: "POST",
                body: formData,
                credentials: "include",
            }),
            async onQueryStarted(formData, { dispatch, queryFulfilled }) {
                const temp = {
                    id: -1,
                    title: formData.get("title"),
                    coverImage: "",
                    tracksCount: 0,
                };
                const patch = dispatch(
                    apiSlice.util.updateQueryData(
                        "getUserPlaylists",
                        { page: 0, size: 10 },
                        (draft) => {
                            draft.content.unshift(temp);
                            draft.totalElements += 1;
                        }
                    )
                );
                try {
                    const { data: newPl } = await queryFulfilled;
                    dispatch(
                        apiSlice.util.updateQueryData(
                            "getUserPlaylists",
                            { page: 0, size: 10 },
                            (draft) => {
                                const idx = draft.content.findIndex((pl) => pl.id === -1);
                                if (idx !== -1) draft.content[idx] = newPl;
                            }
                        )
                    );
                } catch {
                    patch.undo();
                }
            },
        }),

        getUserPlaylists: builder.query({
            query: ({ page = 0, size = 10 } = {}) => ({
                url: `/playLists/user?page=${page}&size=${size}`,
                method: "GET",
                credentials: "include",
            }),
            transformResponse: (response) => response,
            providesTags: (response) =>
                response.content
                    .map((pl) => ({ type: "Playlist", id: pl.id }))
                    .concat([{ type: "Playlist", id: "LIST" }]),
        }),

        getPlaylist: builder.query({
            query: (id) => ({
                url: `/playLists/${id}`,
                method: "GET",
                credentials: "include",
            }),
            transformResponse: (response) => response,
            providesTags: (result, error, id) => [{ type: "Playlist", id }],
        }),

        getPlaylistTracks: builder.query({
            query: ({ playListId, page = 0, size = 10 }) => ({
                url: `/playLists/${playListId}/tracks?page=${page}&size=${size}`,
                method: "GET",
                credentials: "include",
            }),
            transformResponse: (response) => response.content,
            providesTags: (tracks = [], error, { playListId }) =>
                tracks
                    .map((t) => ({ type: "Track", id: t.id }))
                    .concat([{ type: "Track", id: `PLAYLIST_${playListId}` }]),
        }),

        uploadSongToPlaylist: builder.mutation({
            query: ({ playListId, songId }) => ({
                url: `/playLists/${playListId}/addSong/${songId}`,
                method: "PUT",
                credentials: "include",
            }),
            async onQueryStarted({ playListId, songId }, { dispatch, queryFulfilled }) {
                const patchTracks = dispatch(
                    apiSlice.util.updateQueryData(
                        "getPlaylistTracks",
                        { playListId, page: 0, size: 10 },
                        (draft) => {
                            draft.unshift({ id: songId });
                        }
                    )
                );
                const patchInfo = dispatch(
                    apiSlice.util.updateQueryData(
                        "getPlaylist",
                        playListId,
                        (draft) => {
                            draft.tracksCount += 1;
                        }
                    )
                );
                try {
                    const { data: updated } = await queryFulfilled;
                    dispatch(
                        apiSlice.util.updateQueryData(
                            "getPlaylist",
                            playListId,
                            (draft) => {
                                draft.tracksCount = updated.tracksCount;
                                draft.totalDuration = updated.totalDuration;
                            }
                        )
                    );
                } catch {
                    patchTracks.undo();
                    patchInfo.undo();
                }
            },
        }),

        deleteSongFromPlaylist: builder.mutation({
            query: ({ playListId, songId }) => ({
                url: `/playLists/${playListId}/deleteSong/${songId}`,
                method: "DELETE",
                credentials: "include",
            }),
            async onQueryStarted({ playListId, songId }, { dispatch, queryFulfilled }) {
                const patchTracks = dispatch(
                    apiSlice.util.updateQueryData(
                        "getPlaylistTracks",
                        { playListId, page: 0, size: 10 },
                        draft => draft.filter(s => s.id !== songId)
                    )
                );
                try {
                    await queryFulfilled;
                } catch {
                    patchTracks.undo();
                }
            },
        }),

        updatePlaylist: builder.mutation({
            query: (formData) => ({
                url: "/playLists/update",
                method: "PUT",
                body: formData,
                credentials: "include",
            }),
            async onQueryStarted(formData, { dispatch, queryFulfilled }) {
                const id = formData.get("playListId");
                const patchList = dispatch(
                    apiSlice.util.updateQueryData(
                        "getUserPlaylists",
                        { page: 0, size: 10 },
                        (draft) => {
                            const pl = draft.content.find((p) => p.id === +id);
                            if (pl) pl.title = formData.get("title");
                        }
                    )
                );
                const patchSingle = dispatch(
                    apiSlice.util.updateQueryData("getPlaylist", id, (draft) => {
                        draft.title = formData.get("title");
                    })
                );
                try {
                    await queryFulfilled;
                } catch {
                    patchList.undo();
                    patchSingle.undo();
                }
            },
        }),

        deletePlaylist: builder.mutation({
            query: (id) => ({
                url: `/playLists/delete/${id}`,
                method: "DELETE",
                credentials: "include",
            }),
            async onQueryStarted(id, { dispatch, queryFulfilled }) {
                const patchList = dispatch(
                    apiSlice.util.updateQueryData(
                        "getUserPlaylists",
                        { page: 0, size: 10 },
                        (draft) => {
                            draft.content = draft.content.filter((pl) => pl.id !== id);
                            draft.totalElements -= 1;
                        }
                    )
                );
                try {
                    await queryFulfilled;
                } catch {
                    patchList.undo();
                }
            },
        }),
    }),
    overrideExisting: false,
});

export const {
    useCreatePlaylistMutation,
    useGetUserPlaylistsQuery,
    useGetPlaylistQuery,
    useGetPlaylistTracksQuery,
    useUploadSongToPlaylistMutation,
    useDeleteSongFromPlaylistMutation,
    useUpdatePlaylistMutation,
    useDeletePlaylistMutation,
} = playlistApi;
