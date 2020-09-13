document.getElementById('emailDistributionCheckbox').addEventListener('change', distributionCheckboxHandler)

function distributionCheckboxHandler() {
    if (document.getElementById('emailDistributionCheckbox').checked === true){
        document.getElementById('distributionTimeDiv').style.visibility = 'visible'
    } else {
        document.getElementById('distributionTimeDiv').style.visibility = 'hidden'
    }
}
