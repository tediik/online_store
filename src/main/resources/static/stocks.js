///////////////////////////////////////////////////////////////
//////////////////////Добавить акцию/////////////////////////

function addStock() {


    const stock = {
        id: $('#id').val(),
        stockImg: $('#addStockImg').val(),
        stockTitle: $('#addStockTitle').val(),
        stockText: $('#addStockText').val(),
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


///////получение даты и вывод акции/////////////////////

const start = new Date("February 29, 2020").getTime();
const end = new Date("March 1, 2021").getTime();

// const time = new Date().getTime();
// if(time > start && time < end + 60*60*24)
    // alert("Только с 29 февраля по 2 марта при заказе утюга, получите асфальтоукладчик бесплатно!");
















