fetch("/api/categories")
    .then(response => response.json())
    .then(function (data) {
        fillCategories(data);
    });

function fillCategories(data) {
    let siteMenu = document.getElementById('siteMenu');
    for (let key in data) {
        let item = `<li class="nav-item dropright">
                <a class="btn btn-outline-light dropdown-toggle text-black-50 font-weight-bolder dropdownbtn" href=${key} id="dropdownMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${key}</a>
                <div class="dropdown-menu" aria-labelledby="dropdownMenu">`;
        let subItem = ``;
        for (let value of data[key]) {
            subItem += `<a class="dropdown-item" href=${value}>${value}</a> `;
        }
        item += subItem;
        $(siteMenu).append(item);
    }
}