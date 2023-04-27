sap.ui.define([
	"./AbstractController",
	'sap/m/MessageBox',
	'sap/m/MessageToast'
], function (AbstractController, MessageBox, MessageToast) {
	"use strict";

	return AbstractController.extend("sap.f.ShellBarWithFlexibleColumnLayout.controller.AbstractDetailController", {
		onInit: function () {
			this.oRouter = this.getOwnerComponent().getRouter();

			this.oRouter.getRoute("detail").attachPatternMatched(this._onMatched, this);
		},
		handleFullScreen: function () {
			this.navToLayoutProperty("/actionButtonsInfo/midColumn/fullScreen");
		},
        restartConnector: function () {
            console.log("Implement Connector Restart here");
        },
		navToLayoutProperty: function (sLayoutProperty) {
			let oLayoutModel = this.getOwnerComponent().getModel("Layout");
			let sNextLayout = oLayoutModel.getProperty(sLayoutProperty);
			let oParams = { layout: sNextLayout };
			oParams["id"] = this._entity;
			this.oRouter.navTo("detail", oParams);
		},
		handleExitFullScreen: function () {
			this.navToLayoutProperty("/actionButtonsInfo/midColumn/exitFullScreen");
		},
		handleClose: function () {
			let oLayoutModel = this.getOwnerComponent().getModel("Layout");
			let sNextLayout = oLayoutModel.getProperty("/actionButtonsInfo/midColumn/closeColumn");
			this.oRouter.navTo("master", { layout: sNextLayout });
		},
		onEdit: function () {
			this.enableEditMode(true);
		},
		onSave: function (oEvent) {
			let oModel = this.getView().getModel();
			if (this.validateResource()) {
				let fnSuccess = function (oData) {
					this.enableEditMode(false);
					MessageToast.show(this.translate(this.getEntityName()) + ' ' + this.translate("msgSaveResourceSuccessful"));
				}.bind(this);

				let fnError = function (oError) {
					this.enableEditMode(false);
					MessageBox.show(this.translate(this.getEntityName()) + ' ' + this.translate("msgSaveResouceFailed", [oError.statusCode, oError.statusText]));
				}.bind(this);

				let oRequest = oModel.submitChanges(this.getEntityName().toLowerCase() + "Details", fnSuccess, fnError);
				if (!oRequest) {
					this.enableEditMode(false);
				}
			}
		},
		validateResource: function () {
			return true;
		},
		onCancel: function (oEvent) {
			this.enableEditMode(false);
			this.getView().getModel().resetChanges();
		},
		onDelete: function (oEvent) {
			this.getOwnerComponent().getRouter().navTo("master");
		},
		_onMatched: function (oEvent) {
			this._entity = oEvent.getParameter("arguments")["id"];
			this.getView().bindElement({
				path: "/" + this.getEntityName() + "/" + this._entity,
				parameters: this.getBindElementParams()
			});
		},
		getBindElementParams: function () {
			return {};
		},
		getEntityName: function () {
			throw new Error("getEntityName must be implemented by derived class");
		},
		onExit: function () {
			this.oRouter.getRoute(this.getEntityName().toLowerCase() + "-master").detachPatternMatched(this._onMatched, this);
			this.oRouter.getRoute(this.getEntityName().toLowerCase() + "-detail").detachPatternMatched(this._onMatched, this);
		},
		enableEditMode: function (bEditMode) {
			this.getView().getModel("appState").setProperty("/editMode", bEditMode);
		}
	});
}, true);