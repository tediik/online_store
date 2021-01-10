function getCurrentUrlJs() {
    return window.location;
}

function returnCurrentUrl() {
    let currentUrlFromJs = getCurrentUrlJs();
    fetch("/api/currentUrl", {
        method: "POST",
        body:currentUrlFromJs,
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    })
}