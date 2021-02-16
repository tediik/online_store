let listOfNews;
let key;
let content;
let allNewsList;
let button = document.getElementById("showDetails");
document.getElementById("fullTextButton").addEventListener("click", showNewsDetails);

// function showNewsDetails(id) {
//     fetch(`/api/news/${id}`)
//         .then(function (response) {
//             if (response.status === 200) {
//                 response.json().then(repairOrder => {
//                     $('#idRepairOrderUpdate').val(news.title);
//                     $('#orderNumberUpdate').val(news.fullText);
//                 })
//             }
//         })

// }

function showNewsDetails() {
    listOfNews = fetch("/api/news/${id}").then(response => response.json())
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
    document.getElementById("fullTextButton");
}