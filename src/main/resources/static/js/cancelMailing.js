// Функция для отписки от рассыки изменения цены на продукт
function cancelMailingOnProduct() {
    let email = document.forms['dataCancelMailing'].elements['emailId'].value;
    let pass = document.forms['dataCancelMailing'].elements['passwordId'].value;
    let productForDelete = document.forms['dataCancelMailing'].elements['productId'].value;

    fetch("/cancelMailing/delete",
        {
            method: "POST",
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                password: pass,
                product: productForDelete,
            }),
        })
}

// Функция для отписки от всех рассылок
function cancelAllMailing() {
    let email = document.forms['dataCancelMailingAll'].elements['email'].value;
    fetch("/cancelMailing/deleteAll",
        {
            method: "POST",
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email,
            }),
        })
}

// Функция для закрытия текущей вкладки
function closePageCancelMailing() {
    window.close();
}

$(document).ready(function () {
    var currentLocation = window.location;
    var pathName = currentLocation.pathname;
    var mass = pathName.split("/")
    var token = mass[2];
    var id = mass[3];
    var data = {
        "id": id,
        "token": token
    }
    $.ajax({
        url: '/cancelMailing/data',
        method: "POST",
        data: data,
        success: function (response) {
            if (response !== undefined) {
                $('#stringMassageCancelMailing').append($(`
                    <h4> Вы действительно хотите отписаться от рассылки на изменение цены` + response.product + `? </h4>
                    `));
                $('#appendDataCancelMailing').append($(`
                            <input type="hidden" id="emailId" name="email" value="${response.email}">
                            <input type="hidden" id="passwordId" name="password" value="${response.password}">
                            <input type="hidden" id="productId" name="productId" value="${response.id}">
`));
            }
        }
    });
});
