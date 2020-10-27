$(function () {
    fillProductCategories().then();
})

$('.accordion').accordion({
    header: '> .accordion-item > .accordion-header',
    heightStyle: 'content',
    active: true,
    collapsible: true
});
$("#depth-5", "#depth-4").accordion({
    disabled: true
});

async function fillProductCategories() {
    let insideAccordion = $('#accordionProductsCategories');
    let data = await fetch("/api/categories").then(response => response.json()); //await - ?
    let data2 = await fetch("/api/categories/sub/17").then(response => response.json());

    for (let count in data) {
        let tmpCount = data[count].id;
        console.log('parent - ' + data[count].parentCategoryId);
        let mainCat = `
            <div class="card">
                <div class="card-header" role="tab" id="mainCategory${tmpCount}">
                    <a class="collapsed" data-toggle="collapse" data-parent="#accordionProductsCategories" href="#collapse${tmpCount}"
                        aria-expanded="false" aria-controls="collapse${tmpCount}">
                        <h5 class="mb-0">${data[count].category}
                            <span class="float-right">
                                <div class="btn-group btn-group-sm" role="group" aria-label="Basic example">
                                    <button type="button" class="btn btn-outline-info" data-toggle="modal" data-target="#editProductCategoryModal"
                                    onclick="getProductCategory(${tmpCount})">Переименовать</button>
                                    <button type="button" class="btn btn-outline-danger" onclick="deleteProductCategory(${tmpCount})">Удалить</button>
                                </div>
                            </span>
                        </h5>
                    </a>
                </div>
                <div id="collapse${tmpCount}" class="collapse" role="tabpanel" aria-labelledby="mainCategory${tmpCount}"
                    data-parent="#accordionProductsCategories">
                    <div class="card-body" id="subCategory">
                        ${await fillProductSubCategories(tmpCount)}                     
                    </div>
                </div>
            </div>
        `;
        $(insideAccordion).append(mainCat);
    }
}
async function fillProductSubCategories(tmpId) {
    console.log('!!!!!!!!   -   ' + tmpId);
    let insideSubAccordion = document.getElementById('subCategory');
    let subCat = `<div class="accordion" id="accordionProductsSubCategories">
                        <p>${tmpId} - jhgfdghs</p>
                  </div>
    `;
    $(insideSubAccordion).append(subCat);
}