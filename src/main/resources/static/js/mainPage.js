fetch("/api/categories")
    .then(response => response.json())
    .then(function (data) {
        fillCategories(data);
    });

function fillCategories(data) {
    let siteMenu = document.getElementById('siteMenu');
    for (let key in data) {
        let item = `<li class="nav-item dropright">
                <a class="btn btn-outline-light dropdown-toggle text-secondary font-weight-normal dropdownbtn" href=${key} id="dropdownMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${key}</a>
                <div class="dropdown-menu" aria-labelledby="dropdownMenu">`;
        let subItem = ``;
        for (let value of data[key]) {
            subItem += `<a class="dropdown-item" href=${value}>${value}</a> `;
        }
        item += subItem;
        $(siteMenu).append(item);
    }
}

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