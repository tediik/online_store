document.getElementById('savePriceChangeTemplateChanges').addEventListener('click', handleSaveTemplateButton)
document.getElementById('saveSendConfirmationTokenToResetPasswordTemplateChanges').addEventListener('click', handleSaveTemplateResetPasswordButton)

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
    fetch( apiResetPasswordTemplate + '/send_confirmation_token_to_reset_password').then(function (response) {
        if (response.ok) {
            response.json()
                .then(emailTemplate => $('#sendConfirmationTokenToResetPasswordTemplateSummernote').summernote('code', emailTemplate.textValue))
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
 * функция сохранения изменённого шаблона
 */
function handleSaveTemplateResetPasswordButton() {
    if (document.getElementById('sendConfirmationTokenToResetPasswordTemplateSummernote').value !== '') {
        let templatesMailingSettings = {
            settingName: "send_confirmation_token_to_reset_password",
            textValue: document.getElementById('sendConfirmationTokenToResetPasswordTemplateSummernote').value
        }
        fetch(apiResetPasswordTemplate, {
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