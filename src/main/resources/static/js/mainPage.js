$(document).ready(function ($) {
    $("#openNewRegistrationModal").on('hidden.bs.modal', function (e) {
        $("#openNewRegistrationModal form")[0].reset();//reset modal fields
        $("#openNewRegistrationModal .error").hide();//reset error spans
    });

    getCurrent();
    fillCategories();
    fillSomeProducts();
});

function getCurrent() {
    $.ajax({
        url: '/users/getCurrent',
        type: 'GET',
        dataType: 'json',
        success: function (user) {
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
                // toastr.success('Ссылка для подтверждения регистрации отправлена на вашу почту', {timeOut: 5000});
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
                if(data.status == 400){
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

async function fillSomeProducts() {
    let data = await fetch("/api/products").then(response => response.json());
    let prodsView = document.getElementById('someProductsView');
    let item = ``;
    for (let key = 0; key < data.length; key++) {
        item += `
            <div class="col-2">
                <div class="row no-gutters border rounded overflow-hidden flex-md-row mb-4 shadow-sm productView">
                    <div class="col-auto d-none d-lg-block productImg">
                        <img class="bd-placeholder-img" src="/uploads/images/products/0.jpg">
                    </div>
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
    }
}