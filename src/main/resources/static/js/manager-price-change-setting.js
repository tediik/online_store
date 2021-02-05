document.getElementById('savePriceChangeTemplateChanges').addEventListener('click', handleSaveTemplateButton)
document.getElementById('saveSendConfirmationTokenToResetPasswordTemplateChanges').addEventListener('click', handleSaveTemplateResetPasswordButton)
document.getElementById('saveRegNewAccountTemplateChanges').addEventListener('click', handleSaveTemplateRegNewAccountButton)
document.getElementById('saveChangeUsersMailTemplateChanges').addEventListener('click', handleSaveTemplateChangeUsersMailButton)
document.getElementById('saveChangeUsersPassTemplateChanges').addEventListener('click', handleSaveTemplateChangeUsersPassButton)
document.getElementById('saveChangeActivateUserTemplateChanges').addEventListener('click', handleSaveTemplateActivateUserButton)

$(document).ready(function () {
    $('#editPriceChangeTemplateSummernote').summernote({
        height: 500
    });
    fetchEmailPriceTemplate();
})

$(document).ready(function () {
    $('#sendConfirmationTokenToResetPasswordTemplateSummernote').summernote({
        height: 500
    });
    fetchSendConfirmationTokenToResetPasswordTemplate();
})

$(document).ready(function () {
    $('#regNewAccountTemplateSummernote').summernote({
        height: 500
    });
    fetchRegNewAccountTemplate();
})

$(document).ready(function () {
    $('#changeUsersMailTemplateSummernote').summernote({
        height: 500
    });
    fetchChangeUsersMailTemplate();
})

$(document).ready(function () {
    $('#changeUsersPassTemplateSummernote').summernote({
        height: 500
    });
    fetchChangeUsersPassTemplate();
})

$(document).ready(function () {
    $('#activateUserTemplateSummernote').summernote({
        height: 500
    });
    fetchChangeActivateUserTemplate();
})

/**
 * Функция отправляет запрос и получает шаблон для рассылки оповещений об изменении цены
 */
function fetchEmailPriceTemplate() {
    fetch(apiCommonSettingsUrl + '/price_change_distribution_template').then(function (response) {
        if (response.ok) {
            response.json()
                .then(emailTemplate => $('#editPriceChangeTemplateSummernote').summernote('code', emailTemplate.textValue))
        }
    })
}

/**
 * Функция отправляет запрос и получает шаблон для рассылки шаблона подтверждения на изменение пароля
 */
function fetchSendConfirmationTokenToResetPasswordTemplate() {
    fetch( apiTemplatesMailingSettingsUrl + '/send_confirmation_token_to_reset_password').then(function (response) {
        if (response.ok) {
            response.json()
                .then(emailTemplate => $('#sendConfirmationTokenToResetPasswordTemplateSummernote').summernote('code', emailTemplate.textValue))
        }
    })
}

/**
 * Функция отправляет запрос и получает шаблон для рассылки шаблона подтверждения регистрации нового аккаунта
 */
function fetchRegNewAccountTemplate() {
    fetch( apiTemplatesMailingSettingsUrl + '/reg_new_account').then(function (response) {
        if (response.ok) {
            response.json()
                .then(emailTemplate => $('#regNewAccountTemplateSummernote').summernote('code', emailTemplate.textValue))
        }
    })
}

/**
 * Функция отправляет запрос и получает шаблон для рассылки подтверждения изменения емайла
 */
function fetchChangeUsersMailTemplate() {
    fetch( apiTemplatesMailingSettingsUrl + '/change_users_mail').then(function (response) {
        if (response.ok) {
            response.json()
                .then(emailTemplate => $('#changeUsersMailTemplateSummernote').summernote('code', emailTemplate.textValue))
        }
    })
}

/**
 * Функция отправляет запрос и получает шаблон для рассылки подтверждения изменения пароля
 */
function fetchChangeUsersPassTemplate() {
    fetch( apiTemplatesMailingSettingsUrl + '/change_users_pass').then(function (response) {
        if (response.ok) {
            response.json()
                .then(emailTemplate => $('#changeUsersPassTemplateSummernote').summernote('code', emailTemplate.textValue))
        }
    })
}

/**
 * Функция отправляет запрос и получает шаблон для рассылки подтверждения активации пользователя
 */
function fetchChangeActivateUserTemplate() {
    fetch( apiTemplatesMailingSettingsUrl + '/activate_user').then(function (response) {
        if (response.ok) {
            response.json()
                .then(emailTemplate => $('#activateUserTemplateSummernote').summernote('code', emailTemplate.textValue))
        }
    })
}
/**
 * функция сохранения изменённого шаблона
 */
function handleSaveTemplateButton() {
    if (document.getElementById('editPriceChangeTemplateSummernote').value !== '') {
        let commonSetting = {
            settingName: "price_change_distribution_template",
            textValue: document.getElementById('editPriceChangeTemplateSummernote').value
        }
        fetch(apiCommonSettingsUrl, {
            headers: myHeaders,
            method: 'PUT',
            body: JSON.stringify(commonSetting)

        }).then(function (response) {
            if (response.ok) {
                toastr.success("Шаблон успешно сохранён")
            }
        })
    } else {
        toastr.error("Заполните все поля шаблона")
    }
}

/**
 * функция сохранения изменённого шаблона TemplateResetPassword
 */
function handleSaveTemplateResetPasswordButton() {
    if (document.getElementById('sendConfirmationTokenToResetPasswordTemplateSummernote').value !== '') {
        let templatesMailingSettings = {
            settingName: "send_confirmation_token_to_reset_password",
            textValue: document.getElementById('sendConfirmationTokenToResetPasswordTemplateSummernote').value
        }
        fetch(apiTemplatesMailingSettingsUrl, {
            headers: myHeaders,
            method: 'PUT',
            body: JSON.stringify(templatesMailingSettings)

        }).then(function (response) {
            if (response.ok) {
                toastr.success("Шаблон успешно сохранён")
            }
        })
    } else {
        toastr.error("Заполните все поля шаблона")
    }
}

/**
 * функция сохранения изменённого шаблона TemplateRegNewAccount
 */
function handleSaveTemplateRegNewAccountButton() {
    if (document.getElementById('regNewAccountTemplateSummernote').value !== '') {
        let templatesMailingSettings = {
            settingName: "reg_new_account",
            textValue: document.getElementById('regNewAccountTemplateSummernote').value
        }
        fetch(apiTemplatesMailingSettingsUrl, {
            headers: myHeaders,
            method: 'PUT',
            body: JSON.stringify(templatesMailingSettings)

        }).then(function (response) {
            if (response.ok) {
                toastr.success("Шаблон успешно сохранён")
            }
        })
    } else {
        toastr.error("Заполните все поля шаблона")
    }
}

/**
 * функция сохранения изменённого шаблона ChangeUsersMailTemplate
 */
function handleSaveTemplateChangeUsersMailButton() {
    if (document.getElementById('changeUsersMailTemplateSummernote').value !== '') {
        let templatesMailingSettings = {
            settingName: "change_users_mail",
            textValue: document.getElementById('changeUsersMailTemplateSummernote').value
        }
        fetch(apiTemplatesMailingSettingsUrl, {
            headers: myHeaders,
            method: 'PUT',
            body: JSON.stringify(templatesMailingSettings)

        }).then(function (response) {
            if (response.ok) {
                toastr.success("Шаблон успешно сохранён")
            }
        })
    } else {
        toastr.error("Заполните все поля шаблона")
    }
}

/**
 * функция сохранения изменённого шаблона ChangeUsersPassTemplate
 */
function handleSaveTemplateChangeUsersPassButton() {
    if (document.getElementById('changeUsersPassTemplateSummernote').value !== '') {
        let templatesMailingSettings = {
            settingName: "change_users_pass",
            textValue: document.getElementById('changeUsersPassTemplateSummernote').value
        }
        fetch(apiTemplatesMailingSettingsUrl, {
            headers: myHeaders,
            method: 'PUT',
            body: JSON.stringify(templatesMailingSettings)

        }).then(function (response) {
            if (response.ok) {
                toastr.success("Шаблон успешно сохранён")
            }
        })
    } else {
        toastr.error("Заполните все поля шаблона")
    }
}

/**
 * функция сохранения изменённого шаблона activateUserTemplateSummernote
 */
function handleSaveTemplateActivateUserButton() {
    if (document.getElementById('activateUserTemplateSummernote').value !== '') {
        let templatesMailingSettings = {
            settingName: "activate_user",
            textValue: document.getElementById('activateUserTemplateSummernote').value
        }
        fetch(apiTemplatesMailingSettingsUrl, {
            headers: myHeaders,
            method: 'PUT',
            body: JSON.stringify(templatesMailingSettings)

        }).then(function (response) {
            if (response.ok) {
                toastr.success("Шаблон успешно сохранён")
            }
        })
    } else {
        toastr.error("Заполните все поля шаблона")
    }
}