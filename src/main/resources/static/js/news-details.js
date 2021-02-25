let fullTextDetails;
let i;
let content2;
let fullNews;

/**
 * Функция для получения полного текста новости по id
 */
function showNewsDetails(id) {
    fullTextDetails =  fetch("/api/news"+`/${id}`).then(response => response.json())
    fullNews = $('#newsInfo')
        fullNews.empty();
        for(i in fullTextDetails)
    {
        content2 =
            `
            <div class="alert alert-info mt-2">
                <div>${fullTextDetails[i].title}</div>
                <div>${fullTextDetails[i].fullText}</div>

            </div>
            `;
        fullNews.append(content2);
        document.getElementById("newsInfo").innerHTML = content2;
    }

}