let customerRecentlyProductsViewApiUrl = "/api/customer/recentlyViewedProducts"

$(document).ready(function () {
    /*Заполнение полей с данными пользователя*/
    userData();
    /* Слушатель для кнопки удалить профиль (в модальном окне)*/
    document.getElementById('deleteProfileCustomer').addEventListener('click', deleteProfile)
    /* Слушатель для кнопки удалить профиль (без возможности восстановить (в модальном окне)*/
    document.getElementById('deleteProfileCustomerUnrecoverable').addEventListener('click', deleteProfile2)
    /*Слушатель для кнопки смены email modal*/
    $(document).delegate("#buttonChangeMail", "click", changeMail);
    /*Слушатель для кнопки смены пароля modal*/
    document.getElementById('changePassword').addEventListener('click', changePassword)
    /*Слушатель для ввода пароля modal*/
    $(document).delegate("#new_password", "keyup", checkPassword);
    fillRecentlyProductsView()
});

/**
 * Функция смены пароля в профиле
 */
function changePassword() {
    let oldPassword = document.getElementById('old_password').value
    let newPassword = document.getElementById('new_password').value
    fetch('/api/customer/change-password', {
        method: 'POST',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json'
        },
        body: JSON.stringify({
            oldPassword: oldPassword,
            newPassword: newPassword
        })
    }).then(function (response) {
        if (response.ok) {
            toastr.success("Пароль успешно изменен.");
            $('#openChangePassModal').modal('hide')
            userData()
        } else {
            toastr.error("Некорректный пароль.")
        }
    })
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
            document.location.href = "/logout"
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
    if (o.checked !== true) {
        $(".day-of-the-week-drop-list").addClass("d-none")
        $("#dayOfWeekDropList").val('')
    } else {
        $(".day-of-the-week-drop-list").removeClass("d-none")
    }
}

/**
 * Функция удаления профиля
 * @param event
 */
function deleteProfile() {
    let id = $('#id_update').val();
    alert(id);

    fetch('/api/customer/deleteProfile/' + id, {
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
    let id = $('#id_update').val();
    fetch('/api/customer/deleteProfileUnrecoverable/' + id, {
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
 * @param elementId
 */
function fillViewedProducts(data, elementId) {
    let prodsView = document.getElementById(elementId);
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

/**
 * fetch GET request to server to receive list of all recently viewed products
 */
function fillRecentlyProductsView() {
    fetch("/api/customer/getRecentlyViewedProductsFromDb").then(response => response.json().then(data => data.reverse())).then(data => fillViewedProducts(data, 'recentlyProductsViewDiv'));
    $('#headerForRecentlyProductsView').text('Недавно просмотренные товары')
}

/**
 * fetch GET request to server to receive all list of this week recently viewed products
 */
function fillRecentlyProductsViewThisWeek() {
    $('#thisWeekRecentlyProductsDiv').empty()
    let startDate = moment().subtract(6, 'days').format('YYYY-MM-DD')
    let endDate = moment().format('YYYY-MM-DD')
    fetch(customerRecentlyProductsViewApiUrl + `?stringStartDate=${startDate}&stringEndDate=${endDate}`)
        .then(function (response) {
            if (response.status === 200) {
                response.json().then(data => data.reverse()).then(data => fillViewedProducts(data, 'thisWeekRecentlyProductsDiv'));
                showRecentlyProductsView()
                $('#headerForRecentlyProductsView').text('Недавно просмотренные товары')
            }
        })
}

/**
 * fetch GET request to server to receive all list of this month recently viewed products
 */
function fillRecentlyProductsViewThisMonth() {
    $('#thisMonthRecentlyProductsDiv').empty()
    let startDate = moment().subtract(29, 'days').format('YYYY-MM-DD')
    let endDate = moment().format('YYYY-MM-DD')
    fetch(customerRecentlyProductsViewApiUrl + `?stringStartDate=${startDate}&stringEndDate=${endDate}`)
        .then(function (response) {
            if (response.status === 200) {
                response.json().then(data => data.reverse()).then(data => fillViewedProducts(data, 'thisMonthRecentlyProductsDiv'));
                showRecentlyProductsView()
                $('#headerForRecentlyProductsView').text('Недавно просмотренные товары')
            }
        })
}

/**
 * fetch GET request to server to receive list of last month recently viewed products
 */
function fillRecentlyProductsViewLastMonth() {
    $('#lastMonthRecentlyProductsDiv').empty()
    let startDate = moment().subtract(1, 'month').startOf('month').format('YYYY-MM-DD')
    let endDate = moment().subtract(1, 'month').endOf('month').format('YYYY-MM-DD')
    fetch(customerRecentlyProductsViewApiUrl + `?stringStartDate=${startDate}&stringEndDate=${endDate}`)
        .then(function (response) {
            if (response.status === 200) {
                response.json().then(data => data.reverse()).then(data => fillViewedProducts(data, 'lastMonthRecentlyProductsDiv'));
                showRecentlyProductsView()
                $('#headerForRecentlyProductsView').text('Недавно просмотренные товары')
            }
        })
}

/**
 * Заполнение полей информации о пользователе
 */
function userData() {
    fetch("/api/customer")
        .then(response => response.json())
        .then((customer) => {
            $('#id_update').val(customer.data.id);
            $('#password_update').val(customer.data.password);
            $('#first_name_update').val(customer.data.firstName);
            $('#last_name_input').val(customer.data.lastName);
            $('#email_input').val(customer.data.email);
            $('#date_birthday_input').val(customer.data.birthdayDate);
            $('#register_date').html(customer.data.registerDate);
            if (customer.data.userGender === "MAN") {
                $('#userGenderMan').prop('checked', true);
            } else if (customer.data.userGender === "WOMAN") {
                $('#userGenderWoman').prop('checked', true);
            } else {
                $('#userGenderNone').prop('checked', true);
            }

            fetch("/api/customer/dayOfWeekForStockSend")
                .then(response => response.json())
                .then(day => {
                    if (day.data !== undefined || day.data !== null) {
                        $('#stockMailingCheckbox').prop('checked', true);
                        $(".day-of-the-week-drop-list").removeClass("d-none")
                        switch (day.data) {
                            case "MONDAY":
                                $('#monday').prop('selected', true);
                                break;
                            case "TUESDAY":
                                $('#tuesday').prop('selected', true);
                                break;
                            case "WEDNESDAY":
                                $('#wednesday').prop('selected', true);
                                break;
                            case "THURSDAY":
                                $('#thursday').prop('selected', true);
                                break;
                            case "FRIDAY":
                                $('#friday').prop('selected', true);
                                break;
                            case "SATURDAY":
                                $('#saturday').prop('selected', true);
                                break;
                            case "SUNDAY":
                                $('#sunday').prop('selected', true);
                                break;
                        }
                    } else {
                        $('#stockMailingCheckbox').prop('checked', false);
                        $(".day-of-the-week-drop-list").addClass("d-none")
                    }
                })
        })
}

/**
 * Обновление данных пользователя
 */
function updateCustomer() {
    let gender = "";
    if (document.getElementById('userGenderMan').checked) {
        gender = "MAN"
    } else if (document.getElementById('userGenderWoman').checked) {
        gender = "WOMAN"
    } else {
        gender = null
    }

    let dropList = document.getElementById("dayOfWeekDropList");
    let selectedDay = dropList.options[dropList.selectedIndex].value;

    let customerForUpdate = {
        id: $("#id_update").val(),
        password: $('#password_update').val(),
        firstName: $('#first_name_update').val(),
        lastName: $('#last_name_input').val(),
        email: $('#email_input').val(),
        birthdayDate: $('#date_birthday_input').val(),
        userGender: gender,
        registerDate: $('#register_date').val(),
        dayOfWeekForStockSend: selectedDay
    }
    fetch('/api/customer', {
        method: 'PUT',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify(customerForUpdate)
    })
        .then(() => userData())
        .catch(error => console.log("Не удалось сохранить изменения " + error))
}
