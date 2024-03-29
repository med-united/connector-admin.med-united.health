sap.ui.define(
    [
      "./AbstractMasterController",
      "sap/ui/model/Filter",
      "sap/ui/model/FilterOperator",
      "sap/ui/model/Sorter",
      "sap/ui/core/Fragment",
      "sap/m/MessageToast",
      "sap/m/MessageBox",
    ],
    function (
        AbstractMasterController,
        Filter,
        FilterOperator,
        Sorter,
        Fragment,
        MessageToast,
        MessageBox,
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
                  oParams = {layout: oNextUIState.layout};
              oParams["id"] = oEvent
              .getSource()
              .getBindingContext()
              .getProperty("Id");

              this.oRouter.navTo("detail", oParams);
            },
            onSearch: function (oEvent) {
              const sQuery = oEvent.getParameter("query");
              const aFilters = [];

              // Validate sQuery
              if (!sQuery || sQuery.length === 0) {
                // Clear filters and refresh table
                this.getView().byId("runtimeConfigTable").getBinding(
                    "items").filter([]);
                return;
              }
              // Create filters and push only if the query exists
              aFilters.push(
                  new Filter("UserId", FilterOperator.Contains, sQuery));
              aFilters.push(new Filter("Url", FilterOperator.Contains, sQuery));
              aFilters.push(
                  new Filter("MandantId", FilterOperator.Contains, sQuery));
              aFilters.push(
                  new Filter("ClientSystemId", FilterOperator.Contains,
                      sQuery));
              aFilters.push(
                  new Filter("WorkplaceId", FilterOperator.Contains, sQuery));

              const oAllFilter = new Filter({
                filters: aFilters,
                and: false, // Use 'or' operator between filters
              });

              this.getView().byId("runtimeConfigTable").getBinding(
                  "items").filter(oAllFilter);
            },

            getEntityName: function () {
              return "RuntimeConfigs";
            },

            onSort: function (oEvent) {
              this._bDescendingSort = !this._bDescendingSort;

              const oView = this.getView();
              const oTable = oView.byId("runtimeConfigTable");
              const oBinding = oTable.getBinding("items");

              // Define an array of sorter objects for multi-level sorting
              const sorters = [];

              // Define different sorters and push into the array
              sorters.push(new Sorter("UserId", this._bDescendingSort));
              sorters.push(new Sorter("Url", this._bDescendingSort));
              sorters.push(new Sorter("MandantId", this._bDescendingSort));
              sorters.push(new Sorter("ClientSystemId", this._bDescendingSort));
              sorters.push(new Sorter("WorkplaceId", this._bDescendingSort));

              oBinding.sort(sorters);
            },

            onEdit: function () {
              const oView = this.getView();
              const aSelectedItems = oView.byId(
                  "runtimeConfigTable").getSelectedItems();
              if (aSelectedItems.length === 1) {
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
              } else {
                if (aSelectedItems.length === 0) {
                  MessageBox.error(this.translate("msgSelectOneConnector"));
                } else if (aSelectedItems.length > 1) {
                  MessageBox.error(this.translate("msgSelectOnlyOneConnector"));
                }
              }
            },

            _openEditDialog: function (oDialog) {
              oDialog.open();
              const aSelectedItems = this.getView().byId(
                  "runtimeConfigTable").getSelectedItems();
              const oItemContextPath = aSelectedItems[0].getBindingContext().getPath();
              oDialog.bindElement(oItemContextPath);
            },

            onCancelEdit: function () {
              this.byId("editDialog").close();
            },

            onSaveEdit: function () {
              this.getView()
              .getModel()
              .submitChanges({
                success: function () {
                  const oTable = this.getView().byId("runtimeConfigTable");
                  oTable.getBinding("items").refresh();
                  sap.m.MessageToast.show(this.translate("msgEditSuccess"));
                }.bind(this),
                error: function () {
                  sap.m.MessageToast.show(this.translate("msgEditError"));
                },
              });
              this.byId("editDialog").close();
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

              MessageToast.show(
                  this.translate("msgProcessingDeletionOfConnectorsSelected"));
              await Promise.all(rowPaths.map(async (path) => {
                await new Promise(resolve => setTimeout(resolve, 2000));
                await oTable.getModel().remove(path);
              }));
              this.byId("deleteConfirmationDialog").close();

            },

            onCreateDialogFileUploaderChange: function (event) {
              this.onFileUploaderChange(event, "createDialog");
            },

            onEditDialogFileUploaderChange: function (event) {
              this.onFileUploaderChange(event, "editDialog");
            },

            onCreateDialogRadioButtonSelected: function (event) {
              this.onRadioButtonSelected(event, "createDialog");
            },

            onEditDialogRadioButtonSelected: function (event) {
              this.onRadioButtonSelected(event, "editDialog");
            },

          }
      );
    }
);
