/**
 * Global variables declaration
 */
let urlToShare = document.location.href;

$(document).ready(function () {
    generateShareLinks();
})

/**
 * Share buttons event listeners
 */
document.getElementById('shareVKLink').addEventListener('click', handleShareButton);
document.getElementById('shareFbLink').addEventListener('click', handleShareButton);
document.getElementById('shareOkLink').addEventListener('click', handleShareButton);
document.getElementById('shareTwitterLink').addEventListener('click', handleShareButton);
document.getElementById('shareCopyLink').addEventListener('click', handleShareButton);
document.getElementById('shareCopyLink').addEventListener('click', copyLink);


/**
 * Generates share links
 */
function generateShareLinks() {
    let encoded = encodeURIComponent(urlToShare);
    let facebookShareLink = `https://www.facebook.com/sharer/sharer.php?u=${encoded}`;
    let vkShareLink = `https://vk.com/share.php?url=${urlToShare}`;
    let okShareLink = `https://connect.ok.ru/offer?url=${urlToShare}`;
    let twitterShareLink = `http://twitter.com/share?&url=${urlToShare}`;
    $('#shareFbLink').attr("href", facebookShareLink);
    $('#shareVKLink').attr("href", vkShareLink);
    $('#shareOkLink').attr("href", okShareLink);
    $('#shareTwitterLink').attr("href", twitterShareLink);
}

function copyLink() {
    let link = document.createElement('input');
    link.value = urlToShare;
    document.body.appendChild(link);
    link.select();
    document.execCommand('copy');
    document.body.removeChild(link);
}

/**
 * function that handles share buttons.
 * Sends POST fetch request to shared stock api
 * name of social network, which button was clicked
 */
function handleShareButton() {
    let sharedStockApiUrl;
    let body;
    let socialNetworkName = $(this).attr('data-socialNetworkName');
    let id = $(this).attr('data-id');

    if (urlToShare.includes('news')) {
        sharedStockApiUrl = '/global/api/sharedNews/';
        body = {
            news: {
                id: id
            },
            socialNetworkName: socialNetworkName
        }
    } else if (urlToShare.includes('stock')) {
        sharedStockApiUrl = '/global/api/sharedStock/';
        body = {
            stock: {
                id: id
            },
            socialNetworkName: socialNetworkName
        }
    }

    fetch(sharedStockApiUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify(body)
    }).then(response => response.status)
        .then(text => console.log(text));
}