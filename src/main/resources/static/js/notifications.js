const customerNotificationsUrl = '../api/customer/notifications/';

$(document).ready(function () {
    getCustomerPriceChangeNotifications()
    getCommentAnswers()
})

function getCustomerPriceChangeNotifications() {
    getCurrentLoggedInCustomer().then(customer => {
        fetch(customerNotificationsUrl + "priceChanges/" + customer.id)
            .then(response => response.json())
            .then(response => renderNotificationTable(response.data))
    })
}

function getProductById(id) {
    return fetch("../api/products/" + id)
        .then(response => response.json())
        .then(product => product.data)
}

function renderNotificationTable(notifications) {
    let table = $('#priceChangeNotificationTable');
    table.empty()
        .append(`<tr>
                <th>Дата</th>
                <th>Товар</th>
                <th>Прежняя цена</th>
                <th>Новая цена</th>
              </tr>`)

    for (let notification of notifications.reverse()) {
        getProductById(notification.productId).then(product => {
            let row = `<tr>
                    <td>${new Date(notification.changeDate).toLocaleDateString()}</td>
                    <td><a href="/products/${product.id}">${product.product}</a></td>
                    <td>${notification.oldPrice}</td>
                    <td>${notification.newPrice}</td>
                    <td>`
            table.append(row)
        })
    }
}

function getCommentAnswers() {
    fetch(customerNotificationsUrl + "commentAnswers")
        .then(response => response.json())
        .then(response => renderNewCommentsTable(response.data))
}

function renderNewCommentsTable(comments) {
    let table = $('#newCommentsNotificationTable');
    table.empty()
        .append(`<tr>
                <th>Дата</th>
                <th>Товар</th>
                <th>Ответ</th>
              </tr>`)

    for (let comment of comments.sort(function (a,b) {
        return new Date(b.commentDate) - new Date(a.commentDate);
    })) {

        getProductById(comment.productId).then(product => {
            let row = `<tr>
                    <td>${new Date(comment.commentDate).toLocaleDateString()}</td>
                    <td><a href="/products/${product.id}">${product.product}</a></td>
                    <td>${comment.content}</td>
                    <td>`
            table.append(row)
        })
    }
}

function getCurrentLoggedInCustomer() {
    return fetch('../api/customer')
        .then(response => response.json())
        .then(response => response.data)
}
