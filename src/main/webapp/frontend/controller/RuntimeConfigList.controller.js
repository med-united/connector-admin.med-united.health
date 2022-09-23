sap.ui.define([
   "sap/ui/core/mvc/Controller",
   "sap/ui/model/Filter",
   "sap/ui/model/FilterOperator"
], function (Controller, Filter, FilterOperator) {
   "use strict";
   return Controller.extend("frontend.controller.RuntimeConfigList", {
	   onItemPress : function (oEvent) {
		   this.getOwnerComponent().getRouter().navTo("RuntimeConfigs", {
			    "id" : oEvent.getParameter("listItem").getBindingContext().getProperty("Id")
		   });
	   },
	   onDelete : function (oEvent) {
		   var list = this.byId("list");
		   var dataModel = this.getView().getModel();
		   list.getSelectedContexts().forEach(function (c) {
			   dataModel.remove("/RuntimeConfigs('"+c.getProperty("Id")+"')");
		   });
	   },
	   onAdd : function (oEvent) {
		   this.getOwnerComponent().getRouter().navTo("addRuntimeConfig");
	   },
	   onRefresh: function () {
		   this.byId("list").getBinding("items").refresh();
	   },
	   onSearch: function(oEvent) {
		   this.byId("list").getBinding("items").filter(
				   new Filter("Note", FilterOperator.Contains, oEvent.getParameter("newValue")));
	   }
   });
});