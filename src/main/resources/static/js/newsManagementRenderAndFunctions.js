/**
 * Declaration of global variables
 */
let myHeaders = new Headers()
let newsApiUrl = "/api/manager/news"
myHeaders.append('Content-type', 'application/json; charset=UTF-8')

$(document).ready(function () {
    fetchNews("/all")
});

/**
 * Event listeners of all document
 */
/*event listener in news div of edit and delete buttons*/
document.getElementById('newsTabContent').addEventListener('click', checkButton)
/*modal window publish checkbox listener*/
document.getElementById('publishCheckboxDiv').addEventListener('change', publishCheckboxHandler)
/*modal window footer */
document.querySelector('.modal-footer').addEventListener('click', checkButtonClicked)
/*left nav bar*/
document.getElementById('leftNavBar').addEventListener('click', handleLeftNavBarClick)


/**
 * checks which button was clicked in modal window update or add
 * @param event
 */
function checkButtonClicked(event) {
    console.log(event.target.dataset.toggleId)
    if (event.target.dataset.toggleId === 'update') {
        updateNews(event.target.dataset.newsId)
    }
}


/**
 * function check which button was clicked in news dev (edit or delete)
 * @param event - event from button
 */
function checkButton(event) {
    let newsId = event.target.dataset.newsId
    if (event.target.dataset.toggleId === 'edit') {
        renderEditModalWindow(newsId)
    }
    if (event.target.dataset.toggleId === 'delete') {
        handleDeleteButton(newsId)
    }
}

/**
 * function checks which button was pressed in left nav bar
 * @param event
 */
function handleLeftNavBarClick(event) {
    if (event.target.dataset.toggleId === 'addNewNews') {
        renderNewNewsModal()
    }
}

/**
 * function handles $('#addNewNews') ("Добавить новость") button click in left nav bar
 */
function renderNewNewsModal() {
    clearModalFields()
    $('#idModalDiv').hide()
    $('#modalWindowTitle').text('Добавить новость')
    $('#editSave').text('Добавить')
    $('#editSave').attr('data-toggle-id', "add")
}

/**
 * function clears modal window fields
 */
function clearModalFields() {
    document.getElementById('titleNewsUpdate').value = ''
    document.getElementById('anonsNewsUpdate').value = ''
    $('#fullTextUpdate').summernote('code', '')
    document.getElementById('postingDateUpdate').value = ''

}

/**
 * function check modal window fields to be filled
 * @returns {boolean}
 */
function checkFields() {
    if (document.getElementById('titleNewsUpdate').value === '') {
        invalidModalField('Введите заголовок новости!', document.getElementById('titleNewsUpdate'))
        return false
    } else if (document.getElementById('anonsNewsUpdate').value === '') {
        invalidModalField('Введите анонс новости!', document.getElementById('anonsNewsUpdate'))
        return false
    } else if (document.getElementById('fullTextUpdate').value === '') {
        invalidModalField('Введите новость!', document.getElementById('fullTextUpdate'))
        return false
    } else if (document.getElementById('postingDateUpdate').value === '') {
        invalidModalField('Введите дату публикации!', document.getElementById('postingDateUpdate'))
        return false
    } else {
        return true
    }
}

function updateNews() {
    let news = {
        title: document.getElementById('titleNewsUpdate').value,
        anons: document.getElementById('anonsNewsUpdate').value,
        fullText: document.getElementById('fullTextUpdate').value,
        postingDate: document.getElementById('postingDateUpdate').value
    }
    if (checkFields()){
        fetch(newsApiUrl, {
            method: 'PUT',
            headers: myHeaders,
            body: JSON.stringify(news)
        }).then(function (response){
            if (response.status === 200){
                console.log('news successfully updated')
            }
        })
        //TODO доделать
    }

}


/**
 * function handles delete button
 * @param newsId - Long id of news to delete
 */
function handleDeleteButton(newsId) {
    let doDelete = confirm(`Вы уверены что хотите удалить новость?`);
    if (doDelete) {
        fetch(newsApiUrl + `/${newsId}`, {
            method: 'DELETE',
            headers: myHeaders
        }).then(function (response) {
            if (response.status === 200) {
                $('#div-' + newsId).remove()
                infoMessage('#infoMessageMainPage', 'Новость успешно удалена', 'success')
            } else {
                console.log('news not found')
                infoMessage('#infoMessageMainPage', 'Новость не удалена', 'error')
            }
        })
    }
}

/**
 * Renders edit model windows
 * @param newsId - Long id of news to edit
 */
function renderEditModalWindow(newsId) {
    $('#editSave').text('Обновить')
    $('#editSave').attr('data-toggle-id', "update")
    $('#editSave').attr('data-news-id', newsId)
    fetch(newsApiUrl + `/${newsId}`, {
        headers: myHeaders
    }).then(function (response) {
        if (response.status === 200) {
            response.json().then(news => renderEditModal(news))
        } else {
            console.log('news not found')
            infoMessage('#infoMessageMainPage', 'Новость не найдена', 'error')
        }
    })

    function renderEditModal(news) {
        $('#idModalDiv').show()
        $('#modalWindowTitle').text('Редактировать новость')
        clearModalFields()
        $('#idNewsUpdate').val(news.id)
        $('#titleNewsUpdate').val(news.title)
        $('#anonsNewsUpdate').val(news.anons)
        $('#fullTextUpdate').summernote('code', news.fullText)
        $("#postingDateUpdate").val(moment(news.postingDate).format("yyyy-MM-DD"))
    }
}

/**
 * function that handles publish checkbox
 */
//TODO переделать логику
function publishCheckboxHandler() {
    if (document.getElementById('publishCheckbox').checked) {
        document.getElementById('publishCheckboxLabel').innerHTML = 'Снять новость с публикации?'
        document.getElementById('postingDateUpdate').setAttribute('disabled', 'true')
    } else {
        document.getElementById('publishCheckboxLabel').innerHTML = 'Опубликовать новость?'
        document.getElementById('postingDateUpdate').removeAttribute('disabled')
    }
}

/**
 * makes fetch request to manager rest controller
 * @param status news status can be:
 *  - /all
 *  - /published
 *  - /unpublished
 */
function fetchNews(status) {
    fetch(newsApiUrl + status, {
        headers: myHeaders
    }).then(function (response) {
        if (response.status !== 200) {
            //TODO обработчик
        } else {
            response.json().then(news => renderNewsTable(news))
        }
    })
}

/**
 * function renders news page
 * @param news List of news
 */
function renderNewsTable(news) {
    const allNewsUl = $('#allNews')
    allNewsUl.empty()
    news.forEach(function (element) {
        let postingDate = moment(element.postingDate).format("YYYY-MM-DD HH:mm")
        let modifiedDate = moment(element.modifiedDate).format("YYYY-MM-DD HH:mm")
        let row = `<div id="div-${element.id}" class="alert alert-info mt-2">
                        <h2 id="title">${element.title}</h2>
                        <h5 id="anons">${element.anons}</h5>
                        <p id="fullText">${element.fullText}</p>
                        <div class="container-fluid row" id="divContainer">
                            <div class="text-left align-text-bottom col" id="divSpan">
                                <span id="postingDate">Дата публикации: ${postingDate}</span>
                                <br>
                                <span id="postingDate">Дата последнего изменения: ${modifiedDate}</span>
                            </div>
                            <div class="text-right col" id="divButtons">
                                <button type="button" data-toggle="modal" class="btn btn-primary"  id="btn_edit_news" data-target="#editNewsModal" data-toggle-id="edit" data-news-id="${element.id}">Редактировать</button>
                                <button type="button" data-toggle="modal" class="btn btn-danger"  id="btn_delete_news" data-toggle-id="delete" data-news-id="${element.id}" >Удалить</button>
                            </div>
                        </div>
                    </div>`
        allNewsUl.append(row)
    })
}

/**
 * function that shows success or error message
 * @param inputField - location where message appears
 * @param text - text of message
 * @param messageStatus - status (String): 'success' or 'error'
 */
function infoMessage(inputField, text, messageStatus) {
    let successMessage = `<div class="alert text-center alert-success alert-dismissible info-message" role="alert">
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
    window.scroll(0, 0)

    document.querySelector(`${inputField}`).innerHTML = message
    window.setTimeout(function () {
        $('.info-message').alert('close');
    }, 3000)
}

/**
 * Function creates allert message when fields in modal are invalid
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
    //TODO не работает автозакрытие
    window.setTimeout(function () {
        $('.alert').alert('close')
    }, 3000)
}



