sap.ui.define([
	'./AbstractMasterController',
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator",
	'sap/ui/model/Sorter',
	'sap/m/MessageBox'
], function (AbstractMasterController, JSONModel, Controller, Filter, FilterOperator, Sorter, MessageBox) {
	"use strict";

	return AbstractMasterController.extend("sap.f.ShellBarWithFlexibleColumnLayout.controller.Master", {
		onInit: function () {
			this.oRouter = this.getOwnerComponent().getRouter();
			this._bDescendingSort = false;
		},
		onListItemPress: function (oEvent) {
			this.oRouter.navTo("detail");
			/*MessageBox.show("This functionality is not ready yet.", {
				icon: MessageBox.Icon.INFORMATION,
				title: "Aw, Snap!",
				actions: [MessageBox.Action.OK]
			});*/
		},
		onSearch: function (oEvent) {
			let oTableSearchState = [],
				sQuery = oEvent.getParameter("query");

			if (sQuery && sQuery.length > 0) {
				oTableSearchState = [new Filter("UserId", FilterOperator.Contains, sQuery)];
			}

			this.getView().byId("runtimeConfigTable").getBinding("items").filter(oTableSearchState, "Application");
		},

		// onAdd: function (oEvent) {
		// 	let context = this.getView().getModel().createEntry("/RuntimeConfigs", { properties: { UserId: "test" } });
		// 	this.getView().setBindingContext(context);
		// 	this.getView().getModel().submitChanges();
		// },

		remove: function (oEvent) {
			let oTable = this.getView().byId("runtimeConfigTable");
			oTable.removeItem(oEvent.getSource().getParent());
		},

		getEntityName: function () {
			return "RuntimeConfigs";
		},

		onSort: function (oEvent) {
			this._bDescendingSort = !this._bDescendingSort;
			let oView = this.getView(),
				oTable = oView.byId("runtimeConfigTable"),
				oBinding = oTable.getBinding("items"),
				oSorter = new Sorter("Name", this._bDescendingSort);

			oBinding.sort(oSorter);
		}
	});
});
