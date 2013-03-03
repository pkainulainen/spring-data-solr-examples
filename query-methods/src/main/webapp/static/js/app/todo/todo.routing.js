TodoApp.TodoRouting = function(){
    var TodoRouting = {};

    TodoRouting.Router = Backbone.Marionette.AppRouter.extend({
        appRoutes: {
            "todo/add": "add",
            "todo/:id": "view",
            "todo/update/:id": "update",
            "todo/search/:searchTerm": "search",
            "": "list"
        }
    });

    TodoApp.addInitializer(function(){
        TodoRouting.router = new TodoRouting.Router({
            controller: TodoApp.Controllers.TodoController
        });

        TodoApp.vent.trigger("routing:started");
    });

    return TodoRouting;
}();
