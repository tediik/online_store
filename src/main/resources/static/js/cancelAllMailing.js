$(document).ready(function () {
    var currentLocation = window.location;
    var pathName = currentLocation.pathname;
    var mass = pathName.split("/")
    var token = mass[3];
    var data = {
        "token": token
    }
    $.ajax({
        url: '/cancelAllMailing/data',
        method: "POST",
        data: data,
        success: function (response) {
            if (response !== undefined) {
                alert(response.email)

                $('#appendDataCancelAllMailing').append($(`
                            <input type="hidden" id="email" name="email" value="${response.email}">
`));
            }
        }
    });
});

// Функция для закрытия текущей вкладки
function closePageCancelAllMailing() {
    window.close();
}
