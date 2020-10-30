/**
 * Слушатели событий
 */
$(document).ready(function () {
    getAllCategories();

    $('#addCategoryButton').click(function (event) {
        addNewCategory();
        event.preventDefault();
    })
    $('#addTopicButton').click(function (event) {
        addNewTopic();
        event.preventDefault();
    })
    $('#editCategoryButton').click(function (event) {
        editCategory($('#editCategoryId').val());
        event.preventDefault();
    })
    $('#editTopicButton').click(function (event) {
        editTopic($('#editTopicId').val());
        event.preventDefault();
    })
})

/**
 * Делает запрос всех категорий
 */
function getAllCategories() {
    fetch("/api/manager/topicsCategory")
        .then(response => {
            if (response.status === 200) {
                response.json()
                    .then(topicsCategories => {
                        for (let topicsCategory of topicsCategories) {
                            if (topicsCategory.actual === true) {
                                renderActualCategories(topicsCategory);
                            } else {
                                renderArchiveCategories(topicsCategory);
                            }
                        }
                    })
            }
        })
}

/**
 * Вставляет все актуальные категории на фронт
 * @param topicsCategories категории, которые будут вставлены
 */
function renderActualCategories(topicsCategory) {
    let categoriesBody = document.querySelector('#accordionCategories');
    let insertBody = makeActualCategoryBody(topicsCategory);
    categoriesBody.insertAdjacentHTML("afterbegin", insertBody);
}

/**
 * Вставляет все архивные категории на фронт
 * @param topicsCategories категории, которые будут вставлены
 */
function renderArchiveCategories(topicsCategory) {
    let categoriesBody = document.querySelector('#accordionCategoriesArchive');
    let insertBody = makeArchiveCategoryBody(topicsCategory);
    categoriesBody.insertAdjacentHTML("afterbegin", insertBody);
}

/**
 * Отрисовывает тело актуальной категории
 * @param topicsCategory категория, которая будет отрисована
 * @returns {string} тело категории в html
 */
function makeActualCategoryBody(topicsCategory) {
    let topicsTable = `<ul class="list-group list-group-flush" id="topic${topicsCategory.id}">`;
    if (topicsCategory.topics !== null && topicsCategory.topics.length !== 0) {
        for (let tops of topicsCategory.topics) {
            topicsTable += `<li class="list-group-item" id="row${tops.id}">
                                <span class="text-left">${tops.topicName}</span>
                                <span class="float-right">
                                    <button type="button" class="btn btn-outline-info btn-sm" data-toggle="modal" data-target="#editTopicModal" onclick="getTopic(${tops.id})">Переименовать</button>
                                </span>                        
                            </li>`
        }
    } else {
        topicsTable += `<li class="list-group-item" id="nullRow${topicsCategory.id}">
                           <span class="text-left">Тем пока что нет</span>                                                      
                        </li>`;
    }
    topicsTable += `<li class="list-group-item">
                            <button type="button" class="btn btn-primary float-right btn-sm" data-toggle="modal" data-target="#addNewTopicModal" onclick="getCategoryForNewTopic(${topicsCategory.id})">
                                Добавить
                            </button>
                        </li>
                    </ul>`
    return `<div class="card" id="categoryCard${topicsCategory.id}">
                <div class="card-header" id="heading${topicsCategory.id}">
                    <h5 class="mb-0">                    
                    <button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapse${topicsCategory.id}" aria-expanded="false" aria-controls="collapse${topicsCategory.id}">
                      ${topicsCategory.categoryName}
                    </button>
                    <span class="float-right">
                        <div class="btn-group btn-group-sm" role="group" aria-label="Basic example">
                            <button type="button" class="btn btn-outline-info" data-toggle="modal" data-target="#editCategoryModal" onclick="getCategory(${topicsCategory.id})">Переименовать</button>
                            <button type="button" class="btn btn-outline-danger" onclick="archiveCategory(${topicsCategory.id})">Архивировать</button>
                        </div>
                    </span>
                    </h5>
                </div>
                <div id="collapse${topicsCategory.id}" class="collapse" aria-labelledby="heading${topicsCategory.id}" data-parent="#accordionCategories">
                    <div class="card-body">
                        ${topicsTable}
                    </div>
                </div>
          </div>`
}

/**
 * Отрисовывает тело архивной категории
 * @param topicsCategory категория, которая будет отрисована
 * @returns {string} тело категории в html
 */
function makeArchiveCategoryBody(topicsCategory) {
    let topicsTable = `<ul class="list-group list-group-flush" id="archiveTopic${topicsCategory.id}">`;
    if (topicsCategory.topics !== null && topicsCategory.topics.length !== 0) {
        for (let tops of topicsCategory.topics) {
            topicsTable += `<li class="list-group-item" id="archiveRow${tops.id}">
                                <span class="text-left">${tops.topicName}</span>
                            </li>`
        }
    } else {
        topicsTable += `<li class="list-group-item" id="nullArchiveRow${topicsCategory.id}">
                           <span class="text-left">Тем нет</span>                                                      
                        </li>`;
    }
    return `<div class="card" id="categoryCardArchive${topicsCategory.id}">
                <div class="card-header" id="headingArchive${topicsCategory.id}">
                    <h5 class="mb-0">                    
                    <button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseArchiveCategory${topicsCategory.id}" aria-expanded="false" aria-controls="collapseArchiveCategory${topicsCategory.id}">
                        ${topicsCategory.categoryName}
                    </button>
                    <span class="float-right">
                        <button type="button" class="btn btn-outline-info btn-sm" onclick="unarchiveCategory(${topicsCategory.id})">Разархивировать</button>
                    </span>
                    </h5>
                </div>
                <div id="collapseArchiveCategory${topicsCategory.id}" class="collapse" aria-labelledby="headingArchiveCategory${topicsCategory.id}" data-parent="#accordionCategoriesArchive">
                    <div class="card-body">
                        ${topicsTable}
                    </div>
                </div>
            </div>`;
}

/**
 * Делает запрос конкретной категории
 * @param id идентификатор категории
 */
function getCategory(id) {
    fetch("/api/manager/topicsCategory/" + id)
        .then(response => {
            if (response.status === 200) {
                response.json()
                    .then(topicsCategory => completeEditCategory(topicsCategory));
            }
        })
}

/**
 * Заполняет модальное окно для редактирования категории
 * @param topicsCategory категория, которая будет заполнена в модальном окне для последующего редактирования
 */
let topic;
function completeEditCategory(topicsCategory) {
    topic = topicsCategory.topics;
    let editCategoryForm = document.forms['editCategoryForm'];
    editCategoryForm.elements['editCategoryId'].value = topicsCategory.id;
    editCategoryForm.elements['editCategoryName'].value = topicsCategory.categoryName;
}

/**
 * Делает запрос на добавление новой категории
 */
function addNewCategory() {
    if ($('#addNewCategory').val() !== '') {
        fetch("/api/manager/topicsCategory", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                categoryName: $('#addNewCategory').val()
            })
        })
            .then(response => {
                if (response.status === 409) {
                    toastr.error('Такая категория уже существует')
                }
                if (response.status === 200) {
                    response.json()
                        .then(topicsCategory => renderCategory(topicsCategory))
                }
            })
    } else {
        toastr.error('Введите название категории');
    }

}

/**
 * Вставляет категорию на фронт
 * @param topicsCategories категория, которая будет вставлена
 */
function renderCategory(topicsCategory) {
    let cardsBody = document.querySelector("#accordionCategories");
    cardsBody.insertAdjacentHTML("afterbegin", makeActualCategoryBody(topicsCategory));
    $('#addNewCategoryModal').modal('hide');
    document.forms["addCategoryForm"].reset();
}

/**
 * Делает запрос на измененние категории
 * @param id идентификатор категории
 */
function editCategory(id) {
    if ($('#editCategoryName').val() !== '') {
        fetch("/api/manager/topicsCategory/" + id, {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                id: $('#editCategoryId').val(),
                categoryName: $('#editCategoryName').val(),
                topics: topic
            })
        }).then(response => {
            if (response.status === 409) {
                toastr.error('Такая категория уже существует')
            }
            if (response.status === 200) {
                response.json()
                    .then(changedCategory => changeCategoryBody(changedCategory));
            }
        })
    } else {
        toastr.error('Введите новое название категории');
    }
}

/**
 * Перевсатвляет измененную категорию на фронт
 * @param changedCategory измененная категория, которая будет вставлена
 */
function changeCategoryBody(changedCategory) {
    let cardsBody = document.querySelector(`#categoryCard${changedCategory.id}`);
    cardsBody.insertAdjacentHTML("beforebegin", makeActualCategoryBody(changedCategory));
    cardsBody.remove();
    $('#editCategoryModal').modal('hide');
    document.forms["editCategoryForm"].reset();
}

/**
 * Делает запрос на архивацию категории и удаляет ее из актуальных категорий на фронте
 * @param id идентификатор категории
 */
function archiveCategory(id) {
    fetch("/api/manager/topicsCategory/archive/" + id, {
        method: "PUT",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id: id
        })
    }).then(response => {
        if (response.status === 200) {
            response.json()
                .then(archiveCategory => {
                    deleteCategoryBodyFromActual(archiveCategory);
                })
        }
    })

    function deleteCategoryBodyFromActual(archiveCategory) {
        let cardsBody = document.querySelector(`#categoryCard${archiveCategory.id}`);
        cardsBody.remove();
        renderArchiveCategories(archiveCategory);
    }
}

/**
 * Делает запрос на разархивацию категории и удаляет ее из архивных категорий на фронте
 * @param id идентификатор категории
 */
function unarchiveCategory(id) {
    fetch("/api/manager/topicsCategory/unarchive/" + id, {
        method: "PUT",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id: id
        })
    }).then(response => {
        if (response.status === 200) {
            response.json()
                .then(actualCategory => {
                    deleteCategoryBodyFromArchive(actualCategory);
                })
        }
    })

    function deleteCategoryBodyFromArchive(actualCategory) {
        let cardsBody = document.querySelector(`#categoryCardArchive${actualCategory.id}`);
        cardsBody.remove();
        renderActualCategories(actualCategory);
    }
}

/**
 * Делает запрос конкретной темы
 * @param id идентификатор темы
 */
function getTopic(id) {
    fetch("/api/manager/topic/" + id)
        .then(response => {
            if (response.status === 200) {
                response.json()
                    .then(topic => completeEditTopic(topic));
            }
        })
}

/**
 * Заполняет модальное окно для редактирования темы
 * @param topic тема, которая будет заполнена в модальном окне для последующего редактирования
 */
let category;
function completeEditTopic(topic) {
    category = topic.topicsCategory;
    let editTopicForm = document.forms['editTopicForm'];
    editTopicForm.elements['editTopicId'].value = topic.id;
    editTopicForm.elements['editTopicName'].value = topic.topicName;
}

/**
 * Делает запрос на измененние темы
 * @param id идентификатор темы
 */
function editTopic(id) {
    if ($('#editTopicName').val() !== '') {
        fetch("/api/manager/topic/" + id, {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                id: $('#editTopicId').val(),
                topicName: $('#editTopicName').val(),
                topicsCategory: category
            })
        }).then(response => {
            if (response.status === 409) {
                toastr.error('Такая тема уже существует')
            }
            if (response.status === 200) {
                response.json()
                    .then(changedTopic => changeTopicRow(changedTopic));
            }
        })
    } else {
        toastr.error('Введите новое название темы');
    }
}

/**
 * Перевсатвляет измененную тему на фронт
 * @param changedTopic измененная тема, которая будет вставлена
 */
function changeTopicRow(changedTopic) {
    let topicRow = document.querySelector(`#row${changedTopic.id}`);
    topicRow.insertAdjacentHTML("beforebegin", makeTopicRow(changedTopic));
    topicRow.remove();
    $('#editTopicModal').modal('hide');
    document.forms["editTopicForm"].reset();
}

/**
 * Отрисовывает тело темы
 * @param topic тема, которая будет отрисована
 * @returns {string} тело темы в html
 */
function makeTopicRow(topic) {
    return `<li class="list-group-item" id="row${topic.id}">
                <span class="text-left">${topic.topicName}</span>
                <span class="float-right">
                    <button type="button" class="btn btn-outline-info btn-sm" data-toggle="modal" data-target="#editTopicModal" onclick="getTopic(${topic.id})">Переименовать</button>
                </span>                        
            </li>`
}

/**
 * Делает запрос конкретной категории для ее вставки в новую тему
 * @param id идентификатор категории
 */
function getCategoryForNewTopic(id) {
    fetch("/api/manager/topicsCategory/" + id)
        .then(response => {
            if (response.status === 200) {
                response.json()
                    .then(topicsCategory => completeAddTopic(topicsCategory));
            }
        })
}

/**
 * Присваивает переменной категорию для новой темы
 */
let categoryForAdd;
function completeAddTopic(topicsCategory) {
    categoryForAdd = topicsCategory;
}

/**
 * Делает запрос на добавление новой темы
 */
function addNewTopic() {
    if ($('#addNewTopic').val() !== '') {
        fetch("/api/manager/topic", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                topicName: $('#addNewTopic').val(),
                topicsCategory: categoryForAdd
            })
        })
            .then(response => {
                if (response.status === 409) {
                    toastr.error('Такая тема уже существует')
                }
                if (response.status === 200) {
                    response.json()
                        .then(topic => renderTopic(topic))
                }
            })
    } else {
        toastr.error('Введите название темы');
    }
}

/**
 * Вставляет новую категорию
 * @param topic
 */
function renderTopic(topic) {
    let topicRow;
    if (topic.topicsCategory.topics.length === 0) {
        topicRow = document.querySelector(`#nullRow${topic.topicsCategory.id}`);
        topicRow.insertAdjacentHTML("beforebegin", makeTopicRow(topic));
        topicRow.remove();
    } else {
        topicRow = document.querySelector(`#topic${topic.topicsCategory.id}`);
        topicRow.insertAdjacentHTML("afterbegin", makeTopicRow(topic));
    }
    $('#addNewTopicModal').modal('hide');
    document.forms["addTopicForm"].reset();
}



