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
            alert ("Подтвердите смену email. Вам на почту отправлено письмо.");
            document.location.href = "/customer";
            //$('#mytabs a[href="#tab3"]').tab('show')
        },
        error: function (){
            alert("Вы ввели тот же email");
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