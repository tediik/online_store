sortProducts('default')

/**
 * Обработка события с выбором порядка сортировки товаров
 */
function sortProducts(categorySelect) {
    $('#filterRating').on("change", function () {
        let orderSelect = $('#filterRating').val();
        let categorySelect1 = $('#filterCategory').val();
        if (orderSelect === 'descOrder') {
            fetchCategorySelectProductsInDescOrderAndRender(categorySelect1)
        }
        if (orderSelect === 'ascOrder') {
            fetchCategorySelectProductsInAscOrderAndRender(categorySelect1)
        }

    });
    let orderSelect = $('#filterRating').val();
    if (orderSelect === 'descOrder') {
        fetchCategorySelectProductsInDescOrderAndRender(categorySelect)
    }
    if (orderSelect === 'ascOrder') {
        fetchCategorySelectProductsInAscOrderAndRender(categorySelect)
    }
}

/**
 * Обработка события с выбором категории для фильтрации списка товаров по выбранной категории
 */
$('#filterCategory').on("change", function () {
    let categorySelect = $('#filterCategory').val();
    sortProducts(categorySelect)

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
 * функция делает fetch запрос на рест контроллер, получает список продуктов выбранной категории
 * по возрастанию их рейтинга,
 * преобразует полученный объект в json
 * и передает функции рендера таблицы renderProductsRatingTable
 */
function fetchCategorySelectProductsInAscOrderAndRender(categorySelect) {
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
function fetchCategorySelectProductsInDescOrderAndRender(categorySelect) {
    fetch("/rest/products/descOrder/" + categorySelect)
        .then(response => response.json())
        .then(products => renderProductsRatingTable(products))
}

/**
 * Функция отвечает за процесс создания отчета по продуктам, учитывая выбранную категорию и сортировку
 *
 */
function createReport() {
    let categorySelect = $('#filterCategory').val();
    let number = $('#addNumber').val();
    let orderSelect = $('#filterRating').val();
    fetch('/rest/products/report/' + categorySelect + '/' + number + '/' + orderSelect)
        .then(function (response) {
            if (response.status === 200) {
                response.blob().then(blob => {
                    let url = window.URL.createObjectURL(blob);
                    let file = document.createElement('a');
                    file.href = url;
                    file.download = 'products_report_' + categorySelect + '.xlsx';
                    file.click();
                    toastr.success('Данные успешно выгружены')
                })
            } else {
                toastr.error('Товаров не найдено')
            }
        })
}
