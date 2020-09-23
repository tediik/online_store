$(document).ready(function() {
    $(document).delegate("#buttonChangeMail", "click", changeMail);
});

function changeMail() {
    var formData = $('#formChangeMail').serialize();
    $.ajax({
        url: '/customer/changemail',
        type: 'POST',
        data: formData,
        success: function (res) {
            toastr.success('Подтвердите смену email. Вам на почту отправлено письмо.', {timeOut: 5000});
            document.location.href = "/customer";
        },
        error: function (res){
            toastr.error('Вы ввели тот же email', {timeOut: 5000});
            document.location.href = "/customer";
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