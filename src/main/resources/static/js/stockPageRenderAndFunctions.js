/**
 * Declaration of global variables
 */
let myHeaders = new Headers()
let sharedStockApiUrl = "/manager/api/sharedStock"
let stockApiUrl = "/manager/api/stock"
let stockImgUrl = "../../uploads/images/stocks/"
myHeaders.append('Content-type', 'application/json; charset=UTF-8')
const lastPage = {type: 'ALL', currentDate: new Date().toLocaleDateString(), number: 0, last: false};

$(document).ready(function () {
    handleSummernote()
    fetchStockList()

    /**
     * buttons 'click' event listeners
     */
    /*Sort Buttons*/
    document.getElementById('sortUp').addEventListener('click', handleSortButton)
    document.getElementById('sortDown').addEventListener('click', handleSortButton)
    /*Filter Buttons*/
    document.getElementById('stockFilters').addEventListener('click', defineFilterAndFetchList)
    /*New stock*/
    document.getElementById('newStockButton').addEventListener('click', handleAddNewStockButton)
    /*Modal window buttons*/
    document.getElementById('modalFooter').addEventListener('click', checkFields)

    document.getElementById('stocksDiv').addEventListener('click', handleStockDivButtons)

    $('#stockModal').on('hidden.bs.modal', function () {
        fetchStockList()
    })

    $(window).scroll(yHandler);
});

function yHandler() {
    if (lastPage.last) {
        return;
    }
    let stocksDiv = document.getElementById('stocksDiv');
    let contentHeight = stocksDiv.offsetHeight;
    let yOffset = window.pageYOffset;
    let y = yOffset + window.innerHeight;
    if (y >= contentHeight) {
        fetchStockList();
    }
}

/**
 * function validate fields in modal window
 * @param event
 */
function checkFields(event) {
    if (event.target.dataset.toggleId === 'submit') {
        let stockTitle = document.getElementById('stockTitle');
        let stockText = document.getElementById('stockText');
        let startDate = document.getElementById('startDate');
        let stockPublished;
        if (document.getElementById('published').checked) {
            stockPublished = "true";
        } else {
            stockPublished = "false";
        }
        console.log("stock: " + stockId + ". stockPublished: " + stockPublished);
        let filename = "default.jpg";

        try {
            let fakefilename = $('#fileImgInput')[0].files[0].name;
            if (fakefilename.indexOf('fakepath') === -1) {
                filename = fakefilename;
                console.log("Checking fields. Case1. filename = " + filename);
            } else {
                filename = $(fakefilename).val().replace(/C:\\fakepath\\/i, '')
                console.log("Checking fields. Case2. filename = " + filename);
                invalidModalField("Ошибка загрузки. Повторите выбор файла", stockImgUrl)
            }
        } catch (err) {
            console.log("name of file not found");
        }

        if (stockTitle.value === '') {
            invalidModalField("Заполните заголовок акции", stockTitle)
        } else if (stockText.value === "") {
            invalidModalField("Заполните описание акции", stockText)
        } else if (startDate.value === '') {
            invalidModalField("Заполните начальную дату", startDate)
        } else {
            handleSaveChangesButton(event, filename)
        }
    }
}

/**
 * function handles edit and delete buttons of main table
 * @param event
 */
function handleStockDivButtons(event) {
    let button = event.target.dataset.toggleId;
    if (button === "edit-stock") {
        handleEditButtonClick(event)
    } else if (button === "delete-stock") {
        handleDeleteButtonClick(event)
    }
}

/**
 * function defines filter for fetch
 * @param event
 */
function defineFilterAndFetchList(event) {
    lastPage.type = event.target.dataset.toggleId;
    lastPage.currentDate = new Date().toLocaleDateString();
    lastPage.number = 0;
    lastPage.last = false;
    fetchStockList();
}

/**
 * Fetch request to stock list
 */
function fetchStockList() {
    $.ajax(stockApiUrl + '/page', {
        headers: myHeaders,
        async: false,
        data: {page: lastPage.number, type: lastPage.type, currentDate: lastPage.currentDate},
        success: renderStockList,
        error: printStocksNotFoundMessage
    })
}

/**
 * function writes message that stocks not found
 */
function printStocksNotFoundMessage() {
    $("#stocksDiv").empty().append(`<Strong>Акций не найдено</Strong>`)
}

/**
 * function handles sort buttons 'click'
 */
function handleSortButton() {
    let list = document.getElementById('stocksDiv')
    let items = document.querySelectorAll('#stocksDiv li')
    let checkButton = $(this).attr('data-sort')
    let sorted = [...items].sort(function (a, b) {
        if (checkButton === "sortDown") {
            return b.getAttribute('rating') - a.getAttribute('rating');
        }
        return a.getAttribute('rating') - b.getAttribute('rating');
    })
    list.innerHTML = ''
    for (let li of sorted) {
        list.appendChild(li)
    }
}

/**
 * delete button handler
 * @param event - эвент откуда берем id элемента
 */
function handleDeleteButtonClick(event) {
    let stockId = event.target.dataset.stockId
    let doDelete = confirm(`Удалить акцию id: ${stockId}?`);
    if (doDelete) {
        console.log(`${stockId} will be deleted`)
        fetch(stockApiUrl + `/${stockId}`, {
            method: 'DELETE',
            headers: myHeaders
        }).then(function (response) {
            if (response.status === 200) {
                successActionMainPage("#mainWindowAlert", "Акция успешно удалена", "success")
                $(`#li-${stockId}`).remove()
            } else {
                successActionMainPage("#mainWindowAlert", "Акция не найдена", "error")
            }
        })
    }
    $('#stockModal').modal('hide')
}

// function fetchStock(stock, method) {
//     console.log("fetch stock: " + JSON.stringify(stock));
//     fetch(stockApiUrl, {
//         method: method,
//         headers: myHeaders,
//         body: JSON.stringify(stock)
//     }).then(function (response) {
//         if (response.status === 200) {
//             successActionMainPage("#mainWindowAlert", "Акция успешно сохранена", "success");
//         } else {
//             successActionMainPage("#mainWindowAlert", "Акция не сохранена", "error")
//         }
//     })
// }


// сохраняем картинку акции в БД
async function changeStockImage(upload_Id, form_data) {
    let uploadId = upload_Id
    if (form_data === null) {
        console.log("No file to upload stock picture");
    } else {
        let formdata = form_data;

        await fetch(`/rest/uploadStockImage/` + uploadId, {
            method: 'POST',
            body: formdata,
        }).then(response => {
            console.log("fetching rest/uploadStockImage. response = " + response);
            return response.ok;
        });
    }
}



/**
 * modal window "save changes" button handler
 */
function handleSaveChangesButton(event, file_name_stockImg) {
    let startDate = $('#startDate').val();
    console.log("$('#published').val() = " + $('#published').val());
    let stockPublished;
    if (document.getElementById('published').checked) {
        stockPublished = "true";
    } else {
        stockPublished = "false";
    }
    console.log("handleSaveChangesButton. published: " + stockPublished);
    // if ($('#published').val() === 'on' || $('#published').val() === 'true') {
    //     published = true;
    // }
    // console.log("handleSaveChangesButton. published: " + published);


    let uploadId = $('#stockId').val();

    // let file_data = $('#fileImgInput')[0].files[0]
    // let form_data = new FormData();
    // form_data.append("stockImg", file_data);
    // if (file_data != null) {
    //     changeStockImage(uploadId, form_data).then(function (response) {
    //         // if (response.status === 200) {
    //         console.log("Картинка акции успешно сохранена");
    //         // } else {
    //         //     console.log("Картинка акции не сохранена");
    //         // }
    //     });
    // }

    startDate = moment(startDate).format("YYYY-MM-DD")
    let endDate = ""
    if ($('#endDate').val() !== null || $('#endDate').val() !== "") {
        endDate = $('#endDate').val()
        endDate = moment(endDate).format("YYYY-MM-DD")
    }
    if (endDate === "Invalid date") {
        endDate = ""
    }


    //
    // if (file_data === null) {
    //     let form_data = new FormData();
    //     form_data.append("stockImg", 'default.jpg');
    // }

        const stock = {
            id: $('#stockId').val(),
            stockImg: file_name_stockImg,
            stockTitle: $('#stockTitle').val(),
            stockText: $('#stockText').summernote('code'),
            startDate: startDate,
            endDate: endDate,
            // stock: $('#stockTimeZone').val(),
            published: stockPublished
        }
        let method = (stock.id !== '' ? 'PUT' : 'POST')


        fetchStock(stock, method);


        function fetchStock(stock, method) {
            console.log("fetching..." + stock);
            fetch(stockApiUrl, {
                method: method,
                headers: myHeaders,
                body: JSON.stringify(stock)
            }).then(function (response) {
                if (response.status === 200) {
                    successActionMainPage("#mainWindowAlert", "Акция успешно сохранена", "success")
                } else {
                    successActionMainPage("#mainWindowAlert", "Акция не сохранена", "error")
                }
            })
        }


    $('#stockModal').modal('hide')
}


$(function () {
    $('#deleteStockImgBtn').on('click', function () {
        let deleteId = $('#stockId').val();
        $.ajax(
            {
                type: 'DELETE',
                url: '/rest/deleteStockImage/' + deleteId,
                contentType: false,
                processData: false,
                cache: false,
                success: function (data) {
                    console.log("stockImage " + deleteId + " deleted.")
                },
                error: function (jqXhr, textStatus, errorThrown) {
                    console.log(errorThrown);
                }
            });
    })

})

/**
 * При изменении содержимого формы для ввода картинки, удаляет старую картинку с диска и БД
 *
 */
$("#fileImgInput" +
    "").change(function () {
    let deleteId = $('#stockId').val();

    $.ajax(
        {
            type: 'DELETE',
            url: '/rest/deleteStockImage/' + deleteId,
            contentType: false,
            processData: false,
            cache: false,
            success: function () {
                console.log("stockImage " + deleteId + " deleted.");
            },
            error: function (jqXhr, textStatus, errorThrown) {
                console.log(errorThrown);
            }
        });
});

/**
 * Edit button handler
 * @param event
 */
function handleEditButtonClick(event) {
    $('#stockIdDiv').removeClass('d-none')
    $('.modal-title').text("Редактировать акцию")
    let stockId = event.target.dataset.stockId
    stockModalClearFields()

    function renderModalWindowEdit(stock) {
        let stockText = stock.stockText
        $("#stockId").val(stock.id);
        $("#stockTitle").val(stock.stockTitle);
        $('#stockText').summernote('code', stockText);
        $("#startDate").val(stock.startDate);
        $("#endDate").val(stock.endDate);
        //$("#fileImgInput").val(stock.stockImg);
        $("#published").prop('checked', stock.published);
    }

    fetch(stockApiUrl + `/${stockId}`, {
        method: 'GET',
        headers: myHeaders
    }).then(response => response.json()).then(stock => renderModalWindowEdit(stock))
}


// /**
//  * При изменении чекбокса "Опубликовать на гл.странице" запускает цепочку проверки чекбоксов
//  *
//  */
// $("#published" +
//     "").change(function () {
//     let stockPublished = $('#published').val();
//     console.log("stockPublished:  " + stockPublished);
// });

/**
 * Обработка чекбокса #published
 * если галка стоит, то установить published = true
 * и наоборот
 */
function chekboxPublished(o) {
    if (o.checked === true) {
        $("#published").val(true);
        let uploadId = $('#stockId').val();
        // console.log('stockId = ' + uploadId);
        // changeStockPublished(uploadId, "true").then(function (response) {
             console.log("Акция отмечена на публикацию.");
        // });
    } else {
        console.log("Published checkbox = " + o.checked);
        $("#published").val(false);
        // let uploadId = $('#stockId').val();
        // changeStockPublished(uploadId, "false").then(function (response) {
        //     console.log("Акция снята с публикации");
        // });
    }

    // // сохраняем значение чекбокса в БД
    // async function changeStockPublished(upload_Id, upload_data) {
    //     let uploadId = upload_Id
    //     console.log("Сохраняем чекбокс. upload_data:  " + upload_data);
    //
    //     let form_data_check = new FormData();
    //     form_data_check.append("StockPublishedCheckBox", upload_data);
    //
    //
    //         if (upload_data === null) {
    //             console.log("No checkbox information to upload");
    //         } else {
    //             console.log("checkbox to upload = " + JSON.stringify(form_data_check) + ". upload_data: " + upload_data);
    //         }
    //         let stockCheckApiUrl = `/rest/uploadStockPublished/` + uploadId
    //             await fetch(stockCheckApiUrl, {
    //                 method: 'POST',
    //                 headers: myHeaders,
    //                 body: JSON.stringify(form_data_check)
    //             }).then(function (response) {
    //                 if (response.status === 200) {
    //                     console.log("fetch /rest/uploadStockPublished/ = OK");
    //                 } else {
    //                     console.log("fetch /rest/uploadStockPublished/ = ERROR");
    //                 }
    //             //     .then(response => {
    //             //     return response.status;
    //             // }).then((data)=>{
    //             //     console.log("fetching rest/uploadStockImage. response = " + data);
    //             });
    //     }

        // $.ajax(
        //     {
        //         type: 'POST',
        //         url: `/rest/uploadStockPublished/` + uploadId,
        //         contentType: false,
        //         processData: false,
        //         cache: false,
        //         success: function () {
        //             console.log("Stock " + uploadId + " checkbox changed to " + upload_data);
        //         },
        //         error: function () {
        //             console.log("Stock checkbox change error");
        //         }
        //     });

        // await fetch(`/rest/uploadStockPublished/` + uploadId, {
        //     method: 'POST',
        //     body: upload_data,
        // }).then(response => {
        //     console.log("fetching rest/uploadStockPublished. response = " + response);
        //     return response.ok;
        // });
}
;

/**
 * function changes modal window header
 */
function handleAddNewStockButton() {
    $('#stockIdDiv').addClass('d-none')
    $('.modal-title').text("Добавить новую акцию")
    stockModalClearFields()
}

/**
 * function clears modal window fields
 */
function stockModalClearFields() {
    $("#stockId").val("")
    $("#stockTitle").val("")
    $('#stockText').summernote('code', "")
    $("#startDate").val("")
    $("#endDate").val("")
    // $("#published").val(false)
}

/**
 * Stock list render
 * @param data
 */
function renderStockList(data) {
    lastPage.number = data.number;
    lastPage.last = data.last;
    let stockDiv = $("#stocksDiv");
    if (data.number === 0) {
        $(stockDiv).empty();
    }
    let stocks = data.content;
    $.ajax(sharedStockApiUrl, {
        headers: myHeaders,
        async: false,
        success: render,
        error: function (error) {
            console.log(error);
        }
    });

    function render(sharedStocks) {
        let sharedStocksQuantity = sharedStocks.length
        for (let i = 0; i < stocks.length; i++) {
            let stockId = stocks[i].id
            let stockImgToRender = "../../uploads/images/stocks/" + stocks[i].stockImg
            // console.log("StockId = " + stockId + ". StockImg to render: " + stockImgToRender)
            let rating = Math.round(stocks[i].sharedStocks.length / sharedStocksQuantity * 1000);
            let publish = stocks[i].published;
            if (stockId === 1) {
                console.log("StockId = " + stockId + ". ");
                console.log("Publish: " + publish);
            }
            if (publish === true) {
                publish = "✔"
            } else {
                publish = "✘"
            }
            let endDate = stocks[i].endDate
            if (endDate === null) {
                endDate = "бессрочно"
            } else {
                endDate = moment(endDate).format("DD MMM YYYY")
            }
            let row = $(`<li id=li-${stocks[i].id}>`).attr("rating", rating);
            row.append(`<div class=\"card mb-3\">
                        <div class=\"row no-gutters\">
                            <div class=\"col-md-4\">
                                 <img class="card-img" src=${stockImgToRender} width=\"250\" alt="Ошибка. Перезагрузите фото акции">
                                 <p></p>
                                 <p id="renderedStockId" class="stockId">ID акции: ${stockId}</p>
                                    <p id="rating" class="rating">Рейтинг: ${rating}</p>
                                    <div>Срок проведения акции: <br>
                                    <p class=\"card-date\">
                                         с ${moment(stocks[i].startDate).format("DD MMM")}
                                         по ${endDate}
                                    </p> 
                                    </div>
                                    <p>Опубликовано <br> на гл.странице:    
                                     ${publish} </p>
                                    
                            </div>
                            <div class=\"col-md-6\">
                                <div class=\"card-body\">
                                    <h3 class='card-title'>${stocks[i].stockTitle}</h3>
                                    <p class=\"card-text\">${stocks[i].stockText}</p>
                                </div>
                            </div>
                            <div class="col-md-2 flex-row align-items-center">
                                <div class="nav flex-column nav-pills mt-2 container-fluid" role="tablist" 
                                            aria-orientation="vertical">
                                    <button data-toggle-id="edit-stock" class="btn btn-info" data-toggle='modal'
                                            data-target='#stockModal' id="editButton" 
                                            data-stock-id="${stocks[i].id}">Изменить</button>
                                </div>
                                <div  class="nav flex-column nav-pills mt-2 container-fluid" role="tablist" 
                                            aria-orientation="vertical">
                                    <button data-toggle-id="delete-stock" class="btn btn-danger" 
                                            id="deleteButton" 
                                            data-stock-id="${stocks[i].id}">Удалить</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </li> `)
            stockDiv.append(row)
        }
    }
}

function handleSummernote() {
    $('#stockText').summernote({
        lang: 'ru-RU',
        placeholder: 'Введите текст акции',
        height: 300,
        focus: true,
        toolbar: [
            ['style', ['style']],
            ['font', ['bold', 'underline', 'clear']],
            ['color', ['color']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['table', ['table']],
            ['insert', ['link', 'picture', 'video']],
            ['view', ['fullscreen', 'codeview', 'help']]
        ]
    });
}

/**
 * Function creates alert message when fields in modal are invalid
 * @param text - text of message
 * @param focusField - field to focus
 */
function invalidModalField(text, focusField) {
    document.querySelector('#modal-alert').innerHTML = `<div class="alert alert-danger alert-dismissible fade show" role="alert">
                                                                    <strong>${text}</strong>
                                                                     <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                                                         <span aria-hidden="true">&times;</span>
                                                                     </button>
                                                                </div>`
    focusField.focus()
    window.setTimeout(function () {
        $('.alert').alert('close');
    }, 3000)
}

/**
 * function that shows success or error message
 * @param inputField - location where message appears
 * @param text - text of message
 * @param messageStatus - status success or error
 */
function successActionMainPage(inputField, text, messageStatus) {
    let successMessage = `<div class="alert text-center alert-success alert-dismissible" role="alert">
                            <strong>${text}</strong>
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span></button>
                       </div>`
    let alertMessage = `<div class="alert text-center alert-danger alert-dismissible" role="alert">
                            <strong>${text}</strong>
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span></button>
                       </div>`
    let message = ''

    if (messageStatus === "success") {
        message = successMessage
    } else if (message === "error") {
        message = alertMessage;
    }

    document.querySelector(`${inputField}`).innerHTML = message
    window.setTimeout(function () {
        $('.alert').alert('close');
    }, 3000)
}