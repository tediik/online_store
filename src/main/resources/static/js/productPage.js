let categories;

fetch("/api/categories/categories")
    .then(response => response.json())
    .then(function (content) {
        categories = content;
    });

fetch("/api/products/product")
    .then(response => response.json())
    .then(function (data) {
        fillBreadcrumb(data);
    });

function fillBreadcrumb(data) {
    let breadcr = document.getElementById('categoriesBreadcrumb');
    let categoryOfProduct;
    let subcategoryOfProduct;

    top:
    for (let i in categories) {
        let prodInCategory = categories[i].products;
        for (let j in prodInCategory) {
            if (prodInCategory[j].id == data.id) {
                categoryOfProduct = categories[i].superCategory;
                subcategoryOfProduct = categories[i].category;
                break top;
            }
        }
    }

    $(breadcr).append(`<li class="breadcrumb-item"><a href="/">Главная</a></li>`);
    $(breadcr).append(`<li class="breadcrumb-item"><a>${categoryOfProduct}</a></li>`);
    $(breadcr).append(`<li class="breadcrumb-item"><a href="#">${subcategoryOfProduct}</a></li>`);
    $(breadcr).append(`<li class="breadcrumb-item"><a>${data.product}</a></li>`);
}