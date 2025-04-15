import React from 'react'

function AboutArtist({artistBio}) {
    return (
        <div className='aboutArtist'>
            <p>{artistBio}</p>
        </div>
    )
}

export {AboutArtist}