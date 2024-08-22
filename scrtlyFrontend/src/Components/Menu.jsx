import React, { useEffect } from 'react'
import { Link } from 'react-router-dom';

function Menu({ title, menuObject }) {

    useEffect(() => {
        const allLi = document.querySelector(".menuBox ul").querySelectorAll("li")

        function changeMenuActive() {
            allLi.forEach((n) => n.classList.remove("active"))
            this.classList.add("active")
        }

        allLi.forEach((n) => n.addEventListener("click", changeMenuActive))

        return () => {
            allLi.forEach((n) => n.removeEventListener("click", changeMenuActive))
        };
    }, [])


    return (
        <div className='menuBox'>
            <p className='title'>{title}</p>
            <ul>
                {
                    menuObject && menuObject.map((menu) => (
                        <li key={menu.id} data-view={menu.view}>
                            <Link to={menu.route}>
                                <i>{menu.icon}</i>
                                <span>{menu.name}</span>
                            </Link>
                        </li>
                    ))
                }
            </ul>
        </div>
    )
}

export { Menu }