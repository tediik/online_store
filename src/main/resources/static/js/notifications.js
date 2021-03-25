const customerNotificationsUrl = '../api/customer/notifications/';

$(document).ready(function () {
    //получение уведомлений об изменении цен
    getCustomerPriceChangeNotifications()
    //получение уведомлений о новых комментариях
    getCommentAnswers()
})

/**
 * Запрос на получение уведомлений об изменении цен для залогиненного пользователя
 */
function getCustomerPriceChangeNotifications() {
    getCurrentLoggedInCustomer().then(customer => {
        fetch(customerNotificationsUrl + "priceChanges/" + customer.id)
            .then(response => response.json())
            .then(response => renderPriceChangeNotificationTable(response.data))
    })
}

/**
 * Получение товара по id
 * @param id товара
 * @returns данные товара
 */
function getProductById(id) {
    return fetch("../api/products/" + id)
        .then(response => response.json())
        .then(product => product.data)
}

/**
 * отрисовка таблицы с уведомлениями об изменении цен
 * Сортировка по дате от новых к старым
 * @param notifications список уведомлений
 */
function renderPriceChangeNotificationTable(notifications) {
    let table = $('#priceChangeNotificationTable');
    table.empty()
        .append(`<tr>
                <th>Дата</th>
                <th>Товар</th>
                <th>Прежняя цена</th>
                <th>Новая цена</th>
                <th>Действия</th>
              </tr>`)

    for (let notification of notifications.reverse()) {
        getProductById(notification.productId).then(product => {
            let row = `<tr>
                    <td>${new Date(notification.changeDate).toLocaleDateString()}</td>
                    <td><a href="/products/${product.id}">${product.product}</a></td>
                    <td>${notification.oldPrice}</td>
                    <td>${notification.newPrice}</td>
                    <td class="text-center">
                       <div class="btn-group" role="group" aria-label="Action Buttons">
                          <button type="button" class="btn btn-danger btn-primary" data-toggle="modal" id="deleteButton"
                            data-id="${user.id}" data-action="deleteUser" data-toggle="modal" data-target="#deleteModal">
                          Удалить уведомление
                          </button>
                       </div>
                    </td>`
            table.append(row)
        })
    }
}

/**
 * Получение списка ответов на комментарии
 */
function getCommentAnswers() {
    fetch(customerNotificationsUrl + "commentAnswers")
        .then(response => response.json())
        .then(response => renderNewCommentsTable(response.data))
}

/**
 * отрисовка таблицы с уведомлениями о новых комментариях
 * Сортировка по дате от новых к старым
 * @param comments список уведомлений
 */
function renderNewCommentsTable(comments) {
    let table = $('#newCommentsNotificationTable');
    table.empty()
        .append(`<tr>
                <th>Дата</th>
                <th>Товар</th>
                <th>Ответ</th>
                <th>Действия</th>
              </tr>`)

    for (let comment of comments.sort(function (a,b) {
        return new Date(b.commentDate) - new Date(a.commentDate);
    })) {

        getProductById(comment.productId).then(product => {
            let row = `<tr>
                    <td>${new Date(comment.commentDate).toLocaleDateString()}</td>
                    <td><a href="/products/${product.id}">${product.product}</a></td>
                    <td>${comment.content}</td>
                    <td>
                       <button type="button" class="btn btn-danger btn-primary">
                        Удалить уведомление
                        </button>
                    </td>
                    <td>`
            table.append(row)
        })
    }
}

/**
 * получение залогиненного пользователя
 * @returns данные пользователя
 */
function getCurrentLoggedInCustomer() {
    return fetch('../api/customer')
        .then(response => response.json())
        .then(response => response.data)
}
