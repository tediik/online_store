sortProducts('default')
// getCategories()

/**
 * Обработка события с выбором порядка сортировки товаров
 */
function sortProducts(categorySelect) {
    $('#filterRating').on("change", function () {
        let orderSelect = $('#filterRating').val();
        let categorySelect1 = $('#ratingFilterCategory').val();
        if (orderSelect === 'descOrder') {
            fetchCategorySelectProductsInDescOrderAndRender(categorySelect1, orderSelect)
        }
        if (orderSelect === 'ascOrder') {
            fetchCategorySelectProductsInAscOrderAndRender(categorySelect1, orderSelect)
        }

    });
    let orderSelect = $('#filterRating').val();
    if (orderSelect === 'descOrder') {
        fetchCategorySelectProductsInDescOrderAndRender(categorySelect, orderSelect)
    }
    if (orderSelect === 'ascOrder') {
        fetchCategorySelectProductsInAscOrderAndRender(categorySelect, orderSelect)
    }
}

/**
 * Обработка события с выбором категории для фильтрации списка товаров по выбранной категории
 */
$('#ratingFilterCategory').on("change", function () {
    let categorySelect = $('#ratingFilterCategory').val();
    sortProducts(categorySelect)

});

/**
 * функция рендера таблицы продуктов
 * @param products
 */
function renderProductsRatingTable(products) {
    let table = $('#rating-products-table')
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
function fetchCategorySelectProductsInAscOrderAndRender(categorySelect, orderSelect) {
    fetch("/api/product/sort/" + categorySelect + '/' + orderSelect)
        .then(response => response.json())
        .then(response => response.data)
        .then(products => renderProductsRatingTable(products))
}

/**
 * функция делает fetch запрос на рест контроллер, получает список продуктов выбранной категории
 * по убыванию их рейтинга,
 * преобразует полученный объект в json
 * и передает функции рендера таблицы renderProductsRatingTable
 */
function fetchCategorySelectProductsInDescOrderAndRender(categorySelect, orderSelect) {
    fetch("/api/product/sort/" + categorySelect + '/' + orderSelect)
        .then(response => response.json())
        .then(response => response.data)
        .then(products => renderProductsRatingTable(products))
}

/**
 * Функция отвечает за процесс создания отчета по продуктам, учитывая выбранную категорию и сортировку
 *
 */
function createReport() {
    let categorySelect = $('#ratingFilterCategory').val();
    let number = $('#addNumber').val();
    let numberField = document.getElementById('addNumber');
    if (!number) {
        toastr.error('Заполните поле количества товаров');
        return false;
    }
    let orderSelect = $('#filterRating').val();
    fetch('/api/product/report/' + categorySelect + '/' + number + '/' + orderSelect)
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
