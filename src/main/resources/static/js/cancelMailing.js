
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

// Функция для закрытия текущей вкладки
function closePageCancelMailing() {
    window.close();
}