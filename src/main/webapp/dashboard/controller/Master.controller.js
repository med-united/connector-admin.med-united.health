sap.ui.define(
  [
    "./AbstractMasterController",
    "sap/ui/model/Filter",
    "sap/ui/model/FilterOperator",
    "sap/ui/model/Sorter",
    "sap/ui/core/Fragment",
    "sap/m/MessageToast",
    "sap/m/MessageBox"
  ],
  function (
    AbstractMasterController,
    Filter,
    FilterOperator,
    Sorter,
    Fragment,
    MessageToast,
    MessageBox
  ) {
    "use strict";

    return AbstractMasterController.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.Master",
      {
        onInit: function () {
          this.oRouter = this.getOwnerComponent().getRouter();
          this._bDescendingSort = false;
        },
        onListItemPress: function (oEvent) {
          let oNextUIState = this.getOwnerComponent()
              .getHelper()
              .getNextUIState(1),
            oParams = { layout: oNextUIState.layout };
          oParams["id"] = oEvent
            .getSource()
            .getBindingContext()
            .getProperty("Id");

          this.oRouter.navTo("detail", oParams);
        },
        onSearch: function (oEvent) {
          let oTableSearchState = [],
            sQuery = oEvent.getParameter("query");

          if (sQuery && sQuery.length > 0) {
            oTableSearchState = [
              new Filter("UserId", FilterOperator.Contains, sQuery),
            ];
          }

          this.getView()
            .byId("runtimeConfigTable")
            .getBinding("items")
            .filter(oTableSearchState, "Application");
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
        },

        onEdit: function(){
           const oView = this.getView();
           const aSelectedItems = oView.byId("runtimeConfigTable").getSelectedItems();
           if(aSelectedItems.length === 1){
             const me = this;
             if (!this.byId("editDialog")) {
                       // load asynchronous XML fragment
                       Fragment.load({
                         id: oView.getId(),
                         name: "sap.f.ShellBarWithFlexibleColumnLayout.view.EditDialog",
                         controller: this,
                       }).then(function (oDialog) {
                         oView.addDependent(oDialog);
                         me._openEditDialog(oDialog);
                       });
                     } else {
                       this._openEditDialog(this.byId("editDialog"));
                     }
           }
           else{
            if(aSelectedItems.length === 0){
                MessageBox.error(this.translate("msgSelectOneConnector"));
            }
            else if(aSelectedItems.length > 1){
                MessageBox.error(this.translate("msgSelectOnlyOneConnector"));
            }
           }
        },

        _openEditDialog: function (oDialog) {
            oDialog.open();
            const aSelectedItems = this.getView().byId("runtimeConfigTable").getSelectedItems();
            const oItemContextPath = aSelectedItems[0].getBindingContext().getPath();
            oDialog.bindElement(oItemContextPath);
        },

        onCancelEdit:function(){
            this.byId("editDialog").close();
        },

        onSaveEdit: function() {
          this.getView().getModel().submitChanges({
            success: () => {
              console.log("START");

              const oTable = this.getView().byId("runtimeConfigTable");
              console.log("oTable");
              console.log(oTable);
              const aSelectedItems = oTable.getSelectedItems();
              console.log("aSelectedItems");
              console.log(aSelectedItems);
              const oItemContextPath = aSelectedItems[0].getBindingContext().getPath();
              console.log("oItemContextPath");
              console.log(oItemContextPath);

              const oRuntimeConfig = this.getView().getModel().getProperty(oItemContextPath);
              console.log("----------");
              console.log(oRuntimeConfig);

              this._getConnectorType().then((connectorBrand) => {
                console.log("BRAND");
                console.log(connectorBrand);
                console.log(oItemContextPath);

                const propertyPath = oItemContextPath + "/Brand";
                this.getView().getModel().setProperty(propertyPath, connectorBrand);

                console.log(this.getView().getModel().getProperty(oItemContextPath));

                oTable.getBinding("items").refresh();
              });

              console.log(this.getView().getModel().getProperty(oItemContextPath));

              sap.m.MessageToast.show(this.translate("msgEditSuccess"));
            },
            error: () => {
              sap.m.MessageToast.show(this.translate("msgEditError"));
            },
          });
          this.byId("editDialog").close();
        },

        _getHttpHeadersFromRuntimeConfig: function () {
        	const aSelectedItems = this.getView().byId("runtimeConfigTable").getSelectedItems();
            const oItemContextPath = aSelectedItems[0].getBindingContext().getPath();
            const oData = this.getView().getModel().getProperty(oItemContextPath);

		  	return {
				Accept: "application/json",
				"x-client-system-id": oData.ClientSystemId,
				"x-client-certificate": oData.ClientCertificate,
				"x-client-certificate-password": oData.ClientCertificatePassword,
				"x-mandant-id": oData.MandantId,
				"x-workplace-id": oData.WorkplaceId,
				"x-user-id": oData.UserId,
				"x-host": oData.Url,
				"x-use-ssl": oData.UseSSL,
		  	};
		},

        _getConnectorType: async function () {
        	console.log("ENTERED");
		  	const mHeaders = this._getHttpHeadersFromRuntimeConfig();

		  	let promise = new Promise((resolve) => {
				fetch("connector/sds/config", { headers: mHeaders })
			  	.then((remoteResponse) => remoteResponse.json())
			  	.then((remoteConfig) => {
					let productCode =
				  	remoteConfig.productInformation.productIdentification
						.productCode;
					let connectorBrand;
					if (productCode == "secu_kon") connectorBrand = "secunet";
					else if (productCode == "RKONN") connectorBrand = "rise";
					else if (productCode == "kocobox") connectorBrand = "kocobox";
					resolve(connectorBrand);
			  	});
		  	});

		  	return await promise;
		},

        onDelete: function () {
          let oView = this.getView();
          let dialog;
          if (!this.byId("deleteConfirmationDialog")) {
            // load asynchronous XML fragment
            Fragment.load({
              id: oView.getId(),
              name: "sap.f.ShellBarWithFlexibleColumnLayout.view.DeleteConfirmationDialog",
              controller: this,
            }).then(function (oDialog) {
              oView.addDependent(oDialog);
              oDialog.open();
            });
          } else {
            this.byId("deleteConfirmationDialog").open();
          }
        },

        onCancelDelete: function () {
          this.byId("deleteConfirmationDialog").close();
        },

        onConfirmDelete: async function () {
          let oTable = this.getView().byId("runtimeConfigTable");
          let aSelectedItems = oTable.getSelectedItems();

          let rowPaths = [];

          for (let item of aSelectedItems) {
            let oContext = item.getBindingContext();
            let oModel = oContext.getModel();
            rowPaths.push(oContext.getPath());
          }

          MessageToast.show(this.translate("msgProcessingDeletionOfConnectorsSelected"));
            await Promise.all(rowPaths.map(async (path) => {
              await new Promise(resolve => setTimeout(resolve, 2000));
              await oTable.getModel().remove(path);
            }));
          this.byId("deleteConfirmationDialog").close();

        },
      }
    );
  }
);
