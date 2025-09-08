import {BiSearchAlt} from "react-icons/bi";
import React from "react";
import '../Styles/SearchBox.css'

// eslint-disable-next-line react/prop-types
function SearchBox({onSearchChange, query, placeholder}) {
    return (
        <div className="searchBox" style={{
            width: '260px',
            height:'45px',
        }}>
            <input type="search" value={query} onChange={onSearchChange} placeholder={placeholder}/>
            <i className='searchIcon'><BiSearchAlt/></i>
        </div>
    )
}

export {SearchBox}