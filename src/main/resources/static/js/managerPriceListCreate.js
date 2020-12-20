/**
 * event listener
 */
document.getElementById('savePriceTimeChanges').addEventListener('click', handleSaveButton)

/**
 * Function fills DailyPriceForm according to received settings
 * @param settings received from db
 */
$(document).ready(function () {
    fetch(apiManagerSchedulingUrl + '/dailyPriceCreate').then(function (response) {
        if (response.status === 200) {
            response.json().then(settings => document.getElementById('priceTimeInput').value = settings.startTime)
        }
    })
})

/**
 * function handles accept time change form button
 */
function handleSaveButton(event) {
    event.preventDefault()
    let taskSettings = {
        taskName: "dailyPriceCreate",
        active: 'true',
        startTime: document.getElementById('priceTimeInput').value,
    }
    if (document.getElementById('priceTimeInput').value !== '') {
        fetch(apiManagerSchedulingUrl + '/dailyPriceCreate', {
            method: 'POST',
            headers: myHeaders,
            body: JSON.stringify(taskSettings)
        }).then(function (response) {
            if (response.status === 200) {
                popupWindow('#infoDiv', 'Настройки времени сохранены!', 'success')
            } else {
                popupWindow('#infoDiv', 'Настройки не сохранены', 'error')
            }
        })
    } else {
        invalidField('Введите время', document.getElementById('priceTimeInput'))
    }
}
