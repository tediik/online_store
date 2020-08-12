///////////////////////////////////////////////////////////////
/////////////////////Вывод акций///////////////////////////
$(document).ready(function () {
    create();
});


function create() {
    $("#stocksDiv").empty();

    $.ajax("/rest/allStocks", {
        dataType: "json",
        success: function (data) {
            const stocks = JSON.parse(JSON.stringify(data));


            for (let i = 0; i < stocks.length; i++) {
                let out = $("<div>").attr("id", stocks[i].id);
                out.append("" +
                    "<div class=\"card mb-3\">" +
                    "<div class=\"row no-gutters\">" +
                    "<div class=\"col-md-4\">" +
                    "<img class=\"card-img\" src=\"../static/img/stocks/1.jpg\" width=\"250\" >" +
                    "</div>" +

                    "<div class=\"col-md-6\">" +
                    "<div class=\"card-body\">" +
                    "<h3 class='card-title'>" + stocks[i].stock_title + "</h3>" +
                    "<p class=\"card-text\">" + stocks[i].stock_text + "</p>" +
                    "<p class=\"card-date\">" + stocks[i].startDate + "</p>" +
                    "<p class=\"card-date\">" + stocks[i].endDate + "</p>" +
                    "</div>" +
                    "</div>" +

                    "<div class=\"col-md-2\">" +

                    "<button onclick='getStockForEdit(" + stocks[i].id + ")' class=\"btn btn-md btn-info mt-5 mr-2\" data-toggle='modal' data-target='#editStockModal'>" + "Edit" + "</button>" +
                    "<button onclick='deleteStock(" + stocks[i].id + ")' class=\"warning btn btn-md btn-danger mt-2 mr-2\"  >" + "Delete" + "</button>" +

                    "</div>" +
                    "</div>" +
                    "</div>"
                );

                $("#stocksDiv").append(out)
            }

        }
    })
}

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
            create();

            toastr.success('Акция успешно добавлена!', {timeOut: 5000})
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
            create();
            toastr.info('Акция успешно отредактирована!', {timeOut: 5000})
            // alert("yes!!!")
            // document.body.style.background = "green"
        },
        error: function (er) {
            alert("error!")
            document.body.style.background = "red"
        }
    })
}


////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////Удалить пользователя из таблицы/////////////////////

function deleteStock(id) {
    $.ajax({
            url: "/rest/" + id,
            type: "DELETE",
            contentType: "application/json",
            success: function (data) {

                $("#stocksDiv #" + id).remove();
                console.log(data)

                toastr.warning('Акция успешно удалена!', {timeOut: 5000})
            },
            error: function (er) {
                console.log(er)
            }
        }
    );
}

///////////////////////////////////////////////////////////////////////////////////////////////



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

















