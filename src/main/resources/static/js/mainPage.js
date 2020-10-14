$(document).ready(function ($) {
    $("#openNewRegistrationModal").on('hidden.bs.modal', function (e) {
        $("#openNewRegistrationModal form")[0].reset();//reset modal fields
        $("#openNewRegistrationModal .error").hide();//reset error spans
    });
    preventDefaultEventForEnterKeyPress()
    getCurrent();
    fillCategories();
    fetchAndRenderSomeProducts();
    fetchAndRenderPublishedStocks();
});

/**
 * function that prevents submit event on Enter keypress in search input
 */
function preventDefaultEventForEnterKeyPress() {
    $(window).keydown(function (event) {
        if (event.keyCode === 13) {
            event.preventDefault();
            handleSearchButton()
            return false;
        }
    })
}

function fetchAndRenderSomeProducts() {
    fetch("/api/products").then(response => response.json()).then(data => fillSomeProducts(data));
    $('#headerForSomeProductsView').text('Актуальные предложения')
}

/**
 * event listener for search button
 */
document.getElementById('mainPageSearchButton').addEventListener('click', handleSearchButton)

/**
 * function that handles search button in headder of main page
 */
function handleSearchButton() {
    if ($('#mainPageSearchInput').val() !== '') {
        window.location.href = "/search/" + $('#mainPageSearchInput').val()
    }
}

function getCurrent() {
    fetch('/users/getCurrent')
        .then(response => {
            if (response.status == 200) {
                response.json()
                    .then(user => {
                        let role = user.roles.map(role => role.name);
                        $('#user-email').append(user.email);
                        if (role.includes("ROLE_ADMIN") || role.includes("ROLE_MANAGER")) {
                            $('#role-redirect').text("Профиль").click(function () {
                                $('#role-redirect').attr("href", "/authority/profile");
                            });
                        }
                        if (role.includes("ROLE_ADMIN")) {
                            $('#profile-main-link-manager, #profile-news, #profile-promotion').hide();
                        } else if (role.includes("ROLE_MANAGER")) {
                            $('#profile-main-link-admin').hide();
                        }
                    })
            }
        })
}

function register() {
    $(".alert").html("").hide();
    var formData = $('form').serialize();
    $.ajax({
        url: '/registration',
        type: 'POST',
        data: formData,
        success: function (data) {
            if (data === "success") {
                $("#emailConfirmationSent").show();
                toastr.info('Ссылка для подтверждения регистрации отправлена на вашу почту', {timeOut: 5000});
                close();
                document.location.href = "redirect:/";
            }
            if (data == "duplicatedEmailError") {
                $("#duplicatedEmailError").show();
            }
            if (data == "notValidEmailError") {
                $("#notValidEmailError").show();
            }
        },
        error: function (data) {
            if (data.status == 400) {
                if (data == "passwordError") {
                    $("#passwordError").show();
                    $("#passwordValidError").hide();
                }
                if (data == "passwordValidError") {
                    $("#passwordValidError").show();
                    $("#passwordError").hide();
                }
            }
        }
    });
}

function close() {
    $('#openNewRegistrationModal').hide();
    $(".modal-backdrop.show").hide();
}

async function fillCategories() {
    let data = await fetch("/api/categories").then(response => response.json());
    let siteMenu = document.getElementById('siteMenu');
    for (let key in data) {
        let item = `
            <li class="nav-item dropright">
                <a class="btn btn-outline-light dropdown-toggle text-secondary font-weight-normal dropdownbtn" 
                    href="#" id="dropdownMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${key}</a>
                <div class="dropdown-menu" aria-labelledby="dropdownMenu">`;
        let subItem = ``;
        for (let innerKey in data[key]) {
            subItem += `<a class="dropdown-item" href="/categories/${data[key][innerKey]}">
                            ${innerKey}</a> `;
        }
        item += subItem;
        $(siteMenu).append(item);
    }
}

/**
 * function that fills main page with products
 * @param data - products list
 */
function fillSomeProducts(data) {
    let prodsView = document.getElementById('someProductsView');
    prodsView.innerHTML = ''
    if (data !== 'error') {
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
            if ((key + 1) % 5 == 0) {
                $(prodsView).append(`<div class="row">` + item);
                item = ``;
            } else if ((key + 1) == data.length) {
                $(prodsView).append(`<div class="row">` + item);
            }
            $(function () {
                if (data[key].rating !== null) {
                    $(`#rate${data[key].id}`).rateYo({
                        rating: data[key].rating,
                        readOnly: true
                    });
                } else {
                    $(`#rate${data[key].id}`).rateYo({
                        rating: 0,
                        readOnly: true
                    });
                }
            });
        }
    } else {
        prodsView.innerHTML = 'Ожидайте новые продукты'
    }
}

/**
 * function that renders main page with publishedStocks
 * @param data - stocks list
 */
function fetchAndRenderPublishedStocks() {
    fetch("/api/publishedstocks").then(response => response.json()).then(data => fillPublishedStocks(data));
    $('#headerForPublishedStocksView').text('Горячие акции!')
}

/**
 * function that fills main page with products
 * @param data - products list
 */
function fillPublishedStocks(data) {
    let stocksView = document.getElementById('publishedStocksView');
    stocksView.innerHTML = ''
    if (data !== 'error') {
        let item = ``;
        for (let key = 0; key < data.length; key++) {
            item += `
            <div class="col-4">
                <div class="row no-gutters border rounded overflow-hidden flex-md-row mb-4 shadow-sm productView">
                    <div class="col-auto d-none d-lg-block productImg">
                        <img class="bd-placeholder-img" src="/manager/api/stock/${data[key].stockImg}">
<!--                        <img class="bd-placeholder-img" src="/uploads/images/default.jpg">-->
                    </div>
                    <div id="${data[key].id}"></div>
                    <div class="col p-4 d-flex flex-column position-static">
                        <p class="card-text mb-auto productName">${data[key].stockTitle}</p>
                        <a class="btn btn-sm btn-outline-light stockhref" href="/manager/api/stock/${data[key].id}" role="button">Подробнее &raquo;</a>
                    </div>
                </div>
            </div>`;
            if ((key + 1) % 5 == 0) {
                $(stocksView).append(`<div class="row">` + item);
                item = ``;
            } else if ((key + 1) == data.length) {
                $(stocksView).append(`<div class="row">` + item);
            }
            $(function () {
                if(data[key].rating !== null) {
                    $(`#rate${data[key].id}`).rateYo({
                        rating: data[key].rating,
                        readOnly: true
                    });
                } else {
                    $(`#rate${data[key].id}`).rateYo({
                        rating: 0,
                        readOnly: true
                    });
                }
            });
        }
    } else {
        stocksView.innerHTML = 'Ожидайте новые акции'
    }
}