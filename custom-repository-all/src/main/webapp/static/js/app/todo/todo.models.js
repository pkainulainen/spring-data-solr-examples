//Models
TodoApp.Models.FeedbackMessage = Backbone.Model.extend();

TodoApp.Models.Todo = Backbone.Model.extend({
    urlRoot: "/api/todo"
});

//Collections
TodoApp.Collections.Todos = Backbone.Collection.extend({
    model: TodoApp.Models.Todo,
    url: function() {
        return "/api/todo";
    }
})

TodoApp.Collections.TodoSearchResults = Backbone.PageableCollection.extend({
    model: TodoApp.Models.Todo,
    url: function() {
        return "/api/todo/search/" + this.searchTerm;
    },
    state: {
        firstPage: 0,
        order: TodoApp.Pagination.order,
        pageSize: TodoApp.Pagination.pageSize,
        sortKey: TodoApp.Pagination.sortProperty,
        totalRecords: this.searchResultCount
    },
    queryParams: {
        currentPage: "page.page",
        pageSize: "page.size",
        sortKey: "page.sort",
        order: "page.sort.dir"
    }
})