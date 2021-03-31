const customerNotificationsUrl = '../api/customer/notifications/';

$(document).ready(function () {
    //получение уведомлений об изменении цен
    getCustomerPriceChangeNotifications()
    //получение уведомлений о новых комментариях
    getCommentAnswers()

    let idInf = $("#notid").attr('data-id');
    $("#notid").on('click', (event) => {deleteNotification(idInf)})

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
                    <td>
                       <input id="changeId" data-id="${notification.id}" 
                       type="button" class="btn btn-danger btn-primary" value="Удалить уведомление" 
                       onclick="deletePriceChangeNot(${notification.id})"/>  
                    </td>
                    </td>`
            table.append(row)
        })
    }
}

/**
 * Удаление уведомления на изменение цены
 * @param id уведомления
 */
function deletePriceChangeNot(id) {
    fetch(customerNotificationsUrl + 'priceChanges/delete/' + id, {
        method: 'DELETE'
    }).then(response => response.text())
        .then(deletePriceChangeNot => console.log('Notification with id: ' + id + ' was successfully deleted'))
        .then(showTable => {
            showAndRefreshPriceChangeNotTab(showTable)
        })
}

/**
 * Обновляет страницу с уведомлениями на изменение цены
 */
function showAndRefreshPriceChangeNotTab() {
    getCustomerPriceChangeNotifications()
}

/**
 * Получение списка ответов на комментарии
 */
function getCommentAnswers() {
    getCurrentLoggedInCustomer().then(customer => {
        fetch(customerNotificationsUrl + "commentAnswers/" + customer.id)
            .then(response => response.json())
            .then(response => renderNewCommentsTable(response.data))
    })
}

/**
 * отрисовка таблицы с уведомлениями о новых комментариях
 * Сортировка по дате от новых к старым
 * @param notifications список уведомлений
 */
function renderNewCommentsTable(notifications) {
    let table = $('#newCommentsNotificationTable');
    table.empty()
        .append(`<tr>
                <th>Дата</th>
                <th>Товар</th>
                <th>Ответ</th>
                <th>Действия</th>
              </tr>`)

    for (let notification of notifications.reverse()) {
        getProductById(notification.productId).then(product => {
            let row = `<tr>
                    <td>${new Date(notification.reviewDate).toLocaleDateString()}</td>
                    <td><a href="/products/${product.id}">${product.product}</a></td>
                    <td>${notification.content}</td>
                    <td>
                       <input id="notid" data-id="${notification.id}" 
                       type="button" class="btn btn-danger btn-primary" value="Удалить уведомление" 
                       onclick="deleteNotification(${notification.id})"/>  
                    </td>
                    </td>`
            table.append(row)
        })
    }

}

/**
 * Удаление уведомления на ответы пользователю
 * @param id уведомления
 */
function deleteNotification(id) {
    fetch(customerNotificationsUrl + 'commentAnswers/delete/' + id, {
        method: 'DELETE'
    }).then(response => response.text())
        .then(deleteNotification => console.log('Notification with id: ' + id + ' was successfully deleted'))
        .then(showTable => {
            showAndRefreshNotificationTab(showTable)
        })
}

/**
 * Обновляет страницу с уведомлениями на ответы пользователю
 */
function showAndRefreshNotificationTab() {
    getCommentAnswers()
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
