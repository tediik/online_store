const customerNotificationsUrl = '../api/customer/notifications/';

$(document).ready(function () {
    getCustomerPriceChangeNotifications()
})

function getCustomerPriceChangeNotifications() {
    getCurrentLoggedInCustomer().then(customer => {
        fetch(customerNotificationsUrl + "priceChanges/" + customer.id)
            .then(response => response.json())
            .then(response => renderNotificationTable(response.data))
    })
}

function getProductById(id) {
    return fetch("../api/product/manager/" + id)
        .then(response => response.json())
        .then(product => product.data)
}

function renderNotificationTable(notifications) {
    let table = $('#priceChangeNotificationTable');
    table.empty()
        .append(`<tr>
                <th>Date</th>
                <th>Product</th>
                <th>Old price</th>
                <th>New price</th>
              </tr>`)

    for (let notification of notifications.reverse()) {
        getProductById(notification.productId).then(product => {
            let row = `<tr>
                    <td>${new Date(notification.changeDate).toLocaleTimeString()}</td>
                    <td><a href="/products/${product.id}">${product.product}</a></td>
                    <td>${notification.oldPrice}</td>
                    <td>${notification.newPrice}</td>
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
