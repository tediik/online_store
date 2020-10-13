$(document).ready(function () {
    let headers = new Headers()
    headers.append('Content-type', 'application/json; charset=UTF-8')
    $(function () {
        $('#stockImgBtn').on('click', function () {
            let uploadId = $('#stockId').val();
            let file_data = $('#stockImg')[0].files[0];
            let form_data = new FormData();
            form_data.append("fileImgInput", file_data);
            $.ajax(
                {
                    type: 'POST',
                    url: '/rest/uploadStockImage/' + uploadId,
                    dataType: 'script',
                    data: form_data,
                    cache: false,
                    headers: headers,
                    processData: false,
                    success: function (data) {
                        console.log("мы в success");
                        console.log("data = " + data);
                        // $('#stockPicture').attr('src', data);
                    },
                    error: function (data) {
                        console.log("мы в error");
                        console.log("data = " + data);
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
    $("#fileImgInput").change(function() {
        readURL(this);
    });
});
