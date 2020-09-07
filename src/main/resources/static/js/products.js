$(document).ready(function () {
    create();
    $("#newProductImportModal").on('hidden.bs.modal', function (e) {
        $("#newProductImportModal form")[0].reset();//reset modal fields
    });
});

function toggle(check)
{ if(!check.checked)
{
    create(true)
}
else
{
    create(false)
}
}

/*Создаем таблицу товаров без метки "удален"*/
function create(showDeleted) {
    $("#productsDiv").empty();
    moment.locale('ru');
    $.ajax("/rest/products/allProducts", {
        dataType: "json",
        success: function (data) {
            const products = JSON.parse(JSON.stringify(data))
            if (showDeleted != true){
                for (let i = 0; i < products.length; i++){
                    if(products[i].deleted == true){
                        delete products[i]
                    }
                }
            }
            for (let i = 0; i < products.length; i++) {
                    let out = $("<li>").attr("id", products[i].id);
                    console.log("Статус удаления" + products[i].deleted);
                    out.append(
                        `<div class=\"card mb-3\">
                        <div class=\"row no-gutters\">
                            <div class=\"col-md-6\">
                                <div class=\"card-body\">
                                    <h4 class='card-title'>${products[i].product}</h4>
                                    <h4 class='card-title'>Цена товара</h4>
                                    <p class=\"card-text\">${products[i].price}</p>
                                    <h4 class='card-title'>Кол-во товара</h4>
                                    <p class=\"card-text\">${products[i].amount}</p>
                                    <h4 class='card-title'>Кол-во товара</h4>
                                    <p class=\\"card-text\\">${products[i].amount}</p>
                                    <h4 class='card-title'>Статус удаления</h4>
                                    <p class=\\"card-text\\">${products[i].deleted}</p>
                                </div>
                            </div>
                            <div class="col-md-2 flex-row align-items-center">
                                <div class="nav flex-column nav-pills mt-2 container-fluid" role="tablist" aria-orientation="vertical">
                                    <button onclick='getProductForEdit(${products[i].id})' class="btn btn-info" data-toggle='modal'
                                            data-target='#editProductModal'>Edit</button>
                                </div>
                                <div class="nav flex-column nav-pills mt-2 container-fluid" role="tablist" aria-orientation="vertical">
                                    <button onclick='deleteProduct(${products[i].id})' class="btn btn-danger">Delete</button>
                                </div>
                                <div class="nav flex-column nav-pills mt-2 container-fluid" role="tablist" aria-orientation="vertical">
                                    <button onclick='restoreProduct(${products[i].id})' class="btn btn-info">Restore</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </li> `);
                    $("#productsDiv").append(out)
                }
            }
    })
}

/*Добавление товара вручную*/
function addProduct() {

    const productAdd = {
        product: $('#addProductTitle').val(),
        price: $('#addProductPrice').val(),
        amount: $('#addProductAmount').val(),
    };

    $.ajax({
        url: "/rest/products/addProduct",
        data: JSON.stringify(productAdd),
        dataType: 'json',
        type: 'POST',
        contentType: 'application/JSON; charset=utf-8',
        success: function () {
            create(false);
            $('.modal-body').find('input,textarea').val('');
            toastr.success('Новый товар добавлен!', {timeOut: 5000})
        },
        error: function () {
            alert("Заполните все поля!")
        }
    })
}

function importProductsFromFile(){

    var fileData = new FormData();
    fileData.append('file', $('#file')[0].files[0]);

    $.ajax({
        url: '/rest/products/uploadProductsFile',
        data: fileData,
        processData: false,
        contentType: false,
        type: 'POST',
        success: function(data){
            create(false);
            toastr.info('Импорт товаров завершен!', {timeOut: 5000})
        },
        error: function () {
            alert("Некорретный путь к файлу!")
        }
    });
}

/*Редактировать товар*/
function getProductForEdit(id) {
    $.ajax({
        type: "GET",
        url: "/rest/products/" + id,
        dataType: 'json',
        success: function (products) {
            $(".modal-body #Eid").val(products.id)
            $(".modal-body #editProductTitle").val(products.product)
            $(".modal-body #editProductPrice").val(products.price)
            $(".modal-body #editProductAmount").val(products.amount)
        }
    });
}

/*Сохранение заполненных полей*/
function updateProduct() {
    const productEdit = {
        id: $('#Eid').val(),
        product: $('#editProductTitle').val(),
        price: $('#editProductPrice').val(),
        amount: $('#editProductAmount').val(),
    };
    $.ajax({
        url: "/rest/products/editProduct",
        data: JSON.stringify(productEdit),
        dataType: 'json',
        type: 'PUT',
        contentType: 'application/JSON; charset=utf-8',
        success: function () {
            create(false);
            toastr.info('Товар успешно отредактирован!', {timeOut: 5000})
        },
        error: function () {
            alert("Заполните все поля!")
        }
    })
}

/*Удалить пользователя из таблицы*/
function deleteProduct(id) {
    $.ajax({
            url: "/rest/products/" + id,
            type: "DELETE",
            contentType: "application/json",
            success: function (data) {
                $("#productsDiv #" + id).remove();
                console.log(data)
                toastr.error('Товар успешно удален!', {timeOut: 5000})
            },
            error: function (er) {
                console.log(er)
            }
        }
    );
}

function restoreProduct(id) {
    $.ajax({
            url: "/rest/products/restoredeleted/" + id,
            type: "POST",
            contentType: "application/json",
            success: function (data) {
                create(false);
                console.log(data)
                toastr.info('Товар успешно восстановлен!', {timeOut: 5000})
            },
            error: function (er) {
                console.log(er)
            }
        }
    );
}


$("#productAddModal").ready(function () {
    var click = document.getElementById("save");
    click.onclick = function () {
        $("#newProductModal").modal('hide');
        addProduct()
    }
})

$("#productEditModal").ready(function () {
    var click = document.getElementById("editSave");
    click.onclick = function () {
        $("#editProductModal").modal('hide');
        updateProduct()
    }
})

$("#productImportModal").ready(function () {
    var click = document.getElementById("inputFileSubmit");
    click.onclick = function () {
        $("#newProductImportModal").modal('hide');
        console.log("Скрипт запущен")
         importProductsFromFile()
    }
})
