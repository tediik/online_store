/**
 * stockId for tests
 * @type {number}
 */
let stockId = 1
const sharedStockApiUrl = "/api/sharedStock/"
const stockApiUrl = "/api/stock/"
let urlToShare = document.location.href
let myHeaders = new Headers()
myHeaders.append('Content-type', 'application/json; charset=UTF-8')


document.getElementById('shareButtons').innerHTML = VK.Share.button(urlToShare, {
    type: 'custom ',
    text: '<button type="button" class="btn btn-secondary" id="vkShareButton">Share on VK</button>'
});
/**
 * function that handles facebook share button.
 * If page was shared lunches function that add row into SharedStock
 */
$('#fbShareButton').click(function () {
    FB.ui({
        method: 'feed',
        link: 'https://www.java-mentor.com/',
    }, function (response) {
        if (response === null) {
            console.log('was not shared');
        } else {
            handleShareButton("facebook")
            console.log('shared - post id is ' + response.post_id);
        }
    });
});

$('#vk_share_button').click(function () {
    handleShareButton("vk")
})

/**
 * Сделал для теста что бы каждый раз не публиковать акцию у себя на странице
 */
// $('#fbShareButton').click(function() {
//             handleShareButton("facebook")
// });

/**
 * function that handles share buttons.
 * Sends POST fetch request to shared stock api
 * @param socialNetworkName - name of social network, which button was clicked
 */
function handleShareButton(socialNetworkName) {
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
            //TODO пишем код если респонс не 200
        } else {
            response.json().then(stockFromDb => renderStockDetailPage(stockFromDb))
        }
    }).catch(error => console.log(error))
}

fetchStockDetailsPage(stockId)

