let stockId = window.location.href.substring(42)

$(document).ready(function () {
    // alert(stockId)
    stockData()
})

function stockData() {
    fetch('/api/stock/' + stockId)
        .then(response => response.json())
        .then(stock => {
            $('#stockImg').attr('src', '/uploads/images/stocks/' + stock.stockImg)
            // $('#stockTitle').innerHTML = stock.stockTitle
            document.getElementById("stockTitle").innerHTML = stock.stockTitle;
            document.getElementById("stockText").innerHTML = stock.stockText;
        })
}
