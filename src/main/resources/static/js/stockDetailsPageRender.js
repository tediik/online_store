/**
 * stockId for tests
 * @type {number}
 */
let stockId = 58

/**
 * Global variables declaration
 */
const sharedStockApiUrl = "/global/api/sharedStock/"
const stockApiUrl = "/global/api/stock/"
let urlToShare = document.location.href
let myHeaders = new Headers()
myHeaders.append('Content-type', 'application/json; charset=UTF-8')

$(document).ready(function () {
    fetchStockDetailsPage(stockId)
    generateShareLinks()
})
/**
 * Share buttons event listeners
 */
document.getElementById('shareFacebookLink').addEventListener('click', handleShareButton)
document.getElementById('shareVKLink').addEventListener('click', handleShareButton)

/**
 * generates share links
 */
function generateShareLinks() {
    let encoded = encodeURIComponent(urlToShare)
    let facebookShareLink = `https://www.facebook.com/sharer/sharer.php?u=${encoded}`
    let vkShareLink = `https://vk.com/share.php?url=${urlToShare}`
    $('#shareFacebookLink').attr("href", facebookShareLink)
    $('#shareVKLink').attr("href", vkShareLink)
}

/**
 * function that handles share buttons.
 * Sends POST fetch request to shared stock api
 * name of social network, which button was clicked
 */
function handleShareButton() {
    let socialNetworkName = $(this).attr('data-socialNetworkName')

    let body = {
        stock: {
            id: stockId
        },
        socialNetworkName: socialNetworkName
    }
    fetch(sharedStockApiUrl, {
        method: 'POST',
        headers: myHeaders,
        body: JSON.stringify(body)
    }).then(response => response.text()).then(text => console.log(text))
}

/**
 * Function that renders Stock detail page
 * @param stockFromDb - accept Stock
 */
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
            response.text().then(text => console.log(text))
        } else {
            response.json().then(stockFromDb => renderStockDetailPage(stockFromDb))
        }
    }).catch(error => console.log(error))
}


