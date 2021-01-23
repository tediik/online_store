let customerRecentlyProductsViewApiUrl = "/api/customer/recentlyViewedProducts"

$(document).ready(function () {
    /* Слушатель для кнопки удалить профиль (в модальном окне)*/
    document.getElementById('deleteProfileCustomer').addEventListener('click', deleteProfile)
    /* Слушатель для кнопки удалить профиль (без возможности восстановить (в модальном окне)*/
    document.getElementById('deleteProfileCustomerUnrecoverable').addEventListener('click', deleteProfile2)
    /*Слушатель для кнопки смены email modal*/
    $(document).delegate("#buttonChangeMail", "click", changeMail);
    /*Слушатель для кнопки смены пароля modal*/
    $(document).delegate("#submitNewPassword", "click", changePass);
    /*Слушатель для ввода пароля modal*/
    $(document).delegate("#new_password", "keyup", checkPassword);
    fetchAndRenderSomeProducts()
    dateRangePickerRecentlyViewedProducts()
});

/**
 * function setting up datepicker field
 */
function dateRangePickerRecentlyViewedProducts() {
    let start = moment().subtract(6, 'days');
    let end = moment();

    function cb(start, end) {
        $('#customerRecentlyProductsView span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
    }

    $('#customerRecentlyProductsView').daterangepicker({
        startDate: start,
        endDate: end,
        ranges: {
            'Today': [moment(), moment()],
            'Last 7 Days': [moment().subtract(6, 'days'), moment()],
            'Last 30 Days': [moment().subtract(29, 'days'), moment()],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
        },
        locale: {
            format: 'DD-MM-YYYY',
            applyLabel: 'Выбрать',
            cancelLabel: 'Отмена',
            invalidDateLabel: 'Выберите дату',
            daysOfWeek: ['Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс', 'Пн'],
            monthNames: ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'],
            firstDay: 1
        }
    }, cb);
    cb(start, end);
}

/**
 * fetch GET request to server to receive list of recently viewed products
 */
function fetchAndRenderSomeProducts() {
    fetch("/api/customer/getRecentlyViewedProductsFromDb").then(response => response.json()).then(data => fillViewedProducts(data));
    $('#headerForRecentlyProductsView').text('Недавно просмотренные товары')
}

/**
 * fetch GET request to server to receive list of recently viewed products for custom  range date
 */
$('#customerRecentlyProductsView').on('apply.daterangepicker', function (ev, picker) {
    $('#recentlyProductsViewDiv').empty()
    let startDate = picker.startDate.format('YYYY-MM-DD')
    let endDate = picker.endDate.format('YYYY-MM-DD')
    fetch(customerRecentlyProductsViewApiUrl + `?stringStartDate=${startDate}&stringEndDate=${endDate}`)
        .then(function (response) {
            if (response.status === 200) {
                response.json().then(data => fillViewedProducts(data));
                showRecentlyProductsView()
                $('#headerForRecentlyProductsView').text('Недавно просмотренные товары')
            }
        })
});

/**
 * Method for customer's pass changing
 * @returns {boolean}
 */
function changePass() {
    var formData = $('#formChangePass').serialize();
    $.ajax({
        url: '/api/customer/change-password',
        type: 'POST',
        data: formData,
        success: function (res) {
            toastr.success("Пароль успешно изменен", {timeOut: 5000});
            close();
        },
        error: function (res) {
            if (res.responseText === "error_old_pass") {
                toastr.error("Текущий пароль не совпадает. Введите заново пароль.", {timeOut: 5000});
                $('#old_password').val('').focus();
            }
            if (res.responseText === "error_valid") {
                toastr.error("Ваш пароль должен состоять из 8-20 символов, содержать буквы и цифры и не должны содержать пробелов и смайлики.", {timeOut: 5000});
                $('#new_password').val('').focus();
            }
            if (res.responseText === "error_pass_len") {
                toastr.error("Ваш пароль должен состоять минимум из 8 символов.", {timeOut: 5000});
                $('#new_password').val('').focus();
            }
        }
    });
    return false;
}

/**
 * Method for changing customer's mail
 * @returns {boolean}
 */
function changeMail() {

    var formData = $('#formChangeMail').serialize();
    $.ajax({
        url: '/api/customer/changemail',
        type: 'POST',
        data: formData,
        success: function (res) {
            toastr.success(res, {timeOut: 5000});
            close();
            $('#email_input').val($('#new_mail').val());
        },
        error: function (res) {
            if (res.status === 400) {
                if (res.responseText === 'duplicatedEmailError') {
                    $(".messages-after-submit").text('Ошибка: Электронный адрес уже зарегистрирован');
                    toastr.error("Ошибка: Электронный адрес уже зарегистрирован", {timeOut: 5000});
                }
                if (res.responseText === 'notValidEmailError') {
                    $(".messages-after-submit").text('Ошибка: Не верно указана электронная почта');
                    toastr.error("Ошибка: Не верно указана электронная почта", {timeOut: 5000});
                }
            }
        }
    });
    return false;
}

/**
 * Method for closing modals
 */
function close() {
    $("body").removeClass('modal-open');
    $('#openNewMailModal').hide();
    $('#openChangePassModal').hide();
    $(".modal-backdrop.show").hide();
}

/**
 * Method for checking that new customer's pass is match requirements
 */
function checkPassword() {
    regularExpression = new RegExp("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9@#$%]).{8,}");
    newPassword = document.getElementById("new_password").value;
    if (regularExpression.test(newPassword)) {
        $("#submitNewPassword").attr('disabled', false);
    }
}

/**
 * Обработка чекбокса #stockMailingCheckbox
 * если галка стоит, то отображать dropdownlist
 * с выбором дня недели для рассылки акций
 * если галку убрать, то скрывается dropdownlist
 * и удаляется значение
 */
function chekboxChanges(o) {
    if (o.checked != true) {
        $(".day-of-the-week-drop-list").addClass("d-none")
        $("#dayOfWeekDropList").val('')
    } else {
        $(".day-of-the-week-drop-list").removeClass("d-none")
    }
};

/**
 * Функция удаления профиля
 * @param event
 */
function deleteProfile(event) {
    let id = event.target.dataset.delId
    fetch(`/api/customer/deleteProfile/${id}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json'
        }
    }).then(function (response) {
        if (response.ok) {
            document.location.href = "/logout";
        } else {
            toastr.error('Ваш профиль не был удален.', {timeOut: 3000});
        }
    })
}

/**
 * Функция удаления профиля безвозвратно
 * @param event
 */
function deleteProfile2(event) {
    let id = event.target.dataset.delId
    fetch(`/api/customer/deleteProfileUnrecoverable/${id}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json'
        }
    }).then(function (response) {
        if (response.ok) {
            document.location.href = "/logout";
        } else {
            toastr.error('Ваш профиль не был удален.', {timeOut: 3000});
        }
    })
}

/**
 * function that fills CustomerPage with recently viewed products
 * @param data - products list
 */
function fillViewedProducts(data) {
    let prodsView = document.getElementById('recentlyProductsViewDiv');
    prodsView.innerHTML = ''
    if (data !== 'error') {
        let item = ``;
        for (let key = 0; key < data.length; key++) {
            item += `
            <div class="col-2">
                <div class="row no-gutters border rounded overflow-hidden flex-md-row mb-4 shadow-sm productView">
                    <div class="col-auto d-none d-lg-block productImg">
                        <img class="bd-placeholder-img" src="/uploads/images/products/0.jpg">
                    </div>
                    <div id="rate${data[key].id}"></div>
                    <div class="col p-4 d-flex flex-column position-static">
                        <p class="card-text mb-auto productName">${data[key].product}</p>
                        <a class="btn btn-sm btn-outline-light producthref" href="/products/${data[key].id}" role="button">Подробнее &raquo;</a>
                    </div>
                </div>
            </div>`;
            if ((key + 1) % 5 == 0) {
                $(prodsView).append(`<div class="row">` + item);
                item = ``;
            } else if ((key + 1) == data.length) {
                $(prodsView).append(`<div class="row">` + item);
            }
            $(function () {
                if (data[key].rating !== null) {
                    $(`#rate${data[key].id}`).rateYo({
                        rating: data[key].rating,
                        readOnly: true
                    });
                } else {
                    $(`#rate${data[key].id}`).rateYo({
                        rating: 0,
                        readOnly: true
                    });
                }
            });
        }
    } else {
        prodsView.innerHTML = 'Пока тут ничего нет'
    }
}

/**
 * function shows elements of report
 */
function showRecentlyProductsView() {
    document.getElementById("recentlyProductsView").style.visibility = "visible";
}
