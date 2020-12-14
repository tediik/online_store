let productSearchByNameUrl = '/api/products/searchByName/'
let productSearchByDescription = '/api/products/searchByDescription/'
$(document).ready(function ($) {
    searchProduct(window.location.pathname.split('/')[2])
    preventDefaultEventForEnterKeyPress()
});

document.getElementById('pageSearchButton').addEventListener('click', handleSearchButton)

/**
 * function that prevents submit event on Enter keypress in search input
 */
let focusButton = document.querySelector('#pageSearchButton');
function preventDefaultEventForEnterKeyPress() {
    $(window).keydown(function (event) {
        if (event.keyCode === 13) {
            focusButton.focus();
            handleSearchButton()
        }
    })
}
/**
 * function handles search button click
 */
function handleSearchButton(){
    searchProduct($('#pageSearchInput').val())
}

/**
 * Function searches products by string typed in #mainPageSearchInput
 * @param searchString - string to search
 */
function searchProduct(searchString) {
    fetch(productSearchByNameUrl + searchString)
        .then(function (response) {
            if (response.ok) {
                response.json().then(searchResult => fillSearchNameDiv(searchResult, 'searchInNameDiv'))
            } else {
                console.log('error no: ' + response.status)
            }
        })
        .catch(error => console.log(error))

    fetch(productSearchByDescription + searchString)
        .then(function (response) {
            if (response.ok) {
                response.json().then(searchResult => fillSearchNameDiv(searchResult, 'searchInDescriptionDiv'))
            } else {
                console.log('error no: ' + response.status)
            }
        })
        .catch(error => console.log(error))
}

/**
 * Function fill divs with results of search
 * @param data - result of search
 * @param elementIdToFill - id of div to fill. Example: 'searchInDescriptionDiv'
 */
function fillSearchNameDiv(data, elementIdToFill) {
    let searchDiv = document.getElementById(elementIdToFill);
    searchDiv.innerHTML = ''
    if (data.length !== 0) {
        let item = ``;
        for (let key = 0; key < data.length; key++) {
            item += `
            <div class="col-2">
                <div class="row no-gutters border rounded overflow-hidden flex-md-row mb-4 shadow-sm productView">
                    <div class="col-auto d-none d-lg-block productImg">
                        <img class="bd-placeholder-img" src="/uploads/images/products/0.jpg">
                    </div>
                    <div id="rate${data[key].id}"></div>
                    <div class="col p-4 d-flex flex-column position-static">
                        <p class="card-text mb-auto productName">${data[key].product}</p>
                        <a class="btn btn-sm btn-outline-light producthref" href="/products/${data[key].id}" role="button">Подробнее &raquo;</a>
                    </div>
                </div>
            </div>`;
            if ((key + 1) % 5 === 0) {
                $(searchDiv).append(`<div class="row">` + item);
                item = ``;
            } else if ((key + 1) === data.length) {
                $(searchDiv).append(`<div class="row">` + item);
            }
            $(function () {
                $(`#rate${data[key].id}`).rateYo({
                    rating: data[key].rating,
                    readOnly: true
                });
            });
        }
    } else {
        searchDiv.innerHTML = 'Продуктов не найденно'
    }
}
