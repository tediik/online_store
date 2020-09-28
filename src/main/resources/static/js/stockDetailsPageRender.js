
/**
 * Global variables declaration
 */
const sharedStockApiUrl = "/global/api/sharedStock/"
let urlToShare = document.location.href
let myHeaders = new Headers()
myHeaders.append('Content-type', 'application/json; charset=UTF-8')

$(document).ready(function () {
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
    let id = $(this).attr('data-id')

    let body = {
        stock: {
            id: id
        },
        socialNetworkName: socialNetworkName
    }
    fetch(sharedStockApiUrl, {
        method: 'POST',
        headers: myHeaders,
        body: JSON.stringify(body)
    }).then(response => response.text()).then(text => console.log(text))
}


