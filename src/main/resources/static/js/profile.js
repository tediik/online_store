$(document).ready(function() {
    $(document).delegate("#buttonChangeMail", "click", changeMail);
    $(document).delegate("#new_password", "keyup", checkPassword);
    $("#submitNewPassword").attr('disabled', true);
});

function changeMail() {
    // this.stopPropagation();
    // this.preventDefault();
    var formData = $('#formChangeMail').serialize();
    $.ajax({
        url: '/customer/changemail',
        type: 'POST',
        data: formData,
        success: function (res) {
          ///  console.log('SUCCESS!!!');
          //  console.log(res);
         //   console.log("ötvet"+res.responseText);

            toastr.success(res, {timeOut: 5000});
            close();
            $('#email_input').val($('#new_mail').val());
            $('#new_mail').val('');
            // document.location.href = "/customer";

        },
        error: function (res){
         //   console.log('ERROR!!! - ' + res.body );
console.log(res);
            if (res.status == 400) {
                if (res.responseText == 'duplicatedEmailError') {
                    $(".messages-after-submit").text('Ошибка: Электронный адрес уже зарегистрирован');
                    toastr.success("Ошибка: Электронный адрес уже зарегистрирован", {timeOut: 5000});
                }
                if (res.responseText == 'notValidEmailError') {
                    $(".messages-after-submit").text('Ошибка: Не верно указана электронная почта');
                    toastr.success("Ошибка: Не верно указана электронная почта", {timeOut: 5000});
                }

             //   toastr.error(res.body, {timeOut: 5000});
            } else {
                console.log(res.status);
            }
            // document.location.href = "/customer";
        }
    });
    return false;
}

function close() {
    $('#openNewMailModal').hide();
    $(".modal-backdrop.show").hide();
}

function successChangePass() {
    toastr.success('Пароль успешно изменен!', {timeOut: 5000})

}

function checkPassword(){
    regularExpression = new RegExp("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9@#$%]).{8,}");
    newPassword = document.getElementById("new_password").value;
    if(regularExpression.test(newPassword)) {
        $("#submitNewPassword").attr('disabled', false);
    }
}