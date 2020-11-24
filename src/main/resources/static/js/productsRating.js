let productRestUrl = "/rest/products/allProducts"

fetchProductsAndRenderTable()

/*function getAllProducts() { // не нашел, где используется эта функция
    fetch(productRestUrl, {headers: headers}).then(response => response.json())
        .then(allProducts => renderProductsRatingTable(allProducts))
}*/
/**
 * Обработка события с выбором роли для фильтрации списка зарегистрированных пользователей по роли
 */
$('#filterCategory').on("change", function() {
    var categorySelect = $('#filterCategory').val();
    if(categorySelect !== 'default') {
        $.ajax({
            type: 'PUT',
            url: '/rest/products/' + categorySelect.id,
            success: function (filteredProducts) {
                renderUsersTable(filteredProducts)
            }
        });
    }
    else{
        renderProductsRatingTable()
    }
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