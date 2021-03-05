let fullTextDetails;
let i;
let content2;
let fullNews;

$(function () {
    showNewsDetails(window.location.pathname.substring(6)
    );
})
/**
 * Функция для получения полного текста новости по id
 */
async function showNewsDetails(id) {
    console.log("showNewsDetails")
    fullTextDetails = await fetch("/api/news"+`/${id}`).then(response => response.json())
    fullNews = $('#newsInfo')
        fullNews.empty();
        content2 =
            `
            <div class="alert alert-info mt-2">
                <div>${fullTextDetails.title}</div>
                <div>${fullTextDetails.fullText}</div>
            </div>
            `;
        fullNews.append(content2);
}