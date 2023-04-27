sap.ui.define(
  [
    "./AbstractMasterController",
    "sap/ui/model/Filter",
    "sap/ui/model/FilterOperator",
    "sap/ui/model/Sorter",
    "sap/ui/core/Fragment",
  ],
  function (
    AbstractMasterController,
    Filter,
    FilterOperator,
    Sorter,
    Fragment
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

/*
        remove: function (oEvent) {
                  console.log("remove function");
          let oTable = this.getView().byId("runtimeConfigTable");
          oTable.removeItem(oEvent.getSource().getParent());
        },
*/

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

        onConfirmDelete: function () {
          let oTable = this.getView().byId("runtimeConfigTable");
          let aSelectedItems = oTable.getSelectedItems();

          let counter = 0;
          let rowPath = [];

          for (let item of aSelectedItems) {
            let oContext = item.getBindingContext();
            let oModel = oContext.getModel();
            rowPath.push(oContext.getPath());

            setTimeout(
            function() {oModel.remove(rowPath[counter++],{
                success : function(){
                    console.log("Konnektor erfolgreich geloescht");
                }
            })}, 2000);

            sap.m.MessageToast.show("Die ausgewaehlten Konnektoren werden alsbald geloescht");

          }
          this.byId("deleteConfirmationDialog").close();
        },
      }
    );
  }
);
