fetch("/api/categories")
    .then(response => response.json())
    .then(function (data) {
        fillCategories(data);
    });

function fillCategories(data) {
    let categories = document.getElementById('categoriesBreadcrumb');
    $(categories).append(`<li class="breadcrumb-item"><a href="/">Главная</a></li>`);
    for (let key in data) {
        let item = `<li class="breadcrumb-item active" aria-current="page">Все</li>`;
        let subItem = ``;
        for (let value = 0; value < data[key].length; value += 2) {
            subItem += `<a class="dropdown-item" href="/categories/${data[key][value + 1]}">${data[key][value]}</a> `;
        }
        item += subItem;
        $(siteMenu).append(item);
    }
}