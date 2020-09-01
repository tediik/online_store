function changeMail() {
    var formData = $('form').serialize();
    $.ajax({
        url: '/customer/changemail',
        type: 'POST',
        data: formData,
        success: function (data) {
            if (data == "success") {
                toastr.success('На вашу почту отправлена ссылка для подтверждения смены почты!', {timeOut: 5000})
                close();
            } else if (data == "duplicatedEmailError") {
                $("#duplicatedEmailError").show();
            }
        }
    });
}

function close() {
    $('#openNewMailModal').hide();
    $(".modal-backdrop.show").hide();
}

function successChangePass() {
    toastr.success('Пароль успешно изменен!', {timeOut: 5000})

}