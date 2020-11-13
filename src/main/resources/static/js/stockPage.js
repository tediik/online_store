let listOfStocks;

$(function () {
    listOfStocks = fetch("/rest/allStocks")
        .then(response => response.json())
        .then(() => {
        console.log(listOfStocks);
    });
})
