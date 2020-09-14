let myHeaders = new Headers()
myHeaders.append('Content-type', 'application/json; charset=UTF-8')

document.getElementById('emailDistributionCheckbox').addEventListener('change', distributionCheckboxHandler)
document.getElementById('saveChanges').addEventListener('click', handleAcceptButton)
document.getElementById('stop').addEventListener('click', handleStopButton)

function handleStopButton() {
    fetch('/api/manager/scheduling/stop').then(response => console.log(response))
}


function handleAcceptButton() {
    let taskSettings = {
        id: 1,
        taskName: "stockMailSending",
        startTime: document.getElementById('distributionTimeInput').value,
    }
    fetch('/api/manager/scheduling/start', {
        method: 'POST',
        headers: myHeaders,
        body: JSON.stringify(taskSettings)
    }).then(response => console.log(response))
}

function distributionCheckboxHandler() {
    if (document.getElementById('emailDistributionCheckbox').checked === true) {
        document.getElementById('distributionTimeDiv').style.visibility = 'visible'
    } else {
        document.getElementById('distributionTimeDiv').style.visibility = 'hidden'
    }
}
