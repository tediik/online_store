$(document).ready(function () {
    $(function () {
        $('#stockImgBtn').on('click', function () {
            console.log("stockImgBtn pressed");
            var $uploadId = $('#stockId').val();
            console.log('uploadId = ', uploadId);
            var file_data = $('#fileImgInput').prop('file');
            console.log(file_data);
            var form_data = new FormData();
            form_data.append("imageFile", file_data);
            console.log(form_data);
            console.log(form_data.get("imageStockFile"));
            $.ajax(
                {
                    type: 'POST',
                    url: '/rest/uploadStockImage/' + $uploadId,
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
        $('#deleteStockImgBtn').on('click', function () {
            console.log("deleteStockImgBtn pressed");
            var $deleteId = $('#stockId').val();
            console.log('Stock to delete Id = ', deleteId);
            $.ajax(
                {
                    type: 'DELETE',
                    url: '/rest/deleteStockImage/' + $deleteId,
                    contentType: false,
                    processData: false,
                    cache: false,
                    success: function (data) {
                        $('#stockPicture').attr('src', data);
                        $('#blahs').attr('src' , data);
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
                $('#blahs').attr('src', e.target.result);
            }
            reader.readAsDataURL(input.files[0]); // convert to base64 string
        }
    }
    $("#fileStockImgInput").change(function() {
        readURL(this);
    });
});
