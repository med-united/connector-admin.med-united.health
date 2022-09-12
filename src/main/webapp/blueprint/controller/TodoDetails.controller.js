sap.ui.define([ "sap/ui/core/mvc/Controller" ], function(Controller) {
	"use strict";
	return Controller.extend("blueprint.controller.TodoDetails", {
		onInit : function() {
			var me = this;
			this.getOwnerComponent().getRouter().attachRouteMatched(function (oEvent) {
				var name = oEvent.getParameter("name");
				if(name == "Todos") {
					me.getView().bindElement("/"+name+"('"+oEvent.getParameter("arguments").id+"')");
				} else if(name == "addTodo") {
					me.getView().getModel().metadataLoaded().then(function() {
						var context = me.getView().getModel().createEntry("/Todos", {
							success: function () {
								me.getOwnerComponent().getRouter().navTo("Home");
							}
						});
						me.getView().setBindingContext(context);
					});
				}
			});
		},
		onSave : function(oEvent) {
			this.getView().getModel().submitChanges();
		},
		onNavButtonPress : function (oEvent) {
			this.getOwnerComponent().getRouter().navTo("Home");
		}
	});
});