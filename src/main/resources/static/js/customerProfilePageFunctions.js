$(document).ready(function () {
    /* Слушатель для кнопки удалить профиль (в модальном окне)*/
    document.getElementById('deleteProfileCustomer').addEventListener('click', deleteProfile)

    /* Слушатель для кнопки удалить профиль (без возможности восстановить (в модальном окне)*/
    document.getElementById('deleteProfileCustomer2').addEventListener('click', deleteProfile2)
    /*Слушатель для кнопки смены email modal*/
    $(document).delegate("#buttonChangeMail", "click", changeMail);
    /*Слушатель для кнопки смены пароля modal*/
    $(document).delegate("#submitNewPassword", "click", changePass);
    /*Слушатель для ввода пароля modal*/
    $(document).delegate("#new_password", "keyup", checkPassword);
});

/**
 * Method for customer's pass changing
 * @returns {boolean}
 */
function changePass() {
    var formData = $('#formChangePass').serialize();
    $.ajax({
        url: '/customer/change-password',
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
        url: '/customer/changemail',
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
    fetch(`/customer/deleteProfile/${id}`, {
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
 * Функция удаления профиля
 * @param event
 */
function deleteProfile2(event) {
    let id = event.target.dataset.delId
    fetch(`/customer/deleteProfile2/${id}`, {
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
