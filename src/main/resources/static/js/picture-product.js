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

    /**
     * Функция для добавления картинки выбранному продукту
     */
    $('#productPictureFile').change(function () {
        let id = $('#idInputPictureModal').val();
        readURL(this);
        let file_data = $(this).prop('files')[0];
        let form_data = new FormData();
        form_data.append("pictureFile", file_data);
        $('#acceptEditPictureButton').one("click", function () {
            $.ajax({
                type: 'PUT',
                url: '/api/product/upload/picture/' + id,
                dataType: 'script',
                data: form_data,
                cache: false,
                contentType: false,
                processData: false,
                success: function (data) {
                    $('#showPictureFirstSlide').attr("src", data);
                }
            });
            form_data.delete("pictureFile")
            $('#productPictureModalWindow').modal('hide')
        });

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
                dataType: 'script',
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
    /**
     * Функция для удаления картинки выбранного продукта
     */
    $('#DeletePictureButton').click(function (){
            const product = {id: $('#idInputPictureModal').val()}
        $.ajax({
            type: 'DELETE',
            url: '/api/product/picture/delete/' + product.id
        })
        $('#productPictureModalWindow').modal("hide");
    })
});
