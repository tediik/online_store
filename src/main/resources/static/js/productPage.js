fetch("/api/products/product")
    .then(response => response.json())
    .then(function (data) {
        fillBreadcrumb(data);
        fillAboutProduct(data);
    });

async function fillBreadcrumb(data) {
    let categories = await fetch("/api/categories/categories").then(response => response.json());
    let categoriesWithLatin = await fetch("/api/categories").then(response => response.json());

    let breadcr = document.getElementById('categoriesBreadcrumb');
    let categoryOfProduct;
    let subcategoryOfProduct;

    // определение категории для продукта
    top:
        for (let i in categories) {
            let prodInCategory = categories[i].products;
            for (let j in prodInCategory) {
                if (prodInCategory[j].id == data.id) {
                    categoryOfProduct = categories[i].superCategory;
                    subcategoryOfProduct = categories[i].category;
                    break top;
                }
            }
        }

    let subcategoryOfProductInLatin;
    // поиск наименования категории на латинице
    otherTop:
        for (let key in categoriesWithLatin) {
            if (key == categoryOfProduct) {
                for (let value = 0; value < categoriesWithLatin[key].length; value += 2) {
                    if (categoriesWithLatin[key][value] == subcategoryOfProduct) {
                        subcategoryOfProductInLatin = categoriesWithLatin[key][value + 1];
                        break otherTop;
                    }
                }
            }
        }

    $(breadcr).append(`<li class="breadcrumb-item"><a href="/">Главная</a></li>`);
    $(breadcr).append(`<li class="breadcrumb-item"><a>${categoryOfProduct}</a></li>`);
    $(breadcr).append(`<li class="breadcrumb-item"><a href="/categories/${subcategoryOfProductInLatin}">${subcategoryOfProduct}</a></li>`);
    $(breadcr).append(`<li class="breadcrumb-item"><a>${data.product}</a></li>`);
}

async function fillAboutProduct(data) {
    let productName = document.getElementById('productName');
    $(productName).append(`<br><h2 class="font-weight-normal">${data.product}</h2>`);
    $(productName).append(`<br><h5 class="font-weight-normal">Оценка товара: ${data.rating} из 5</h5>`);

    let description = document.getElementById('text-description');
    $(description).append(`${data.descriptions.information}`);

    let specifications = document.getElementById('text-specifications');
    for (let key in data.descriptions) {
        if (key != "id" && key != "information") {
            let content = `<tr><td class="font-weight-bold">${key}</td>
                           <td>${data.descriptions[key]}</td>
                       </tr>`;
            $(specifications).append(content);
        }
    }

}