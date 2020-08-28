async function fillCategories() {
    let response = await fetch("/api/categories");
    let content = await response.json();
    let categories = document.getElementById('siteMenu');
    let key

    for (key in content) {
        let category = `
         <ul class="navbar-nav mr-auto menunavbar" aria-orientation="vertical">
            <li class="nav-item dropright">
                <a class="btn btn-outline-light dropdown-toggle dropdownbtn" href="/" id="pc-dropdown" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">key</a>
                <div class="dropdown-menu" aria-labelledby="pc-dropdown">
                    <a class="dropdown-item" href="#">${content[key]}</a>
                    <a class="dropdown-item" href="#">Комплектующие</a>
                    <a class="dropdown-item" href="#">Периферия</a>
                </div>
            </li>
        </ul>
        `;
        $(categories).append(category);
    }
}