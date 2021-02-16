let listOfNews;
let key;
let content;
let allNewsList;
let newsText;

$(function () {
    showNews().then();
})

async function showNews() {
    listOfNews = await fetch("/api/news/all").then(response => response.json())
    allNewsList = $('#allNewsList')
    allNewsList.empty();
    for (key in listOfNews) {
        content =
            `
            <div class="alert alert-info mt-2">
                <div>${listOfNews[key].title}</div>
                <div>${listOfNews[key].anons}</div>
                <div class="mt-2"><a onclick="showNewsDetails()" class="btn btn-warning" >Детальнее</a>
                </div>
            </div>
            `;
        allNewsList.append(content);
        console.log(listOfNews[key].title);
    }
}


