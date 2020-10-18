$(document).ready(function ($) {
    $("#openNewRegistrationModal").on('hidden.bs.modal', function (e) {
        $("#openNewRegistrationModal form")[0].reset();//reset modal fields
        $("#openNewRegistrationModal .error").hide();//reset error spans
    });
    preventDefaultEventForEnterKeyPress()
    getCurrent();
    fillCategories();
    fetchAndRenderPublishedStocks();
    fetchAndRenderPublishedNews();
    fetchAndRenderSomeProducts();
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
    fetch("/api/publishedstocks")
        .then(response => response.json())
        .then(data => fillPublishedStocks(data))
}

/**
 * function that fills main page with publishedStocks
 * @param data - products list
 */
function fillPublishedStocks(data) {
    if (data !== 'error') {
        let item = ``;
        let stockCarouselIndicator = ``;
        let stockCarouselItem = ``;

        for (let key = 0; key < data.length; key++) {
            let li = key + 1;
            console.log("key = " + key + ". li = " + li);
            stockCarouselIndicator = `<li data-target="#myCarousel" data-slide-to="${li}"></li>`;
            $(".stockCarousel-indicators").append(stockCarouselIndicator);
            console.log("stockCarouselIndicator = " + stockCarouselIndicator);
            console.log("stockCarouselImage = " + data[key].stockImg);
            stockCarouselItem = `
                 <div class="carousel-item carousel-itemWithStock">
                        <img class="next-slide"
                             src="/uploads/images/stocks/${data[key].stockImg}" width="400" height="200"
                             onerror="if (this.src != '/uploads/images/stocks/default.jpg')
                                 this.src = '/uploads/images/stocks/default.jpg';">
                        <div class="container">
                            <div class="carousel-caption" style="color: red">
                                <p><a class="btn btn-secondary" style="margin-right: -252px" 
                                href="global/stockDetails/${data[key].id}" role="button">Подробнее &raquo;</a></p>
                            </div>
                             <div class = "card-text stockTitle">
                                <h5>${data[key].stockTitle}</h5>
                             </div>
                        </div>
            </div>`;
            $(".carousel-inner").append(stockCarouselItem);
        }
    } else {
        console.log('Нет опубликованных акций');
    }
}


/**
 * function that renders main page with publishedNews
 * @param data - stocks list
 */
function fetchAndRenderPublishedNews() {
    fetch("/api/publishednews")
        .then(response => response.json())
        .then(data => fillPublishedNews(data))
}

/**
 * function that fills main page with publishedNews
 * @param data - products list
 */
function fillPublishedNews(data) {
    if (data !== 'error') {
        let carouselIndicator = ``;
        let carouselItem = ``;

        for (let key = 0; key < data.length; key++) {
            let li = key + 1;
            console.log("key = " + key + ". li = " + li);
            carouselIndicator = `<li data-target="#carouselExampleIndicators" data-slide-to="${li}"></li>`;
            $(".carousel-indicators").append(carouselIndicator);
            console.log("carouselIndicator = " + carouselIndicator);

            carouselItem = `
                 <div class="carousel-item carousel-itemWithNews">
                        <img class="next-slide"
                             src="/uploads/images/news/${data[key].newsImg}" width="400" height="200"
                             onerror="if (this.src != '/uploads/images/news/defnews.jpg')
                                 this.src = '/uploads/images/news/defnews.jpg';">
                        <div class="container">
                            <div class="carousel-caption" style="color: red">
                                <p><a class="btn btn-secondary" style="margin-bottom: -60px; margin-right: -252px" 
                                href="news/${data[key].id}" role="button">Подробнее &raquo;</a></p>
                            </div>
                             <div class = "card-text newsTitle">
                                <h5>${data[key].newsTitle}</h5>
                             </div>
                        </div>
            </div>`;
            $(".newsCarousel-inner").append(carouselItem);
        }
    } else {
        console.log("No news to view");
    }
}