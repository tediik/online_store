let categoryNameFromPath = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));

fetch("/api/categories/categoryName", {
    method: "POST",
    body: JSON.stringify(categoryNameFromPath)
});

fetch("/api/categories/category")
    .then(response => response.json())
    .then(function (data) {
        fillBreadcrumb(data);
        fillSomeProducts(data);
    });

function fillBreadcrumb(data) {
    let breadcr = document.getElementById('categoriesBreadcrumb');
    $(breadcr).append(`<li class="breadcrumb-item"><a href="/">Главная</a></li>`);
    $(breadcr).append(`<li class="breadcrumb-item"><a>${data.superCategory}</a></li>`);
    $(breadcr).append(`<li class="breadcrumb-item"><a>${data.category}</a></li>`);

    let productsInCategory = document.getElementById('productsInCategory');
    let productsCount = data.products.length;
    let products = "товаров";
    if (productsCount == 1) {
        products = "товар";
    }
    if (productsCount > 1 && productsCount < 5) {
        products = "товара";
    }
    $(productsInCategory).append(`<span>${data.category}   ${productsCount} ${products}</span>`);
}

function fillSomeProducts(data) {
    let prodsView = document.getElementById('someProductsView');
    let item = ``;
    let productsList = data.products;
    for (let key = 0; key < productsList.length; key++) {
        item += `
            <div class="col">
                <div class="row no-gutters border rounded overflow-hidden flex-row mb-4 shadow-sm productView">
                    <div class="col-auto d-none d-lg-block productImg">
                        <svg class="bd-placeholder-img" width="160" height="160" xmlns="http://www.w3.org/2000/svg" 
                            preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: Thumbnail">
                            <title>Placeholder</title><rect width="100%" height="100%" fill="#55595c"/>
                            <text x="25%" y="50%" fill="#eceeef">Product img</text></svg>
                    </div>
                    <div class="col p-4 d-flex flex-column position-static">
                        <p class="card-text mb-auto productName">${productsList[key].product}</p>
                        <a class="btn btn-sm btn-outline-light producthref" href="/products/${productsList[key].id}" role="button">Подробнее&ensp;&raquo;</a>
                    </div>
                </div>
            </div>`;
        if ((key + 1) % 4 == 0) {
            $(prodsView).append(`<div class="row">` + item);
            item = ``;
        } else if ((key + 1) == productsList.length) {
            $(prodsView).append(`<div class="row">` + item);
        }
    }
}