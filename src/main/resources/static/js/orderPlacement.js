/**
 * Адреса магазинов
 */
let shops = {};

/**
 * Функция отчистки полей формы
 */
function clearFields() {
    $('#addressForm')[0].reset();
}

/**
 * Функция для заполнения полей адреса
 * с использование плагина https://github.com/fias-api/jquery
 */
$(function () {
    var $zip = $('[name="zip"]'),
        $region = $('[name="region"]'),
        $district = $('[name="district"]'),
        $city = $('[name="city"]'),
        $street = $('[name="street"]'),
        $building = $('[name="building"]');


    $.fias.setDefault({
        parentInput: '.js-form-address',
        verify: true,
        select: function (obj) {
            setLabel($(this), obj.type);
        },
        check: function (obj) {
            var $input = $(this);

            if (obj) {
                setLabel($input, obj.type);
            } else {
                showError($input, 'Введено неверно');
            }
        },
        checkBefore: function () {
            var $input = $(this);

            if (!$.trim($input.val())) {
                return false;
            }
        },
        change: function (obj) {
            if (obj && obj.parents) {
                $.fias.setValues(obj.parents, '.js-form-address');
            }

            if (obj && obj.zip) {
                $('[name="zip"]').val(obj.zip);
            }
        },
    });

    $region.fias('type', $.fias.type.region);
    $district.fias('type', $.fias.type.district);
    $city.fias('type', $.fias.type.city);
    $street.fias('type', $.fias.type.street);
    $building.fias('type', $.fias.type.building);

    $district.fias('withParents', true);
    $city.fias('withParents', true);
    $street.fias('withParents', true);
    $building.fias('withParents', true);


    function setLabel($input, text) {
        text = text.charAt(0).toUpperCase() + text.substr(1).toLowerCase();
        $input.parent().find('label').text(text);
    }

    function showError($input, message) {
        $('#alert-modal-div').empty().append(`
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
              <strong>Ошибка в поле ${$input.attr('name')}</strong>
              <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            `)
        $('#orderModalWindow').scrollTop(0);
        window.setTimeout(function () {
            $('.alert').alert('close');
        }, 5000)
    }
});

/**
 * Функция смены зачения чекбокса доставка/самовывоз
 */
function addressChange() {
    if (document.getElementById("shopAddress").hidden) {
        getShopAddress()
        $('#addressId').attr("hidden", true);
        $('#shopAddress').attr("hidden", false);
        $('#clear-button').attr("hidden", true);
    } else {
        $('#addressId').attr("hidden", false);
        $('#shopAddress').attr("hidden", true);
        $('#clear-button').attr("hidden", false);
    }
}

/**
 * Функция полчуает адреса магазинов
 */
function getShopAddress() {
    fetch("customer/rest/allShops", {
        method: 'GET',
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    }).then(response => response.json()).then(shops => showShops(shops))
}

/**
 * Заполняет radio button адресами магазинов
 * @param shops1 адреса магазинов из базы
 */
function showShops(shops1) {
    $('#shopAddress').empty().append(`
            <h3>Заберу заказ по адресу:</h3>
        `);
    shops = shops1;
    for (let i = 0; i < shops.length; i++) {
        var address = shops[i]
        $('#shopAddress').append(`
            <div>
                <input type="radio" id="shop${address.id}"
                    name="shops" value="${address.id}">
                <label for="shop${address.id}">
                    ${address.region},${address.city},${address.street},${address.building}
                </label>
            </div>
        `);
    }
}

/**
 * Функция подтверждения оформления заказа на указанный адрес
 * При ошибке получим сообщение об ошибке
 */
function confirmOrder() {
    if (document.getElementById("shopAddress").hidden) {
        if ($('[class="kladr-error"]').attr('name')) {
            $('#alert-modal-div').empty().append(`
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
              <strong>Заполните поля выделенные красным Корректно!</strong>
              <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            `)
            $('#orderModalWindow').scrollTop(0);
            window.setTimeout(function () {
                $('.alert').alert('close');
            }, 5000)
        } else if ($('[name="zip"]').val() !== "" &&
            $('[name="region"]').val() !== "" &&
            $('[name="city"]').val() !== "" &&
            $('[name="street"]').val() !== "" &&
            $('[name="building"]').val() !== "") {
            address = {
                region: $('[name="region"]').val(),
                city: $('[name="city"]').val(),
                district: $('[name="district"]').val(),
                street: $('[name="street"]').val(),
                building: $('[name="building"]').val(),
                zip: $('[name="zip"]').val(),
                flat: $('[name="flat"]').val(),
                shop: false
            };

            fetch("/customer/busketGoods", {
                method: 'POST',
                body: JSON.stringify(address),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then(function (response) {
                if (response.ok) {
                    $('#dismissButton').click();
                    showInformation(true, "Заказ успешно оформлен!")
                } else {
                    showInformation(false, "Не удалось оформить заказ по указанному адресу!")
                }
            })
        } else {
            $('#alert-modal-div').empty().append(`
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
              <strong>Заполните все поля отмеченные звёздочкой</strong>
              <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            `)
            $('#orderModalWindow').scrollTop(0);
            window.setTimeout(function () {
                $('.alert').alert('close');
            }, 5000)
        }
    } else {
        let id = $("input[name=shops]").filter(":checked").val();
        let address = shops[id - 1];
        let addressToAdd = {
            id: id,
            region: address.region,
            city: address.city,
            street: address.street,
            building: address.building,
            zip: address.zip,
            shop: address.shop
        };
        fetch("/customer/busketGoods", {
            method: 'POST',
            body: JSON.stringify(addressToAdd),
            headers: {
                'Content-type': 'application/json; charset=UTF-8'
            }
        }).then(function (response) {
            if (response.ok) {
                $('#dismissButton').click();
                showInformation(true, "Заказ успешно оформлен!")
            } else {
                showInformation(false, "Не удалось оформить заказ по указанному адресу!")
            }
        })
    }
}

/**
 * Функция информаицонного сообщения, сообщает о успешности оформления заказа или неудаче
 * @param success успех или неудача
 * @param message Сообщение для вывода
 */
function showInformation(success, message) {
    if (success) {
        $('#order-div').empty().append(`
            <div class="alert alert-success alert-dismissible fade show" role="alert">
              <strong>${message}</strong>
              <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            `)
        window.setTimeout(function () {
            $('.alert').alert('close');
        }, 5000)
    } else {
        $('#order-div').empty().append(`
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
              <strong>${message}</strong>
              <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            `)
        window.setTimeout(function () {
            $('.alert').alert('close');
        }, 5000)
    }
}