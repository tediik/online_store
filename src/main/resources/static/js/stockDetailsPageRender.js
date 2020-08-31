/**
 * stockId for tests
 * @type {number}
 */
let stockId = 1

const stockApiUrl = "/api/stock/"
let myHeaders = new Headers()
myHeaders.append('Content-type', 'application/json; charset=UTF-8')


function handleFacebookShareButton() {
    fetch(stockApiUrl + stockId, {
        headers: myHeaders,
        method: 'POST'
    }).then(response => response.text()).then(text => console.log(text))
}

function renderStockDetailPage(stockFromDb) {
    $('#stockHeader').append(`
        ${stockFromDb.stockTitle}
    `)
    $('#stockBody').append(`
    ${stockFromDb.stockText}
    `)
}

/**
 * function makes fetch to customer Rest controller, receives stock from db in json
 * and start render page by renderStockDetailPage function
 * @param stockId - id of stock to render
 */
function fetchStockDetailsPage(stockId) {
    fetch(stockApiUrl + stockId, {
        headers: myHeaders,
        method: 'GET'
    }).then(function (response) {
        if (response.status !== 200) {
            // пишем код если респонс не 200
        } else {
            response.json().then(stockFromDb => renderStockDetailPage(stockFromDb))
        }
    }).catch(error => console.log(error))
}

fetchStockDetailsPage(stockId)

