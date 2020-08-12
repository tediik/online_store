///////////////////////////////////////////////////////////////
/////////////////////Вывод акций///////////////////////////

$.ajax("/rest/allStocks", {
    dataType: "json",
    success: function (data) {
        const stocks = JSON.parse(JSON.stringify(data));


        for (let i = 0; i < stocks.length; i++) {
            let out = $("<div>").attr("id", stocks[i].id);
            out.append("" +
                "<div>" + "<img alt=\"...\">" + "</div>" +
                "<h3>" + "Заголовок: " + "</h3>");





            //     out.append "<h3> : + stocks[key].stock_title < /h3>" + '<br>';
            // out += 'Описание: ' + stocks[key].stock_text + '<br>';
            // out += 'Дата: ' + stocks[key].startDate + '<br>';
            // out += 'Дата: ' + stocks[key].endDate + '<br>';
            //
            // out += '<button class="">';

        // document.getElementById('out').innerHTML = out;
        $("#stocksDiv").append(out)
        }

    }
})




///////////////////////////////////////////////////////////////
//////////////////////Добавить акцию/////////////////////////

function addStock() {


    const stock = {
        id: $('#id').val(),
        stockImg: $('#addStockImg').val(),
        stockTitle: $('#addStockTitle').val(),
        stockText: $('#addStockText').val(),
        startDate: $('#addStartDate').val(),
        endDate: $('#addEndDate').val(),
    };


    $.ajax({
        // processData: false,
        url: "/rest/addStock",
        data: JSON.stringify(stock),
        dataType: 'json',
        type: 'POST',
        contentType: 'application/JSON; charset=utf-8',
        success: function (data) {
            location.reload()
            // alert("yes!!!")
            // document.body.style.background = "green"

        },
        error: function (er) {
            // alert("error!")
            // document.body.style.background = "red"
        }

    })
}


///////////////////////////////////////////////////////////////////////////
//////////////////////Редактировать акцию////////////////////////////////

function getStockForEdit(id) {
    $.ajax({
        type: "GET",
        url: "/rest/" + id,
        dataType: 'json',
        success: function (stock) { //заполнение таблицы данными
            $(".modal-body #Eid").val(stock.id)
            $(".modal-body #editStockTitle").val(stock.stock_title)
            $(".modal-body #editStockText").val(stock.stock_text)
            $(".modal-body #editStockImg").val(stock.stock_img)
            $(".modal-body #editStartDate").val(stock.startDate)
            $(".modal-body #editEndDate").val(stock.endDate)
        }
    });
}

////Эдит акции///////////

function updateStock() {

    const stock = {
        id: $('#Eid').val(),
        stockImg: $('#editStockImg').val(),
        stockTitle: $('#editStockTitle').val(),
        stockText: $('#editStockText').val(),
        startDate: $('#editStartDate').val(),
        endDate: $('#editEndDate').val(),

    };


    $.ajax({
        // processData: false,
        url: "/rest/editStock",
        data: JSON.stringify(stock),
        dataType: 'json',
        type: 'PUT',
        contentType: 'application/JSON; charset=utf-8',
        success: function (data) {
            location.reload()
            // alert("yes!!!")
            // document.body.style.background = "green"

        },
        error: function (er) {
            alert("error!")
            document.body.style.background = "red"
        }

    })
}

//////////////////////////////////////////////////////////////////
////////////////////////Фильтры акций//////////////////////////

//


// const start = new Date("February 29, 2020").getTime();
// const end = new Date("March 1, 2021").getTime();

// const time = new Date().getTime();
// if(time > start && time < end + 60*60*24)
// alert("Только с 29 февраля по 2 марта при заказе утюга, получите асфальтоукладчик бесплатно!");


// const filters = document.querySelector('#filters');
//
// filters.addEventListener('input', filterStocks);
//
// function filterStocks() {
//     const
//
//         sizes = [...filters.querySelectorAll('#size input:checked')].map(n => n.value),
//
//     outputStocks(DATA.filter(n => (!sizes.length || sizes.includes(n.size))));
// }
//
// const DATA = [
//
// ];

















