/**
 * Declaration of global variables
 */
let myStockHeaders = new Headers()
let sharedStockApiUrl = "/api/manager/sharedStock"
let stockApiUrl = "/api/manager/stock"
let stockImgUrl = "../../uploads/images/stocks/"
myStockHeaders.append('Content-type', 'application/json; charset=UTF-8')
const lastStockPage = {type: 'ALL', currentDate: new Date().toLocaleDateString(), number: 0, last: false};

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
    if (lastStockPage.last) {
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
        let stockTitle = document.getElementById('stockTitle')
        let stockText = document.getElementById('stockText')
        let startDate = document.getElementById('startDate')
        let filename = $('#fileImgInputHidden').val();
        try {
            let tempfilename = $('#fileImgInput')[0].files[0].name;
            if (tempfilename.indexOf('fakepath') === -1) {
                filename = tempfilename;
            } else {
                filename = $(tempfilename).val().replace(/C:\\fakepath\\/i, '')
                invalidModalField("Ошибка загрузки. Повторите выбор файла", stockImgUrl)
            }
        } catch (err) {
            console.log("name of file not found");
        }

        if (stockTitle.value === '') {
            invalidModalField("Заполните заголовок акции", stockTitle)
        } else if (stockText.value === '') {
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
    lastStockPage.type = event.target.dataset.toggleId;
    lastStockPage.currentDate = new Date().toLocaleDateString();
    lastStockPage.number = 0;
    lastStockPage.last = false;
    fetchStockList();
}

/**
 * Fetch request to stock list
 */
function fetchStockList() {
    $.ajax(stockApiUrl + '/page', {
        headers: myStockHeaders,
        async: false,
        data: {page: lastStockPage.number, type: lastStockPage.type, currentDate: lastStockPage.currentDate},
        success: function (data) {
            let stockData = data.data;
            renderStockList(stockData)
        },
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
            headers: myStockHeaders
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

/**
 * по нажатию на Save changes сохраняет картинку в uploads\images\stocks
 * @returns {Promise<void>}
 */
async function submit() {
    var input = document.querySelector('#fileImgInput')

    var data = new FormData()
    data.append('file', input.files[0])

    fetch('/api/stock/uploadFile', {
        method: 'POST',
        body: data,
    })
}

/**
 * modal window "save changes" button handler
 */
function handleSaveChangesButton(event, file_name_stockImg) {
    let startDate = $('#startDate').val();
    let stockPublished;
    if (document.getElementById('published').checked) {
        stockPublished = "true";
    } else {
        stockPublished = "false";
    }
    startDate = moment(startDate).format("YYYY-MM-DD")
    let endDate = ""
    if ($('#endDate').val() !== null || $('#endDate').val() !== "") {
        endDate = $('#endDate').val()
        endDate = moment(endDate).format("YYYY-MM-DD")
    }
    if (endDate === "Invalid date") {
        endDate = ""
    }

    const stock = {
        id: $('#stockId').val(),
        stockImg: file_name_stockImg,
        stockTitle: $('#stockTitle').val(),
        stockText: $('#stockText').summernote('code'),
        startDate: startDate,
        endDate: endDate,
        published: stockPublished
    }
    let method = (stock.id !== '' ? 'PUT' : 'POST')
    fetchStock(stock, method)

    function fetchStock(stock, method) {
        fetch(stockApiUrl, {
            method: method,
            headers: myStockHeaders,
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

/**
 * Edit button handler
 * @param event
 */
function handleEditButtonClick(event) {
    $('#stockIdDiv').removeClass('d-none')
    $('.modal-title').text("Редактировать акцию")
    let stockId = event.target.dataset.stockId
    let filename = ""
    stockModalClearFields()
    $.ajax('/api/stock/' + stockId, {
        type: 'GET',
        async: false,
        success: function (data) {
                    filename = data.stockImg;
                }
    })
    function renderModalWindowEdit(stock) {
        let stockText = stock.stockText
        $("#stockId").val(stock.id)
        $("#stockTitle").val(stock.stockTitle)
        $('#stockText').summernote('code', stockText)
        $("#startDate").val(stock.startDate)
        $("#endDate").val(stock.endDate)
        $("#published").prop('checked', stock.published)
        $("#fileImgInputHidden").val(filename)
    }

    fetch(stockApiUrl + `/${stockId}`, {
        method: 'GET',
        headers: myStockHeaders
    }).then(response => response.json())
        .then(stock => stock.data)
        .then(stock => renderModalWindowEdit(stock))
}

/**
 * Обработка чекбокса #published
 * если галка стоит, то установить published = true
 * и наоборот
 */
function chekboxPublished(o) {
    if (o.checked === true) {
        $("#published").val(true);
    } else {
        $("#published").val(false);
    }
}

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
}

/**
 * Stock list render
 * @param data
 */
function renderStockList(data) {
    lastStockPage.number = data.number;
    lastStockPage.last = data.last;
    let stockDiv = $("#stocksDiv");
    if (data.number === 0) {
        $(stockDiv).empty();
    }
    let stocks = data.content;
    $.ajax(sharedStockApiUrl, {
        headers: myStockHeaders,
        async: false,
        success: function(data) {
            let stockData = data.data;
            render(stockData)},
        error: function (error) {
            console.log(error);
        }
    });

    function render(sharedStocks) {
        let sharedStocksQuantity = sharedStocks.length
        for (let i = 0; i < stocks.length; i++) {
            let stockId = stocks[i].id
            let stockImgToRender = "../../uploads/images/stocks/" + stocks[i].stockImg
            let rating = Math.round(stocks[i].sharedStocks.length / sharedStocksQuantity * 1000);
            let publish = stocks[i].published;
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
                            <div class="col-md-2 flex-row align-items-right">
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
    document.querySelector('#stock-modal-alert').innerHTML = `<div class="alert alert-danger alert-dismissible fade show" role="alert">
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