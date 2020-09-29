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
    $("#rateNumber").empty().append(`<h5>${data.rating}<h5>`)
    rateInitialize(data.rating)
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

/**
 * Отрисовка звёзд рейтинга
 * @param rating текущий рейтинг товара
 */
function rateInitialize(rating) {
    $("#rate").rateYo({
        onSet: function (rating) {
            newRateForProduct(rating);
        },
        rating: rating,
        halfStar: true
    });
}

/**
 * Вычисление рейтинга и добавление его в базу к товару
 * @param rating оценка товара текущим пользователем
 */
function newRateForProduct(rating) {
    $("#rate").rateYo("destroy");
    let res;
    fetch(`/api/products/rating?id=${productIdFromPath}&rating=${rating}`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json;charset=utf-8'}
    }).then(function (response) {
        if (response.ok) {
            res = response.json();
            res.then(function (value) {
                $("#rateNumber").empty().append(`<h5>${value}<h5>`)
                toastr.success('Ваш голос учтён', {timeOut: 5000})
                close();
                rateInitialize(value)
            })
        } else {
            toastr.error('Ваш голос не учтён', {timeOut: 5000})
            close();
        }
    })
}