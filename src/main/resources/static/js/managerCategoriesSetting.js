/**
 * Слушатели событий
 */
$(document).ready(function () {
    $('#nav-categories-and-topics-tab').click(function () {
        getCategoriesTable();
    })
    $('#addCategoryButton').click(function (event) {
        addNewCategory();
        event.preventDefault();
    })
    $('#editCategoryButton').click(function (event) {
        editCategory($('#editId').val());
        event.preventDefault();
    })
})

/**
 * Делает запрос всех категорий
 */
function getCategoriesTable() {
    fetch("/api/manager/categories")
        .then(response => {
            if (response.status === 200) {
                response.json()
                    .then(categories => renderCategoriesTable(categories))
            }
        })
}

/**
 * Отрисовывает все категории в таблицу на фронт
 * @param categories категории, которые отрисовываются в таблицу
 */
function renderCategoriesTable(categories) {
    let rows = "";
    for (let category of categories) {
        rows += row(category);
    }

    let categoriesBody = document.querySelector('#categoriesBody');
    categoriesBody.insertAdjacentHTML("afterbegin", rows);
}

/**
 * Помогает методу renderCategoriesTable()
 * @param category категория, которая будет отрендерена в строку
 * @returns {string} возвращает строку таблицы
 */
function row(category) {
    return `<tr data-rowId="${category.id}"> 
                <td>${category.id}</td>
                <td>${category.category}</td>
                <td>${category.superCategory}</td>
                <td><button type="button" class="btn btn-info" data-toggle="modal" data-target="#editCategoryModal" onclick="getCategory(${category.id})">Edit</button></td>
                <td><button type="button" class="btn btn-danger" id="deleteCategoryButton" onclick="deleteCategory(${category.id})">Delete</button></td>
            </tr>`
}

/**
 * Делает запрос конкретной категории
 * @param id идентификатор категории
 */
function getCategory(id) {
    fetch("/api/manager/categories/" + id)
        .then(response => {
            if (response.status === 200) {
                response.json()
                    .then(category => completeModal(category));
            }
        })
}

/**
 * Делает запрос на добавление новой категории
 */
function addNewCategory() {
    fetch("/api/manager/categories", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            category: $('#addNewCategory').val(),
            superCategory: $('#addNewSuperCategory').val()
        })
    })
        .then(response => {
            if (response.status === 200) {
                response.json()
                    .then(categories => renderCategoriesInCategoriesTable(categories))
            } else {
                console.log("Problem: " + response.status);
            }
        })
}

/**
 * Отрисовывет новую категорию в таблицу на фронт
 * @param categories - новая категория, которая будет отрендерена в общую таблицу
 */
function renderCategoriesInCategoriesTable(categories) {
    let tbody = document.querySelector("#categoriesBody");
    tbody.insertAdjacentHTML("beforeend", row(categories));

    $('#addNewCategoryModal').modal('hide');
    document.forms["addCategoryForm"].reset();
}

/**
 * Заполняет модальное окно для редактирования категории
 * @param category - категория, которая будет заполнена в модальном окне для последующего редактирования
 */
function completeModal(category) {
    let editForm = document.forms['editCategoryForm'];
    editForm.elements['editId'].value = category.id;
    editForm.elements['editCategory'].value = category.category;
    editForm.elements['editSuperCategory'].value = category.superCategory
}

/**
 * Делает запрос на измененние категории
 * @param id идентификатор категории
 */
function editCategory(id) {
    fetch("/api/manager/categories/" + id, {
        method: "PUT",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id: $('#editId').val(),
            category: $('#editCategory').val(),
            superCategory: $('#editSuperCategory').val()
        })
    }).then(response => {
        if (response.status === 200) {
            response.json()
                .then(changedCategory => renderChangedCategoryInCategoriesTable(changedCategory));
        }
    })
}

/**
 * Отрисовывает измененную категорию в таблицу на фронт
 * @param changedCategory измененная категория, которая будет перерендерена в общую таблицу
 */
function renderChangedCategoryInCategoriesTable(changedCategory) {
    let tr = document.querySelector(`tr[data-rowId="${changedCategory.id}"]`);
    tr.insertAdjacentHTML("beforebegin", row(changedCategory));
    tr.remove();

    $('#editCategoryModal').modal('hide');
    document.forms["editCategoryForm"].reset();
}

/**
 * Делает запрос на удаление категории
 * @param id идентификатор категории
 */
function deleteCategory(id) {
    fetch("/api/manager/categories/" + id, {
        method: "DELETE"
    })
        .then(response => {
            if (response.status === 204) {
                deleteCategoryFromCategoriesTable(id);
            }
        })
}

/**
 * Стирает удаленную категорию из таблицы на фронте
 * @param id идентификатор категории
 */
function deleteCategoryFromCategoriesTable(id) {
    let tr = document.querySelector(`tr[data-rowId="${id}"]`);
    tr.remove();
}


