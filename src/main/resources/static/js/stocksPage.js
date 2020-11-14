let listOfStocks;
let key;
let stockCell;
let allStocksList;

$(function () {
    fillStocks().then();
})

async function fillStocks() {
    listOfStocks = await fetch("/rest/allStocks").then(response => response.json())
    allStocksList = $('#allStocksList')
    allStocksList.empty();
    for (key in listOfStocks) {
        stockCell =
            `
            <div class="alert alert-info mt-2">
                <div><img src="../../uploads/images/stocks/${listOfStocks[key].stockImg}" class="next-slide"
                     alt="Ищите новые акции на нашем сайте"/></div>
                <div>${listOfStocks[key].stockTitle}</div>
                <div>${listOfStocks[key].stockText}</div>
                <div class="mt-2"><a href="/global/stockDetails/${listOfStocks[key].id}" class="btn btn-warning">Детальнее</a></div>
            </div>
            `;
        allStocksList.append(stockCell);
        console.log(listOfStocks[key].stockTitle);
    }
}