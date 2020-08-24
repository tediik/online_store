let buttonEditNews
let buttonDeleteNews
let allNewsList
let allNewsDivId = $("#allNews")
let publishedNewsDivId = $("#publishedNews")
let unpublishedNewsDivId = $("#unpublishedNews")

$(document).ready(function () {

    $.ajax("/api/manager/news", {
        dataType: 'json',
        success: function (message) {
            allNewsList = message
            message.forEach(function (element) {
                currentTime = moment().format("YYYY-MM-DD HH:mm")
                postingDate = moment(element.postingDate).format("YYYY-MM-DD HH:mm")
                if (currentTime >= postingDate) {
                    fillingNews(element, publishedNewsDivId)
                } else {
                    fillingNews(element, unpublishedNewsDivId)
                }
                fillingNews(element, allNewsDivId)
            })
            clickButtonEditDelete()
        }
    })
})


function fillingNews(element, parentDiv) {
    let elementDiv = $("<div></div>")
    elementDiv.attr("id", "div_" + element.id)
    elementDiv.attr("class", "alert alert-info mt-2")

    let h2 = $("<h2/>")
    h2.attr("id", "title_" + element.id)
    h2.text(element.title)
    h2.appendTo(elementDiv)

    let pAnons = $("<h5/>")
    pAnons.attr("id", "anons_" + element.id)
    pAnons.text(element.anons)
    pAnons.appendTo(elementDiv)

    let pFullText = $("<p/>")
    pFullText.attr("id", "fullText_" + element.id)
    pFullText.html(element.fullText)
    pFullText.appendTo(elementDiv)

    let divContainer = $("<div class='container-fluid row'></div>")
    divContainer.attr("id", "divContainer_" + element.id)

    let divSpan = $("<div class='text-left align-text-bottom col'></div>")
    divSpan.attr("id", "divSpan_" + element.id)

    spanPostingDate = $("<span/>")
    spanPostingDate.attr("id", "postingDate_" + element.id)
    spanPostingDate.text("Дата публикации: " + moment(element.postingDate).format("YYYY-MM-DD HH:mm"))
    spanPostingDate.appendTo(divSpan)

    divSpan.appendTo(divContainer)

    let divButton = $("<div class='text-right col'></div>")
    divButton.attr("id", "divButton_" + element.id)

    let editButton = $("<button type='button' data-toggle='modal' class='btn btn-primary'/>")
    editButton.attr("value", element.id)
    editButton.attr("id", "btn_edit_news")
    editButton.attr("name", "newsEditCallModal")
    editButton.text("Редактировать")
    editButton.appendTo(divButton)

    let deleteButton = $("<a type='button' data-toggle='modal' class='btn btn-danger'/>")
    deleteButton.attr("value", element.id);
    deleteButton.attr("id", "btn_delete_news")
    deleteButton.attr("name", "newsDeleteCallModal")
    deleteButton.text("Удалить")
    deleteButton.appendTo(divButton)

    divButton.appendTo(divContainer)

    divContainer.appendTo(elementDiv)

    elementDiv.appendTo(parentDiv)
}


function clickButtonEditDelete() {
    buttonEditNews = $("[name = newsEditCallModal]")
    buttonEditNews.click(
        function () {
            news = getNews(parseInt($(this).attr("value")))
            $("#editModalNews").attr("value", news.id)
            $("#idNewsUpdate").attr("value", news.id)
            $("#titleNewsUpdate").attr("value", news.title)
            $("#anonsNewsUpdate").attr("value", news.anons)
            $("#fullTextUpdate").attr("value", news.fullText)
            $("#postingDateUpdate").attr("value", news.postingDate)
            $("#editNewsModal").modal('show')
        }
    )
    buttonDeleteNews = $("[name = newsDeleteCallModal]")
    buttonDeleteNews.click(
        function () {
            news = getNews(parseInt($(this).attr("value")))
            $("#deleteModalNews").attr("value", news.id)
            $("#idNewsDelete").attr("value", news.id)
            $("#titleNewsDelete").attr("value", news.title)
            $("#anonsNewsDelete").attr("value", news.anons)
            $("#fullTextDelete").attr("value", news.fullText)
            $("#postingDateDelete").attr("value", news.postingDate)
            $("#deleteNewsModal").modal("show")
        }
    )
}

let getNews = function (id) {
    let news
    $.each(allNewsList, function (index, object) {
        if (object.id === id) {
            news = object;
        }
    })
    return news
}

function addNews() {
    let news = {
        title: $("#addNewsTitle").val(),
        anons: $("#addNewsAnons").val(),
        fullText: $("#addNewsFullText").val(),
        postingDate: $('#addNewsPostingDate').val()
    }

    $.ajax("/api/manager/news/post", {
        method: "post",
        contentType: "application/json",
        data: JSON.stringify(news),
        dataType: "json",
        success: function (msg) {
            let currentTime = moment().format("YYYY-MM-DD, HH:mm")
            let postingDate = moment(msg.postingDate).format("YYYY-MM-DD, HH:mm")
            if (currentTime < postingDate) {
                fillingNews(msg, unpublishedNewsDivId)
            } else {
                fillingNews(msg, publishedNewsDivId)
            }
            fillingNews(msg, allNewsDivId)
            allNewsList.push(msg)
            clickButtonEditDelete()
            $("#addNewsTitle").val("")
            $("#addNewsAnons").val("")
            $("#addNewsFullText").val("")
            $('#addNewsPostingDate').val("")
        }
    })
}

function updateNews() {
    let news = {
        id: $("#idNewsUpdate").val(),
        title: $("#titleNewsUpdate").val(),
        anons: $("#anonsNewsUpdate").val(),
        fullText: $("#fullTextUpdate").val(),
        postingDate: $("#postingDateUpdate").val()
    }

    $.ajax("/api/manager/news/update", {
        method: "put",
        contentType: "application/json",
        data: JSON.stringify(news),
        dataType: "json",
        success: function (msg) {
            $("#div_" + msg.id).remove()
            $("#div_" + msg.id).remove()

            currentTime = moment().format("YYYY-MM-DD HH:mm")
            postingDate = moment(msg.postingDate).format("YYYY-MM-DD HH:mm")

            if (currentTime >= postingDate) {
                fillingNews(msg, publishedNewsDivId)
            } else {
                fillingNews(msg, unpublishedNewsDivId)
            }
            fillingNews(msg, allNewsDivId)

            $.each(allNewsList, function (index, object) {
                if (object.id === msg.id) {
                    object = msg;
                }
            })

            clickButtonEditDelete()
        }
    })
    $("#editNewsModal").modal('hide');
}

function deleteNews() {
    $.ajax("/api/manager/news/" + $("#idNewsDelete").val() + "/delete/" , {
        method: "delete",
        dataType: "text",
        success: function (msg) {
            $("#div_" + msg).remove()
            $("#div_" + msg).remove()

            $.each(allNewsList, function (index, object) {
                if (object.id === msg) {
                    object.splice(index, 1);
                }
            })
        }
    });
    $("#deleteNewsModal").modal('hide')
}