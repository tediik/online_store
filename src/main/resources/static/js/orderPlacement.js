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
            }
            else {
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
            if(obj && obj.parents){
                $.fias.setValues(obj.parents, '.js-form-address');
            }

            if(obj && obj.zip){
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
    $building.fias('withParents',true);


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

function test() {

}

function addressChange() {
    if (document.getElementById("shopAddress").hidden) {
        getShopAddress()
        $('#addressId').attr("hidden",true);
        $('#shopAddress').attr("hidden",false);
    } else {
        $('#addressId').attr("hidden",false);
        $('#shopAddress').attr("hidden",true);
    }
}

function getShopAddress() {
    fetch("customer/rest/allShops", {
        method: 'GET',
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    }).then(response => response.json()).then(shops => showShops(shops))
}

function showShops(shops) {
    $('#shopAddress').empty().append(`
            <h3>Заберу заказ по адресу:</h3>
        `);
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
function confirmOrder() {
    if(document.getElementById("shopAddress").hidden){

    } else {
        let id = $("input[name=shops]").filter(":checked").val();
    }
}