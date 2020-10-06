document.getElementById('savePriceChangeTemplateChanges').addEventListener('click', handleSaveTemplateButton)

$(document).ready(function () {
    $('#editPriceChangeTemplateSummernote').summernote({
        height: 500
    });
    fetchEmailPriceTemplate();
})

/**
 * Функция отправляет запрос и получает шаблон для рассылки оповещений об изменении цены
 */
function fetchEmailPriceTemplate() {
    fetch(apiCommonSettingsUrl + '/price_change_distribution_template').then(function (response) {
        if (response.ok=== 200) {
            response.json()
                .then(emailTemplate => $('#editPriceChangeTemplateSummernote').summernote('code', emailTemplate.textValue))
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