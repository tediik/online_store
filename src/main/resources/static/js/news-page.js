let listOfNews;
let key;
let content;
let allNewsList;
let newsText;

$(function () {
    showNews().then();
})
/**
 * Функция для получения списка новостей
 */
async function showNews() {
    listOfNews = await fetch("/api/news/all").then(response => response.json())
    allNewsList = $('#allNewsList')
    allNewsList.empty();
    for(key in listOfNews) {
        content =
            `
            <div class="alert alert-info mt-2">
                <div>${listOfNews[key].title}</div>
                <div>${listOfNews[key].anons}</div>
                <div class="mt-2"><a  href="/news/${listOfNews[key].id}" type="button" class="btn btn-warning" data-target="#newsInfo">Детальнее</a></div>
                </div>
            </div>
            `;
        allNewsList.append(content);
    }
}