let shops = {};
let adresses = {};

/**
 * Функция очистки полей формы
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
 * Функция смены значения чекбокса доставка/самовывоз
 */
function addressChange() {
    if (document.getElementById("shopAddress").hidden) {
        getShopAddress()
        $('#addressId').attr("hidden", true);
        $('#shopAddress').attr("hidden", false);
        $('#clear-button').attr("hidden", true);
        $('#userAddresses').attr("hidden", true);
    } else {
        $('#shopAddress').attr("hidden", true);
        $('#clear-button').attr("hidden", false);
        $('#userAddresses').attr("hidden", false);
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
    let id = null;
    if (document.getElementById("shopAddress").hidden) {
        id = $("input[name=userAddresses]").filter(":checked").val();
    } else {
        id = $("input[name=shops]").filter(":checked").val();
    }
    if (id == null) {
        showModalError("Вы не выбрали адрес доставки")
    } else {
        fetch("/customer/basketGoods", {
            method: 'POST',
            body: id,
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

/**
 * Функция для получения адресов, на которые пользователь уже оформлял заказ
 */
function userAdresses() {
    fetch("/customer/rest/userAddresses", {
        method: 'GET',
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    }).then(function (response) {
        if (response.ok) {
            response.json().then(userAddresses => printUserAdresses(userAddresses));
        } else {
            $('#userAddresses').empty().append(`
            <h3>Добавьте новый адрес</h3>
            <button id="addAddressButton" type="button" class="btn btn-success edit-button"
                                            onclick="confirmOrder()">
                                        Добавить новый адрес
            </button>`)
        }
    })
}

/**
 * Функция для отрисовки адресов пользователя
 * @param userAdresses адреса
 */
function printUserAdresses(userAdresses) {
    adresses = userAdresses;
    $('#userAddresses').empty().append(`
            <h3>Выберите адресс либо добавьте новый: </h3>`);
    for (let i = 0; i < adresses.length; i++) {
        $('#userAddresses').append(`<div>
                    <input type="radio" id="address${adresses[i].id}"
                    name="userAddresses" value="${adresses[i].id}">
                        <label for="shop${adresses[i].id}">
                                      ${addressToStr(adresses[i])}
                        </label>
                    </div>
                        `);
    }
    $('#userAddresses').append(`
        <button id="clear-button" type="button" class="btn btn-secondary"
        onclick="openAddressForm()">Добавить новый адрес
        </button>
        `)
}

/**
 * Функиця добавления адреса пользователю
 */
function addAddress() {
    if ($('[class="kladr-error"]').attr('name')) {
        showModalError("Заполните поля выделенные красным Корректно!");
    } else {
        if ($('[name="zip"]').val() !== "" &&
            $('[name="region"]').val() !== "" &&
            $('[name="city"]').val() !== "" &&
            $('[name="street"]').val() !== "" &&
            $('[name="building"]').val() !== "") {
            let distr = null;
            let flat = null;
            if ($('[name="district"]').val().length != 0) {
                distr = $('[name="district"]').val();
            }
            if ($('[name="flat"]').val().length != 0) {
                flat = $('[name="flat"]').val()
            }
            address = {
                region: $('[name="region"]').val(),
                city: $('[name="city"]').val(),
                district: distr,
                street: $('[name="street"]').val(),
                building: $('[name="building"]').val(),
                zip: $('[name="zip"]').val(),
                flat: flat,
                shop: false
            };
            fetch("/customer/rest/addAddress", {
                method: 'POST',
                body: JSON.stringify(address),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then(function (response) {
                if (response.ok) {
                    $('#addressId').attr("hidden", true);
                    showModalSuccess("Адрес успешно добавлен")
                    userAdresses();
                } else {
                    response.text().then(function (text) {
                        if(text === "addressIsExists") {
                            showModalError("У вас уже есть такой адрес")
                        }
                    })
                    showModalError("Не удалось добавить адрес")
                }
            })
        } else {
            showModalError("Заполните все поля отмеченные звёздочкой");
        }
    }
}

/**
 * Функиця отображения формы для добавления адреса
 */
function openAddressForm() {
    $('#addressId').attr("hidden", false);
}

/**
 * Функция формирует из объекта адреса строку
 */
function addressToStr(address) {
    let addressStr = address.region + ",";
    if (address.district != null) {
        addressStr = addressStr + address.district + ",";
    }
    addressStr = addressStr + `${address.city},${address.street},${address.building}`;
    return addressStr;
}

/**
 * Функция сообщений об ошибке
 * @param message текст сообщения
 */
function showModalError(message) {
    $('#alert-modal-div').empty().append(`
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
              <strong>${message}</strong>
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

/**
 * Функция воводящая сообщение об успехе
 * @param message текст сообщения
 */
function showModalSuccess(message) {
    $('#alert-modal-div').empty().append(`
            <div class="alert alert-success alert-dismissible fade show" role="alert">
              <strong>${message}</strong>
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