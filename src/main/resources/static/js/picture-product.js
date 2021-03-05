$(document).ready(function () {
    /**
     * Функция для динамического отображения выбранного файла
     * @param input
     */
    function readURL(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function (e) {
                $('#prodPict').attr('src', e.target.result);
            }
            reader.readAsDataURL(input.files[0]); // convert to base64 string
        }
    }
    $('#productPictureFile').change(function () {
        readURL(this);
    });

    /**
     * Функция для добавления картинки выбранному продукту
     */
    $('#acceptEditPictureButton').click(function () {
        let id = $('#idInputPictureModal').val();
        let file_data = $('#productPictureFile').prop('files')[0];
        let form_data = new FormData();
        form_data.append("pictureFile", file_data);
        $.ajax({
            type: 'PUT',
            url: '/api/product/upload/picture/' + id,
            dataType: 'text',
            data: form_data,
            cache: false,
            contentType: false,
            processData: false,
            success: function (data) {
                $('#showPictureFirstSlide').attr("src", data);
            }
        });
        $('#productPictureModalWindow').modal('hide')
    });

    /**
     * Функция для динамического отображения выбранного
     * файла в модалке добавления картинки для нового продукта
     * @param input
     */
    function readURLForNewProduct(input) {
        if (input.files && input.files[0]) {
            let reader = new FileReader();
            reader.onload = function (e) {
                $('#pictForNewProduct').attr('src', e.target.result);
            }
            reader.readAsDataURL(input.files[0]); // convert to base64 string
        }
    }

    function showPictureOnPicturePage(pictureFile){
        $('#showPictureFirstSlide')
    }
    /**
     * Функция добавления картинки для нового продукта
     */
    $('#addedPictureFile').change(function() {
        const product = {id: $('#idAddPictureModal').val()}
        readURLForNewProduct(this);
        var file_data = $('#addedPictureFile').prop('files')[0];
        var form_data = new FormData();
        form_data.append("pictureFile", file_data);
        $('#acceptAddPictureButton').click(function (){
            $.ajax({
                type: 'PUT',
                url: '/api/product/upload/picture/' + product.id,
                dataType: 'text',
                data: form_data,
                cache: false,
                contentType: false,
                processData: false,
                success: function (data) {
                    $('#showPictureFirstSlide').attr("src",data)
                },
                error: function (jqXhr, textStatus, errorThrown) {
                    console.log(errorThrown);
                }
            });
            $('#addPictureForNewProductModalWindow').modal('hide')
        });
    });
});
