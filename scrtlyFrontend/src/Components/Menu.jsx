import React, { useEffect } from 'react'

function Menu({ title, menuObject }) {

    useEffect(() => {
        const allLi = document.querySelector(".menuBox ul").querySelectorAll("li")

        function changeManeuActive() {
            allLi.forEach((n) => n.classList.remove("active"))
            this.classList.add("active")
        }

        allLi.forEach((n) => n.addEventListener("click", changeManeuActive))
    }, [])

    return (
        <div className='menuBox'>
            <p className='title'>{title}</p>
            <ul>
                {
                    menuObject && menuObject.map((menu) => (
                        <li key={menu.id}>
                            <a href="#">
                                <i>{menu.icon}</i>
                                <span>{menu.name}</span>
                            </a>
                        </li>
                    ))
                }
            </ul>
        </div>
    )
}

export { Menu }