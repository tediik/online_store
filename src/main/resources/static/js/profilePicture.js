$(document).ready(function () {
    $(function () {
        $('#pictureBtn').on('click', function () {
            var file_data = $('#fileInput').prop('files')[0];
            var form_data = new FormData();
            form_data.append("imageFile", file_data);
            console.log(form_data);
            console.log(form_data.get("imageFile"));
            $.ajax(
                {
                    type: 'POST',
                    url: '/customer/uploadImage',
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

    $(function () {
        $('#deletePicBtn').on('click', function () {
            $.ajax(
                {
                    type: 'DELETE',
                    url: '/customer/deleteImage',
                    contentType: false,
                    processData: false,
                    cache: false,
                    success: function (data) {
                        $('#profilePic').attr('src', data);
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
    });
});
