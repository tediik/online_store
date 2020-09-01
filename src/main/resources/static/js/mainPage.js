function register() {
    $(".alert").html("").hide();
    var formData = $('form').serialize();
    $.ajax({
        url: '/registration',
        type: 'POST',
        data: formData,
        success: function (data) {
            if (data == "success") {
                toastr.success('Ссылка для подтверждения регистрации отправлена на вашу почту', {timeOut: 5000})
                close();
            } else if (data == "duplicatedEmailError") {
                $("#duplicatedEmailError").show();
            } else if (data == "passwordError") {
                $("#passwordError").show();
            } else if (data == "notValidEmailError") {
                $("#notValidEmailError").show();
            }
        }
    });
}

function close() {
    $('#openNewRegistrationModal').hide();
    $(".modal-backdrop.show").hide();
}

fetch("/api/categories")
    .then(response => response.json())
    .then(function (data) {
        fillCategories(data);
    });

fetch("/api/products")
    .then(response => response.json())
    .then(function (data) {
        fillSomeProducts(data);
    });

function fillCategories(data) {
    let siteMenu = document.getElementById('siteMenu');
    for (let key in data) {
        let item = `<li class="nav-item dropright">
                <a class="btn btn-outline-light dropdown-toggle text-secondary font-weight-normal dropdownbtn" 
                    href=${key} id="dropdownMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${key}</a>
                <div class="dropdown-menu" aria-labelledby="dropdownMenu">`;
        let subItem = ``;
        for (let value of data[key]) {
            let toLatin = util.Transliteration.сyrillicToLatin(${value});
            subItem += `<a class="dropdown-item" href=${toLatin}>${value}</a> `;
        }
        item += subItem;
        $(siteMenu).append(item);
    }
}

function fillSomeProducts(data) {
    let prodsView = document.getElementById('someProductsView');
    let item = ``;
    for (let key = 0; key < data.length; key++) {
        item += `
            <div class="col-2">
                <div class="row no-gutters border rounded overflow-hidden flex-md-row mb-4 shadow-sm productView">
                    <div class="col-auto d-none d-lg-block productImg">
                        <svg class="bd-placeholder-img" width="160" height="160" xmlns="http://www.w3.org/2000/svg" 
                            preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: Thumbnail">
                            <title>Placeholder</title><rect width="100%" height="100%" fill="#55595c"/>
                            <text x="25%" y="50%" fill="#eceeef">Product img</text></svg>
                    </div>
                    <div class="col p-4 d-flex flex-column position-static">
                        <p class="card-text mb-auto">${data[key].product}</p>
                        <a class="btn btn-sm btn-outline-light producthref" href="${data[key].id}" role="button">View details &raquo;</a>
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
