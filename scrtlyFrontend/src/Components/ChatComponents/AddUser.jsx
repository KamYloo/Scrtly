import React from 'react'

function AddUser() {
    return (
        <div className='addUser'>
            <form>
                <input type="text" placeholder='Username...' name='userName' />
                <button>Search</button>
            </form>
            <div className="user">
                <div className="detail">
                    <img src="#" alt="" />
                    <span>Name Surname</span>
                </div>
                <button>Add User</button>
            </div>
        </div>
    )
}

export { AddUser }