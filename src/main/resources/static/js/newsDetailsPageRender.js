const url = window.location.href;

function shareLink() {
    new Clipboard('.copy_link', {
        text: function () {
            return document.location.href;
        }
    });
}

document.getElementById('shareFacebookLink').addEventListener('click', handleShareButton)
document.getElementById('shareVkLink').addEventListener('click', handleShareButton)

function handleShareButton() {
    let socialNetworkName = $(this).attr('data-socialNetworkName')
    let id = $(this).attr('data-id')
    let body;

    if (decodeURIComponent(url).includes('news')) {
        console.log("news shared " + socialNetworkName)
    } else if (url.contains('stock')) {
        body = {
            stock: {
                id: id
            },
            socialNetworkName: socialNetworkName
        }
        fetch('/global/api/sharedStock/', {
            method: 'POST',
            body: JSON.stringify(body)
        }).then(response => response.text())
            .then(text => console.log(text))
    }
}

