import {apiSlice} from "../apiSlice.js";

export const albumApi = apiSlice.injectEndpoints({
    endpoints: builder => ({
        createAlbum: builder.mutation({
            query: formData => ({
                url: "/album/create",
                method: "POST",
                body: formData,
            }),
            invalidatesTags: ["Album"],
        }),

        getAllAlbums: builder.query({
            query: ({ page = 0, size = 9 } = {}) =>
                `/album/all?page=${page}&size=${size}`,
            // return only the `content` array
            transformResponse: response => response.content,
            providesTags: (albums = [], err, { page, size }) =>
                albums.map(a => ({ type: "Album", id: a.id }))
                    .concat([{ type: "Album", id: "LIST" }]),
        }),

        getAlbum: builder.query({
            query: albumId => `/album/${albumId}`,
            providesTags: (result, error, albumId) => [{ type: "Album", id: albumId }],
        }),

        getAlbumTracks: builder.query({
            query: albumId => `/album/${albumId}/tracks`,
            providesTags: (result, error, albumId) =>
                result
                    ? [
                        ...result.map(track => ({ type: "Track", id: track.id })),
                        { type: "Track", id: `ALBUM_${albumId}` },
                    ]
                    : [{ type: "Track", id: `ALBUM_${albumId}` }],
        }),

        getArtistAlbums: builder.query({
            query: ({ artistId, page = 0, size = 9, query = '' }) => {
                const q = query ? `&query=${encodeURIComponent(query)}` : '';
                return `/album/artist/${artistId}?page=${page}&size=${size}${q}`;
            },
            providesTags: (result, error, arg) => {
                const artistId = arg?.artistId;
                if (!result || !Array.isArray(result.content)) {
                    return [{ type: "Album", id: `ARTIST_${artistId}` }];
                }
                const tags = result.content.map(({ id }) => ({ type: "Album", id }));
                tags.push({ type: "Album", id: `ARTIST_${artistId}` });
                return tags;
            },
        }),

        uploadSong: builder.mutation({
            query: ({ formData }) => ({
                url: "/song/upload",
                method: "POST",
                body: formData,
            }),
            invalidatesTags: (result) => {
                if (!result) return [];
                const albumId = result.album.id;
                return [
                    { type: "Track", id: `ALBUM_${albumId}` },
                    { type: "Album", id: albumId },
                ];
            },
        }),

        deleteAlbum: builder.mutation({
            query: albumId => ({
                url: `/album/delete/${albumId}`,
                method: "DELETE",
            }),
            invalidatesTags: (result, error, albumId) => [
                { type: "Album", id: albumId },
                { type: "Album", id: "LIST" },
            ],
        }),
    }),
    overrideExisting: false,
});

export const {
    useCreateAlbumMutation,
    useGetAllAlbumsQuery,
    useGetAlbumQuery,
    useGetAlbumTracksQuery,
    useGetArtistAlbumsQuery,
    useUploadSongMutation,
    useDeleteAlbumMutation,
} = albumApi;