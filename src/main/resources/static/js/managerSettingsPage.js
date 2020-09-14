/**
 * Global variables declaration
 * @type {string}
 */
let apiManagerSchedulingUrl = "/api/manager/scheduling"
let myHeaders = new Headers()
myHeaders.append('Content-type', 'application/json; charset=UTF-8')

/**
 * event listener
 */
document.getElementById('saveChanges').addEventListener('click', handleAcceptButton)

$(document).ready(function () {
    fetchStockMailDistributionSettings()
})

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
    fetch(apiManagerSchedulingUrl + '/stockMailDistribution', {}).then(function (response) {
        if (response.status === 200) {
            response.json().then(settings => fillStockMailDistributionForm(settings))
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
    fetch(apiManagerSchedulingUrl + actionUrl, {
        method: 'POST',
        headers: myHeaders,
        body: JSON.stringify(taskSettings)
    }).then(response => console.log(response))
}
