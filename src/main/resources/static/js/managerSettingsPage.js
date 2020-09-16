/**
 * Global variables declaration
 * @type {string}
 */
let apiManagerSchedulingUrl = "/api/manager/scheduling"
let apiCommonSettingsUrl = "/api/commonSettings"
let myHeaders = new Headers()
myHeaders.append('Content-type', 'application/json; charset=UTF-8')

/**
 * event listener
 */
document.getElementById('saveSettingsChanges').addEventListener('click', handleAcceptButton)
document.getElementById('saveTemplateChanges').addEventListener('click', handleSaveTemplateButton)

$(document).ready(function () {
    fetchStockMailDistributionSettings()
    fetchEmailTemplate()
    $('#editTemplateSummernote').summernote({
        height: 500
    });
})

/**
 * function handled
 */
function handleSaveTemplateButton() {
    if (document.getElementById('editTemplateSummernote').value !== '') {
        let commonSetting = {
            settingName: "stock_email_distribution_template",
            textValue: document.getElementById('editTemplateSummernote').value
        }
        fetch(apiCommonSettingsUrl, {
            headers: myHeaders,
            method: 'PUT',
            body: JSON.stringify(commonSetting)

        }).then(function (response) {
            if (response.status === 200) {
                console.log("setting modified")
            }
        })
    } else {
        invalidField("Заполните поля редактирования шаблона", document.getElementById('editTemplateSummernote'))
    }
}

/**
 * Function fills StockMailDistributionForm according to received settings
 * @param settings received from db
 */
function fillStockMailDistributionForm(settings) {
    document.getElementById('emailDistributionCheckbox').checked = settings.active
    document.getElementById('distributionTimeInput').value = settings.startTime
}

/**
 * function send fetch request to get StockMailDistributionTask settings
 */
function fetchStockMailDistributionSettings() {
    fetch(apiManagerSchedulingUrl + '/stockMailDistribution').then(function (response) {
        if (response.status === 200) {
            response.json().then(settings => fillStockMailDistributionForm(settings))
        }
    })
}

/**
 * function send fetch request to get Email template for StockMailDistributionTask
 */
function fetchEmailTemplate() {
    fetch(apiCommonSettingsUrl + '/stock_email_distribution_template').then(function (response) {
        if (response.status === 200) {
            response.json()
                .then(emailTemplate => $('#editTemplateSummernote').summernote('code', emailTemplate.textValue))
        }
    })
}

/**
 * function handles accept form button if checkbox is checked it sends request to start utl
 * else send request to stop url
 */
function handleAcceptButton() {
    let actionUrl;
    if (document.getElementById('emailDistributionCheckbox').checked) {
        actionUrl = '/stockMailDistribution/start'
    } else {
        actionUrl = '/stockMailDistribution/stop'
    }

    let taskSettings = {
        taskName: "stockMailDistribution",
        active: document.getElementById('emailDistributionCheckbox').checked,
        startTime: document.getElementById('distributionTimeInput').value,
    }
    if (document.getElementById('distributionTimeInput').value !== ''){
        fetch(apiManagerSchedulingUrl + actionUrl, {
            method: 'POST',
            headers: myHeaders,
            body: JSON.stringify(taskSettings)
        }).then(response => console.log(response))
        successAction('#infoDiv', 'Настройки успешно изменены', 'success')
    } else {
        successAction('#infoDiv', 'Выбирите время рассылки', 'error')
    }
}

/**
 * Function creates alert message when fields in modal are invalid
 * @param text - text of message
 * @param focusField - field to focus
 */
function invalidField(text, focusField) {
    document.querySelector('#infoDiv').innerHTML = `<div class="alert alert-danger alert-dismissible fade show" role="alert">
                                                                    <strong>${text}</strong>
                                                                     <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                                                         <span aria-hidden="true">&times;</span>
                                                                     </button>
                                                                </div>`
    focusField.focus()
    window.setTimeout(function () {
        $('.alert').alert('close');
    }, 3000)
}

/**
 * function that shows success or error message
 * @param inputField - location where message appears
 * @param text - text of message
 * @param messageStatus - status success or error
 */
function successAction(inputField, text, messageStatus) {
    let successMessage = `<div class="alert text-center alert-success alert-dismissible" role="alert">
                            <strong>${text}</strong>
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span></button>
                       </div>`
    let alertMessage = `<div class="alert text-center alert-danger alert-dismissible" role="alert">
                            <strong>${text}</strong>
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span></button>
                       </div>`
    let message = ''

    if (messageStatus === "success") {
        message = successMessage
    } else if (messageStatus === "error") {
        message = alertMessage;
    }

    document.querySelector(`${inputField}`).innerHTML = message
    window.setTimeout(function () {
        $('.alert').alert('close');
    }, 5000)
}