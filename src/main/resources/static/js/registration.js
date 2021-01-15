$(document).ready(function ($) {
    $("#openNewRegistrationModal").on('hidden.bs.modal', function (e) {
        $("#openNewRegistrationModal form")[0].reset();//reset modal fields
        $("#openNewRegistrationModal .error").hide();//reset error spans
    });

});
function register() {
    $(".alert").html("").hide();
    var formData = $('form').serialize();
    $.ajax({
        url: '/api/registration',
        type: 'POST',
        data: formData,
        success: function (data) {
            if (data === "success") {
                $("#emailConfirmationSent").show();
                toastr.info('Ссылка для подтверждения регистрации отправлена на вашу почту', {timeOut: 5000});
                close();
                document.location.href = "redirect:/";
            }
            if (data === "duplicatedEmailError") {
                $("#duplicatedEmailError").show();
            }
            if (data === "notValidEmailError") {
                $("#notValidEmailError").show();
            }
            if (data === "passwordError") {
                $("#passwordError").show();
                $("#passwordValidError").hide();
            }
            if (data === "passwordValidError") {
                $("#passwordValidError").show();
                $("#passwordError").hide();
            }
        }
    });
}

function close() {
    $('#openNewRegistrationModal').hide();
    $(".modal-backdrop.show").hide();
}