let productIdFromPath = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));
let basketApiAddUrl = "/api/basket/add/"
let myHeaders = new Headers()
myHeaders.append('Content-type', 'application/json; charset=UTF-8')

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

    // формирование навигационной цепочки
    $(breadcr).append(`<li class="breadcrumb-item"><a href="/">Главная</a></li>`);

    // поиск наименования категории на латинице
    if (categoryOfProduct != undefined) {
        let subcategoryOfProductInLatin = categoriesWithLatin[categoryOfProduct][subcategoryOfProduct];
        $(breadcr).append(`<li class="breadcrumb-item"><a>${categoryOfProduct}</a></li>`);
        $(breadcr).append(`<li class="breadcrumb-item"><a href="/categories/${subcategoryOfProductInLatin}">${subcategoryOfProduct}</a></li>`);
    }
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
    $("#favouriteContainer").append(`<div id="favoriteLabel"><h5 class="font-weight-normal my-2">&ensp;В избранное </h5></div>
    <svg width="640" height="480" viewbox="0 0 640 480" id="heartSvg" class="heartSvg">
        <path id="heart"
    d="m219.28949,21.827393c-66.240005,0 -119.999954,53.76001 -119.999954,
    120c0,134.755524 135.933151,170.08728 228.562454,303.308044c87.574219,
        -132.403381 228.5625,-172.854584 228.5625,-303.308044c0,-66.23999
    -53.759888,-120 -120,-120c-48.047913,0 -89.401611,28.370422 -108.5625,
        69.1875c-19.160797,-40.817078 -60.514496,-69.1875 -108.5625,-69.1875z"
    onclick="addToFavourite()"/>
        </svg>`);
    if (data.favourite) {
        $("#heart").toggleClass("filled");
        $("#favoriteLabel").empty().append(`<h5 class="font-weight-normal my-2">&ensp;Удалить</h5>`)
    }
    if (data.rating !== null) {
        $("#rateNumber").empty().append(`<h5>${data.rating}<h5>`)
    } else {
        $("#rateNumber").empty().append(`<h5>Товар ещё никто не оценил<h5>`)
    }
    rateInitialize(data.rating)

    // заполнение вкладки с описанием продукта
    let description = document.getElementById('text-description');
    if (data.descriptions == null) {
        $(description).append("Извините, описание пока не заполнено.");
    } else {
        $(description).append(`${data.descriptions.information}`);
    }


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
    $('#addToCartButton').attr('data-toggle-id', data.id)
}

/**
 * Функиця добавления/удаления товара из избранного
 */
function addToFavourite() {
    if ($("path").is('[class="filled"]')) {
        fetch("/customer/favouritesGoods", {
            method: "DELETE",
            body: productIdFromPath,
            headers: {"Content-Type": "application/json; charset=utf-8"}
        }).then(function (response) {
            if (response.ok) {
                $("#heart").toggleClass("filled");
                $("#favoriteLabel").empty().append(`<h5 class="font-weight-normal my-2">&ensp;В избранное</h5>`)
                toastr.success("Товар успешно удалён из избранного");
            } else {
                toastr.error("Авторизуйтесь/зарегистрируйтесь");
            }
        })
    } else {
        fetch("/customer/favouritesGoods", {
            method: "PUT",
            body: productIdFromPath,
            headers: {"Content-Type": "application/json; charset=utf-8"}
        }).then(function (response) {
            if (response.ok) {
                $("#heart").toggleClass("filled");
                $("#favoriteLabel").empty().append(`<h5 class="font-weight-normal my-2">&ensp;Удалить</h5>`)
                toastr.success("Товар успешно добавлен в избранное");
            } else {
                toastr.error("Авторизуйтесь/зарегистрируйтесь");
            }
        })
    }
}

/**
 * Отрисовка звёзд рейтинга
 * @param rating текущий рейтинг товара
 */
function rateInitialize(rating) {
    if (rating !== null) {
        $("#rate").rateYo({
            onSet: function (rating) {
                newRateForProduct(rating);
            },
            rating: rating,
            halfStar: true
        });
    } else {
        $("#rate").rateYo({
            onSet: function (rating) {
                newRateForProduct(rating);
            },
            rating: 0,
            halfStar: true
        });
    }
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
                $("#rateNumber").empty().append(`<h5>${value.toFixed(2)}<h5>`)
                toastr.success('Ваш голос учтён', {timeOut: 5000})
                close();
                rateInitialize(value.toFixed(2))
            })
        } else {
            toastr.error('Ваш голос не учтён', {timeOut: 5000})
            close();
        }
    })
}

/**
 * Функция добавления email в список подписанных на рассылку при изменении цены товара
 */
function priceChangeSubscribe(auth) {
    let isValidateMail = true;
    let request = {
        id: productIdFromPath
    }

    if (!auth) {
        request = {
            id: productIdFromPath,
            email: $("#emailInputModal").val()
        }

        let regex = /^[A-Z0-9._%+-]+@([A-Z0-9-]+\.)+[A-Z]{2,4}$/i;
        regex.test(request.email) ? isValidateMail = true : isValidateMail = false;
    }

    if (isValidateMail) {
        fetch('/api/products/subscribe', {
            method: 'POST',
            body: JSON.stringify(request),
            headers: {'Content-Type': 'application/json;charset=utf-8'}
        }).then(function (response) {
            if (response.ok) {
                $('#dismissButton').click();
                toastr.success("Мы уведомим вас об изменении цены на товар")
                $("#emailInputModal").val("");
            } else {
                response.text().then(function (text) {
                    toastr.error("Вы уже подписались на изменение цены этого товара");
                })
            }
        })
    } else {
        toastr.error("Введён некорректный Email");
    }
}

/**
 * function add product to cart
 * @param event
 */
function addToProductToCart(event) {
    let productId = event.target.dataset.toggleId
    fetch(basketApiAddUrl + productId, {
        headers: myHeaders,
        method: 'PUT'
    }).then(function (response) {
        if (response.status === 200) {
            toastr.success('Продукт добавлен в корзину', '', {timeOut: 1000})
        } else if (response.status === 405) {
            toastr.warning('Необходимо авторизоваться что бы добавить в корзину', '', {timeOut: 1000})
        } else {
            toastr.warning('Товар не найден', '', {timeOut: 1000})
        }
    })
}

/**
 * Функция сообщений об ошибке
 * @param message текст сообщения
 */
function showModalError(message) {
    $('#alert-modal-div').empty().append(`
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
              <strong>${message}</strong>
              <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            `)
    $('#orderModalWindow').scrollTop(0);
    window.setTimeout(function () {
        $('.alert').alert('close');
    }, 5000)
}

document.getElementById('addToCartButton').addEventListener('click', addToProductToCart)