let listOfNews;
let key;
let content;
let allNewsList;

$(function () {
    showNewsDetails().then();
})

async function showNewsDetails() {
    listOfNews = await fetch("/api/news/{id}").then(response => response.json())
    allNewsList = $('#detailsNews')
    allNewsList.empty();
    for (key in listOfNews) {
        content =
            `
            <div class="alert alert-info mt-2">
                <div>${listOfNews[key].title}</div>
                <div>${listOfNews[key].fullText}</div>
                
            </div>
            `;
        allNewsList.append(content);
        console.log(listOfNews[key].title);
    }
}