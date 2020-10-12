$(document).ready(function () {
    $(function () {
        $('#deletePicBtn').on('click', function () {
            $.ajax(
                {
                    type: 'DELETE',
                    url: '/users/deleteImage',
                    contentType: false,
                    processData: false,
                    cache: false,
                    success: function (data) {
                        $('#profilePic').attr('src', data);
                        $('#blah').attr('src' , data);
                    },
                    error: function (jqXhr, textStatus, errorThrown) {
                        console.log(errorThrown);
                    }
                });
        })

    })
    function readURL(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $('#blah').attr('src', e.target.result);
            }
            reader.readAsDataURL(input.files[0]); // convert to base64 string
        }
    }
    $("#fileInput").change(function() {
        readURL(this);
        var file_data = $('#fileInput').prop('files')[0];
        var form_data = new FormData();
        form_data.append("imageFile", file_data);
        $.ajax(
            {
                type: 'POST',
                url: '/users/uploadImage',
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
