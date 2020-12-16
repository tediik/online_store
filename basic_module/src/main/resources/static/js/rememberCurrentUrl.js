function getCurrentUrlJs() {
    return window.location;
}

function returnCurrentUrl() {
    let currentUrlFromJs = getCurrentUrlJs();
    //console.log("1111111111111111111111111111111")
    fetch("/api/currentUrl", {
        method: "POST",
        body:currentUrlFromJs,
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    })
}