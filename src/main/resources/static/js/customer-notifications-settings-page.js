const customerNotificationsUrl = '../api/customer/notifications/';

$(document).ready(function () {
    checkSubscribe();
    checkPriceEmails();
    /* Слушатель для кнопки удалить профиль (в модальном окне)*/
    document.getElementById('deleteProfileCustomer').addEventListener('click', deleteProfile)
    /* Слушатель для кнопки удалить профиль (без возможности восстановить (в модальном окне)*/
    document.getElementById('deleteProfileCustomerUnrecoverable').addEventListener('click', deleteProfile2)

})

function checkboxChanges(o) {
    if (o.checked != true) {
        $('.day-of-the-week-drop-list').addClass('d-none')
        $('#dayOfWeekDropList').val('')
    } else {
        $('.day-of-the-week-drop-list').removeClass("d-none")
    }
}

/**
 * отображение выбранного дня для рассылки
 */
function checkSubscribe() {
    fetch(customerNotificationsUrl + "dayOfWeekForStockSend")
        .then(response => response.json())
        .then(res => res.data)
        .then(day => {
            if (day != undefined) {
                $('#stockMailingCheckbox').prop('checked', true);
                $(".day-of-the-week-drop-list").removeClass("d-none")
                switch (day) {
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
                $(".day-of-the-week-drop-list").addClass("d-none")
                $("#dayOfWeekDropList").val('')
            }
        })
}

function updateDayForStockSend() {
    let dropList = document.getElementById('dayOfWeekDropList');
    let checkbox = document.getElementById('stockMailingCheckbox')
    let selectedDay;
    if (checkbox.checked == true) {
        selectedDay = dropList.options[dropList.selectedIndex].value;
    } else {
        selectedDay = null;
    }

    fetch(customerNotificationsUrl + 'dayOfWeekForStockSend', {
        method: 'PUT',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'text/plain; charset=UTF-8'
        },
        body: JSON.stringify(selectedDay)
    })
        .then(() => checkSubscribe())
        .catch(err => console.log("Не удалось изменить день рассылки " + err))
}

function checkPriceEmails() {
    const slider = document.getElementById("sliderPricesChanges")
    fetch(customerNotificationsUrl + 'emailConfirmation')
        .then(response => response.json())
        .then(response => response.data)
        .then(confirmation => {
            if (confirmation === "CONFIRMED") {
                slider.checked = true;
            } else {
                slider.checked = false;
            }
        })
}

function checkSlider() {
    const slider = document.getElementById("sliderPricesChanges")
    if (!slider.checked) {
        fetch(customerNotificationsUrl + 'unsubscribeEmail', {
            method: 'PUT',
            headers: {
                'Accept': 'application/json, text/plain, */*',
                'Content-type': 'application/json'
            }
        })
            .then(function (response) {
                if (response.ok) {
                    toastr.success('Вы были отписаны от рассылки', {timeOut: 3000});
                }
            })
    } else {
        fetch(customerNotificationsUrl + 'emailConfirmation', {
            method: 'PUT',
            headers: {
                'Accept': 'application/json, text/plain, */*',
                'Content-type': 'application/json'
            }
        })
            .then(function (response) {
                if (response.ok) {
                    toastr.success('Письмо с подтверждением отправлено Вам на почту', {timeOut: 3000})
                }
            })
    }
}

/**
 * Функция удаления профиля
 */
function deleteProfile() {
    getCurrentLoggedInCustomer().then(customer => {
        fetch('../api/customer/deleteProfile/' + customer.id, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json, text/plain, */*',
                'Content-type': 'application/json'
            }
        }).then(function (response) {
            if (response.status !== 200) {
                toastr.error('Ваш профиль не был удален.', {timeOut: 3000});
            } else {
                document.location.href = "/logout";
            }
        })
    })
}

/**
 * Функция удаления профиля безвозвратно
 */
function deleteProfile2() {
    getCurrentLoggedInCustomer().then(customer => {
        fetch('../api/customer/deleteProfileUnrecoverable/' + customer.id, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json, text/plain, */*',
                'Content-type': 'application/json'
            }
        }).then(function (response) {
            if (response.status !== 200) {
                toastr.error('Ваш профиль не был удален.', {timeOut: 3000});
            } else {
                document.location.href = "/logout";
            }
        })
    })
}

/**
 * fetch запрос на получение залогиненного пользователя
 */
function getCurrentLoggedInCustomer() {
    return fetch('../api/customer')
        .then(response => response.json())
        .then(response => response.data)
}