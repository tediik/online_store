let productIdFromPath = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));

/**
 * Get-запрос - если получаем продукт, то вызываем заполняющие страницу функции,
 * иначе "страница не найдена"
 */
fetch("/api/products" + `/${productIdFromPath}`)
    .then(function (response) {
        if (response.status === 200) {
            response.json().then(function (data) {
                fillBreadcrumb(data);
                fillAboutProduct(data);
            })
        } else if (response.status === 404) {
            location.href = "/404";
        }
    });

/**
 * Заполняет навигационную цепочку под хедером страницы
 *
 * @param data продукт из БД по id
 * @returns {Promise<void>}
 */
async function fillBreadcrumb(data) {
    let categories = await fetch("/api/categories/categories").then(response => response.json());
    let categoriesWithLatin = await fetch("/api/categories").then(response => response.json());

    let breadcr = document.getElementById('categoriesBreadcrumb');
    let categoryOfProduct;
    let subcategoryOfProduct;

    // определение категории для продукта
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

    // поиск наименования категории на латинице
    let subcategoryOfProductInLatin = categoriesWithLatin[categoryOfProduct][subcategoryOfProduct];

    // формирование навигационной цепочки
    $(breadcr).append(`<li class="breadcrumb-item"><a href="/">Главная</a></li>`);
    $(breadcr).append(`<li class="breadcrumb-item"><a>${categoryOfProduct}</a></li>`);
    $(breadcr).append(`<li class="breadcrumb-item"><a href="/categories/${subcategoryOfProductInLatin}">${subcategoryOfProduct}</a></li>`);
    $(breadcr).append(`<li class="breadcrumb-item"><a>${data.product}</a></li>`);
}

/**
 * Заполняет информацию о продукте
 *
 * @param data продукт из БД по id
 * @returns {Promise<void>}
 */
async function fillAboutProduct(data) {
    let productName = document.getElementById('productName');
    $(productName).append(`<br><h2 class="font-weight-normal">${data.product}</h2>`);
    $(productName).append(`<br><h5 class="font-weight-normal">Оценка товара: ${data.rating} из 5</h5>`);
    $("#favouriteContainer").append(`<div id="favoriteLabel"><h5 class="font-weight-normal my-2">&ensp;В избранное </h5></div>
    <svg width="640" height="480" viewbox="0 0 640 480" id="heartSvg">
        <path id="heart"
    d="m219.28949,21.827393c-66.240005,0 -119.999954,53.76001 -119.999954,
    120c0,134.755524 135.933151,170.08728 228.562454,303.308044c87.574219,
        -132.403381 228.5625,-172.854584 228.5625,-303.308044c0,-66.23999
    -53.759888,-120 -120,-120c-48.047913,0 -89.401611,28.370422 -108.5625,
        69.1875c-19.160797,-40.817078 -60.514496,-69.1875 -108.5625,-69.1875z"
    onclick="addToFavourite()"/>
        </svg>`);


    // заполнение вкладки с описанием продукта
    let description = document.getElementById('text-description');
    $(description).append(`${data.descriptions.information}`);

    // заполнение вкладки с характеристиками продукта
    let specifications = document.getElementById('text-specifications');
    for (let key in data.descriptions) {
        if (key != "id" && key != "information") {
            let content = `<tr><td class="font-weight-bold">${key}</td>
                           <td>${data.descriptions[key]}</td>
                       </tr>`;
            $(specifications).append(content);
        }
    }
}

function addToFavourite() {
    if($("path").is('[class="filled"]')) {
        $("#heart").toggleClass("filled");
        $("#favoriteLabel").empty().append(`<h5 class="font-weight-normal my-2">&ensp;В избранное</h5>`)
    } else {
        fetch("/customer/favouritesGoods", {
            method: "PUT",
            body: productIdFromPath,
            headers: {"Content-Type": "application/json; charset=utf-8"}
        });
        $("#heart").toggleClass("filled");
        $("#favoriteLabel").empty().append(`<h5 class="font-weight-normal my-2">&ensp;Удалить</h5>`)
    }
}