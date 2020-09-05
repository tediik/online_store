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
    let allNewsUl = document.getElementById('allNewsUl')
    allNewsUl.innerHTML = ''
    news.forEach(function (element){
        let row = $(`<li id=li-${element.id}>`)
        row.append()

    })
    `<div id="div_1" class="alert alert-info mt-2">
        <h2 id="title_1">${news.title}</h2>
        <h5 id="anons_1">${news.anons}</h5>
        <p id="fullText_1">${news.fullText}</p>
        <div class="container-fluid row" id="divContainer_1">
            <div class="text-left align-text-bottom col" id="divSpan_1">
                <span id="postingDate_1">Дата публикации: 2020-09-05 13:51</span>
            </div>
            <div class="text-right col" id="divButton_1">
                <button type="button" data-toggle="modal" class="btn btn-primary" value="1" id="btn_edit_news" name="newsEditCallModal">Редактировать</button>
                <a type="button" data-toggle="modal" class="btn btn-danger" value="1" id="btn_delete_news" name="newsDeleteCallModal">Удалить</a>
            </div>
        </div>
    </div>`
}