$(document).ready(function () {

    function readURL(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $('#prodPict').attr('src', e.target.result);
            }
            reader.readAsDataURL(input.files[0]); // convert to base64 string
        }
    }

    $("#productPictureFile").change(function(event) {
        const product = {
            id: $('#idInputModal').val(),
            product: $('#productInputModal').val(),
            price: $('#productPriceInputModal').val(),
            amount: $('#productAmountInputModal').val()
        };
        readURL(this);
        var file_data = $('#productPictureFile').prop('files')[0];
        var form_data = new FormData();
        form_data.append("pictureFile", file_data);
        $.ajax(
            {
                type: 'PUT',
                url: '/api/product/upload/picture/' + product.id,
                dataType: 'script',
                data: form_data,
                cache: false,
                contentType: false,
                processData: false,
                success: function (data) {
                    $('#profilePic').attr('src', data);
                },
                error: function (jqXhr, textStatus, errorThrown) {
                    console.log(errorThrown);
                }
        });
    });
});
