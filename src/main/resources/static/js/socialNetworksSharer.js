/**
 * Global variables declaration
 */
const urlToShare = document.location.href;
let splitUrl = urlToShare.split('/');
const id = splitUrl[splitUrl.length - 1];

console.log(id);
console.log(urlToShare);

$(document).ready(function () {
    $('head').append('<link href="/static/css/socialShare.css" rel="stylesheet" type="text/css">');
    renderShareLinks();
    generateShareLinks();
    document.getElementById('shareVKLink').addEventListener('click', handleShareButton);
    document.getElementById('shareFbLink').addEventListener('click', handleShareButton);
    document.getElementById('shareOkLink').addEventListener('click', handleShareButton);
    document.getElementById('shareTwitterLink').addEventListener('click', handleShareButton);
    document.getElementById('shareCopyLink').addEventListener('click', handleShareButton);
    document.getElementById('shareCopyLink').addEventListener('click', copyLink);

});

/**
 * Share buttons event listeners
 */
function renderShareLinks() {
    let shareSocialNetworksBody = document.querySelector('#insertSocialNetworksBlock');
    let insertBody = `<div class="buttonForSocialShare">
                        <a href="#" id="dLabel" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"
                           data-offset="-125, 40"><img src="/static/img/share/shareButton.svg" alt="shareButton"></a>
                        <div class="menuForSocialShare">
                            <div class="dropdown-menu" aria-labelledby="dLabel">
                                <a class="dropdown-item" id="shareVKLink" data-socialNetworkName="vk" target="_blank">
                                    <img src="/static/img/share/vk_logo.svg" alt="vk_logo"> ВКонтакте
                                </a>
                                <a class="dropdown-item" id="shareFbLink" data-socialNetworkName="facebook" target="_blank">
                                    <img src="/static/img/share/fb_logo.svg" alt="fb_logo"> Facebook
                                </a>
                                <a class="dropdown-item" id="shareOkLink" data-socialNetworkName="odnoklassniki" target="_blank">
                                    <img src="/static/img/share/ok_logo.svg" alt="ok_logo"> Одноклассники
                                </a>
                                <a class="dropdown-item" id="shareTwitterLink" data-socialNetworkName="twitter" target="_blank">
                                    <img src="/static/img/share/tw_logo.svg" alt="tw_logo"> Twitter
                                </a>
                                <a class="dropdown-item" id="shareCopyLink" data-socialNetworkName="copyLink">
                                    <img src="/static/img/share/copy_link.svg" alt="copy_link"> Скопировать ссылку
                                </a>
                            </div>
                        </div>
                    </div>`;
    shareSocialNetworksBody.insertAdjacentHTML("beforeend", insertBody);
}


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
    toastr.error("My name is Inigo Montoya. You killed my father. Prepare to die!");
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