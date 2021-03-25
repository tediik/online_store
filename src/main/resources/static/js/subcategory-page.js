let categoryNameFromPath = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));

/**
 * Get-запрос - если получаем категорию, то вызываем заполняющие страницу функции,
 * иначе "страница не найдена"
 *
 * @author Dmitriy (dshishkaryan)
 */
fetch("/api/categories" + `/${categoryNameFromPath}`)
    .then(function (response) {
        if (response.status === 200) {
            response.json()
                // .then(response => response.data)
                .then(function (data) {
                    fillBreadcrumb(data.data);
                    fillSomeProducts(data.data);
            })
        } else if (response.status === 404) {
            location.href = "/404";
        }
    });

/**
 * Заполняет навигационную цепочку под хедером страницы
 *
 * @param data сущность "категория" из БД, полученная по имени
 */
function fillBreadcrumb(data) {
    let breadcr = document.getElementById('categoriesBreadcrumb');

    // формирование навигационной цепочки
    $(breadcr).append(`<li class="breadcrumb-item"><a href="/">Главная</a></li>`);
    $(breadcr).append(`<li class="breadcrumb-item"><a>${data.category}</a></li>`);

    // определение правильного окончания слова "товар" в зависимости от количества
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

/**
 * Формирует отображение карточек товаров, представленных в категории
 *
 * @param data сущность "категория" из БД, полученная по имени
 */
function fillSomeProducts(data) {
    let prodsView = document.getElementById('someProductsView');
    let item = ``;
    let productsList = data.products;
    for (let key = 0; key < productsList.length; key++) {
        item += `
            <div class="col">
                <div class="row no-gutters border rounded overflow-hidden flex-row mb-4 shadow-sm productView">
                    <div class="col-auto d-none d-lg-block productImg">
                        <img class="bd-placeholder-img" src="/uploads/images/products/0.jpg">
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