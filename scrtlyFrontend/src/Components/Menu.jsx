import React, { useEffect } from 'react'

function Menu({ title, menuObject, onViewChange }) {

    useEffect(() => {
        const allLi = document.querySelector(".menuBox ul").querySelectorAll("li")

        function changeMenuActive() {
            allLi.forEach((n) => n.classList.remove("active"))
            this.classList.add("active")

            // Wywołanie onViewChange z odpowiednim widokiem
            const view = this.getAttribute('data-view');
            onViewChange(view);
        }

        allLi.forEach((n) => n.addEventListener("click", changeMenuActive))

        // Czyszczenie event listenerów po odmontowaniu komponentu
        return () => {
            allLi.forEach((n) => n.removeEventListener("click", changeMenuActive))
        };
    }, [onViewChange])
    return (
        <div className='menuBox'>
            <p className='title'>{title}</p>
            <ul>
                {
                    menuObject && menuObject.map((menu) => (
                        <li key={menu.id} data-view={menu.view}>
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