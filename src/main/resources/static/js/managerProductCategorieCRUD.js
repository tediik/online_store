$(document).ready(function () {
    fillProductCategories().then();
})

async function fillProductCategories() {
    let insideAccordion = document.getElementById('accordionProductsCategories');
    let data = await fetch("/api/categories").then(response => response.json()); //await - ?
    for (let count in data) {
        console.log(data[count].category);
        let tmp = `
            <div class="card">
                <div class="card-header" role="tab" id="mainCategory${data[count].id}">
                    <a class="collapsed" data-toggle="collapse" data-parent="#accordionProductsCategories" href="#collapse${data[count].id}"
                        aria-expanded="false" aria-controls="collapse${data[count].id}">
                        <h5 class="mb-0">${data[count].category}
                            <span class="float-right">
                                <div class="btn-group btn-group-sm" role="group" aria-label="Basic example">
                                    <button type="button" class="btn btn-outline-info" data-toggle="modal" data-target="#editProductCategoryModal" onclick="getProductCategory(${data[count].id})">Переименовать</button>
                                    <button type="button" class="btn btn-outline-danger" onclick="deleteProductCategory(${data[count].id})">Удалить</button>
                                </div>
                            </span>
                        </h5>
                    </a>
                </div>
                <div id="collapse${data[count].id}" class="collapse" role="tabpanel" aria-labelledby="mainCategory${data[count].id}"
                    data-parent="#accordionProductsCategories">
                    <div class="card-body">
                        <p>There is nothing here</p>
                    </div>
                </div>
            </div>
        `;
        $(insideAccordion).append(tmp);
    }
}