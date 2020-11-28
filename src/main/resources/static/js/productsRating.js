abv('default')

/*function getAllProducts() { // не нашел, где используется эта функция
    fetch(productRestUrl, {headers: headers}).then(response => response.json())
        .then(allProducts => renderProductsRatingTable(allProducts))
}*/

function abv(categorySelect) {
    $('#filterRating').on("change", function () {
        let orderSelect = $('#filterRating').val();
        if (orderSelect === 'descOrder') {
            fetchCategorySelectProducrsInDescOrderAndRender(categorySelect)
        }
        if (orderSelect === 'ascOrder') {
            fetchCategorySelectProducrsInAscOrderAndRender(categorySelect)
        }

    });
    let orderSelect = $('#filterRating').val();
    if (orderSelect === 'descOrder') {
        fetchCategorySelectProducrsInDescOrderAndRender(categorySelect)
    }
    if (orderSelect === 'ascOrder') {
        fetchCategorySelectProducrsInAscOrderAndRender(categorySelect)
    }
}

/*$('#filterRating').on("change", function () {
    let orderSelect = $('#filterRating').val();
    if (orderSelect === 'descOrder') {
        fetchSortedByRatingProductsInDescendingOrderAndRenderTable()
    }
    if (orderSelect === 'ascOrder') {
        fetchSortedByRatingProductsInAscendingOrderAndRenderTable()
    }

});*/

/**
 * Обработка события с выбором роли для фильтрации списка зарегистрированных пользователей по роли
 */
$('#filterCategory').on("change", function () {
    let categorySelect = $('#filterCategory').val();
    abv(categorySelect)

});

/**
 * функция рендера таблицы продуктов
 * @param products
 */
function renderProductsRatingTable(products) {
    let table = $('#products-table')
    table.empty()
        .append(`<tr>
                <th>Рейтинг</th>
                <th>ID</th>
                <th>Наименование товара</th>
                <th>Цена</th>
              </tr>`)
    for (let i = 0; i < products.length; i++) {
        const product = products[i];
        let row = `
                <tr id="tr-${product.id}">
                    <td>${product.rating}</td>
                    <td>${product.id}</td>
                    <td>${product.product}</td>
                    <td>${product.price}</td>              
                </tr>
                `;
        table.append(row)
    }
}

/**
 * функция делает fetch запрос на рест контроллер, преобразует полученный объект в json
 * и передает функции рендера таблицы renderProductsRatingTable
 */
function fetchProductsAndRenderTable() {
    fetch("/rest/products/allProducts")
        .then(response => response.json())
        .then(products => renderProductsRatingTable(products))
}

/**
 * функция делает fetch запрос на рест контроллер, получает список продуктов по возрастанию их рейтинга,
 * преобразует полученный объект в json
 * и передает функции рендера таблицы renderProductsRatingTable
 */
function fetchSortedByRatingProductsInAscendingOrderAndRenderTable() {
    fetch("/rest/products/getProductsSortedInAscendingOrder")
        .then(response => response.json())
        .then(products => renderProductsRatingTable(products))
}

/**
 * функция делает fetch запрос на рест контроллер, получает список продуктов по убыванию их рейтинга,
 * преобразует полученный объект в json
 * и передает функции рендера таблицы renderProductsRatingTable
 */
function fetchSortedByRatingProductsInDescendingOrderAndRenderTable() {
    fetch("/rest/products/getProductsSortedInDescendingOrder")
        .then(response => response.json())
        .then(products => renderProductsRatingTable(products))
}

/**
 * функция делает fetch запрос на рест контроллер, получает список продуктов выбранной категории
 * по возрастанию их рейтинга,
 * преобразует полученный объект в json
 * и передает функции рендера таблицы renderProductsRatingTable
 */
function fetchCategorySelectProducrsInAscOrderAndRender(categorySelect) {
    fetch("/rest/products/ascOrder/" + categorySelect)
        .then(response => response.json())
        .then(products => renderProductsRatingTable(products))
}

/**
 * функция делает fetch запрос на рест контроллер, получает список продуктов выбранной категории
 * по убыванию их рейтинга,
 * преобразует полученный объект в json
 * и передает функции рендера таблицы renderProductsRatingTable
 */
function fetchCategorySelectProducrsInDescOrderAndRender(categorySelect) {
    fetch("/rest/products/descOrder/" + categorySelect)
        .then(response => response.json())
        .then(products => renderProductsRatingTable(products))
}

function createReport() {
    let categorySelect = $('#filterCategory').val();
    let number = $('#addNumber').val();
    fetch('/manager/products/report/' +  categorySelect + '/' + number)
        .then(function (response) {
            if (response.status === 200) {
                response.blob().then(blob => {
                    let url = window.URL.createObjectURL(blob);
                    let file = document.createElement('a');
                    file.href = url;
                    file.download = 'products_report_'+ categorySelect+'.xlsx';
                    file.click();
                    toastr.success('Данные успешно выгружены')
                })
            } else {
                toastr.error('Товаров не найдено')
            }
        })
}
