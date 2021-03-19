const customerNotificationsUrl = '../api/customer/notifications/';

$(document).ready(function () {
    /*отображает выбранный день для рассылки*/
    checkSubscribe();
    /*отображает положение слайдеров email подписок*/
    checkEmails();
    /* Слушатель для кнопки удалить профиль (в модальном окне)*/
    document.getElementById('deleteProfileCustomer').addEventListener('click', deleteProfile)
    /* Слушатель для кнопки удалить профиль (без возможности восстановить (в модальном окне)*/
    document.getElementById('deleteProfileCustomerUnrecoverable').addEventListener('click', deleteProfile2)
})

/**
 * Отслеживает положение переключателя "Получение рассылок на email"
 * В случае отказа убирает select с выбором дня для рассылки
 * @param o - переключение слайдера
 */
function checkboxChanges(o) {
    if (o.checked != true) {
        $('.day-of-the-week-drop-list').addClass('d-none')
        $('#dayOfWeekDropList').val('')
        changeDayOfWeekForStockSend(null)
    } else {
        $('.day-of-the-week-drop-list').removeClass("d-none")
    }
}

/**
 * fetch запрос для отображения дня для рассылки.
 * Если день не выбран (undefined), переводит слайдер в неактивное положение и прячет select с выбором ролей
 */
function checkSubscribe() {
    fetch(customerNotificationsUrl + "dayOfWeekForStockSend")
        .then(response => response.json())
        .then(res => res.data)
        .then(day => {
            if (day != undefined) {
                $('#sliderEmailConfirmation').prop('checked', true);
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

/**
 * При нажатии на кнопку "сохранить" считывает значение selectа
 * и передает значение в функцию changeDayOfWeekForStockSend
 */
function updateDayForStockSend() {
    let dropList = document.getElementById('dayOfWeekDropList');
    let checkbox = document.getElementById('sliderEmailConfirmation')
    let selectedDay;
    if (checkbox.checked == true) {
        selectedDay = dropList.options[dropList.selectedIndex].value;
    } else {
        selectedDay = null;
    }
    changeDayOfWeekForStockSend(selectedDay)
}

/**
 *Получает значения для fetch запроса для изменения дня для рассылок
 * @param day - день недели
 */
function changeDayOfWeekForStockSend(day) {
    fetch(customerNotificationsUrl + 'dayOfWeekForStockSend', {
        method: 'PUT',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'text/plain; charset=UTF-8'
        },
        body: day
    })
        .then(() => checkSubscribe())
        .catch(err => console.log("Не удалось изменить день рассылки " + err))
}

/**
 * Устанавливает положение слайдеров
 * sliderPriceChanges - рассылка об изменении цен
 * sliderNewComments - рассылка о новых комментариях
 */
function checkEmails() {
    const sliderPriceChanges = document.getElementById("sliderPricesChanges")
    const sliderNewComments = document.getElementById("sliderNewComments")
    fetch(customerNotificationsUrl + 'emailConfirmation/price')
        .then(response => response.json())
        .then(response => response.data)
        .then(confirmation => {
            if (confirmation === "CONFIRMED") {
                sliderPriceChanges.checked = true;
            } else {
                sliderPriceChanges.checked = false;
            }
        })

    fetch(customerNotificationsUrl + 'emailConfirmation/comments')
        .then(response => response.json())
        .then(response => response.data)
        .then(confirmation => {
            if (confirmation === "CONFIRMED") {
                sliderNewComments.checked = true;
            } else {
                sliderNewComments.checked = false;
            }
        })
}

/**
 * При нажатии на слайдер "Изменение цен" либо отменяет подписку,
 * либо отправляется запрос на подтверждение рассылки
 */
function changePriceEmailsSlider() {
    const slider = document.getElementById("sliderPricesChanges")
    if (!slider.checked) {
        fetch(customerNotificationsUrl + 'unsubscribe/priceChangesEmails', {
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
 * При нажатии на слайдер "Комментарии, ответы" либо отменяет рассылку писем,
 * либо отправляется запрос на подтверждение рассылки
 */
function changeCommentsEmailSlider() {
    const slider = document.getElementById("sliderNewComments");
    if (!slider.checked) {
        fetch(customerNotificationsUrl + 'unsubscribe/commentsEmails', {
            method: 'PUT',
            headers: {
                'Accept': 'application/json, text/plain, */*',
                'Content-type': 'application/json'
            }
        })
            .then(response => {
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

