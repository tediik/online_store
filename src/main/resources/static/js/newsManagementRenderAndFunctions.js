/**
 * Declaration of global variables
 */
let myHeaders = new Headers()
let newsApiUrl = "/api/manager/news"
myHeaders.append('Content-type', 'application/json; charset=UTF-8')

$(document).ready(function () {
    // handleSummernote()
    fetchNews("/all")
});

/**
 * Event listeners of all document
 */
document.getElementById('newsTabContent').addEventListener('click', checkButton)



/**
 * function check which button was clicked
 * @param event - event from button
 */
function checkButton(event) {
    if (event.target.dataset.toggleId === 'edit') {
        handleEditButton(event)
    }
    if (event.target.dataset.toggleId === 'delete') {
        handleDeleteButton(event)
    }
}

function handleEditButton(event) {
    let newsId = event.target.dataset.newsId
    fetch(newsApiUrl + `/${newsId}`, {
        method: 'GET',
        headers: myHeaders
    }).then(function (response){
        if (response.status === 200){
            response.json().then(news => renderEditModal(news))
        } else {
            console.log('news not found')
            //TODO добавить обработчик
        }
    })

    function renderEditModal(news) {

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
                                <span id="postingDate">Дата последнего изменения: ${modifiedDate}</span>
                            </div>
                            <div class="text-right col" id="divButtons">
                                <button type="button" data-toggle="modal" class="btn btn-primary"  id="btn_edit_news" data-target="#editNewsModal" data-toggle-id="edit" data-news-id="${element.id}">Редактировать</button>
                                <button type="button" data-toggle="modal" class="btn btn-danger"  id="btn_delete_news" data-toggle-id="delete" >Удалить</button>
                            </div>
                        </div>
                    </div>`
        allNewsUl.append(row)
    })
}