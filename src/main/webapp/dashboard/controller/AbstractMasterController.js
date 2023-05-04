sap.ui.define(
  ["./AbstractController", "sap/ui/core/Fragment"],
  function (AbstractController, Fragment) {
    "use strict";

    return AbstractController.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.AbstractMasterController",
      {
        onInit: function () {
          this.oRouter = this.getOwnerComponent().getRouter();

          this.oRouter.attachRouteMatched(
            function (oEvent) {
              if (
                oEvent.getParameter("name") ==
                this.getEntityName().toLowerCase() + "-add"
              ) {
                this.onRouteAddMatched(oEvent);
              }
            }.bind(this)
          );

          this.oRouter.attachRouteMatched(
            function (oEvent) {
              if (
                oEvent.getParameter("name") ==
                this.getEntityName().toLowerCase() + "-search"
              ) {
                this.onRouteSearchMatched(oEvent);
              }
            }.bind(this)
          );

          this._bDescendingSort = false;
        },

        onAdd: function () {
          let oView = this.getView();
          const me = this;

          if (!this.byId("createDialog")) {
            // load asynchronous XML fragment
            Fragment.load({
              id: oView.getId(),
              name: "sap.f.ShellBarWithFlexibleColumnLayout.view.CreateDialog",
              controller: this,
            }).then(function (oDialog) {
              me.onAfterCreateOpenDialog({ dialog: oDialog });
              // connect dialog to the root view of this component (models, lifecycle)
              oView.addDependent(oDialog);
              me._openCreateDialog(oDialog);
            });
          } else {
            this._openCreateDialog(this.byId("createDialog"));
          }
        },

        onCancel: function (oEvent) {
          this.getOwnerComponent().getModel().resetChanges();
          oEvent.getSource().getParent().close();
          this.oRouter = this.getOwnerComponent().getRouter();
          this.oRouter.navTo(this.getEntityName().toLowerCase() + "-master");
        },

        onAfterCreateOpenDialog: function () {},

        _openCreateDialog: function (oDialog, sEntityName) {
          oDialog.open();

          if (sEntityName === undefined) {
            sEntityName = this.getEntityName();
            sEntityName = sEntityName[0].toUpperCase() + sEntityName.slice(1);
          }

          const sContextPath = this._createContextPathFromModel(
            "/" + sEntityName
          );
          console.log(sContextPath.sPath);
          oDialog.bindElement(sContextPath.sPath);
        },

        _createContextPathFromModel: function (sEntityName) {
          const oModel = this.getView().getModel();
          const sEntityId = oModel.createEntry("/RuntimeConfigs", {
            properties: {},
          });
          return sEntityId;
        },

        onSave: function () {
          this.getView()
            .getModel()
            .submitChanges({
              success: function () {
                const oTable = this.getView().byId("runtimeConfigTable");
                oTable.getBinding("items").refresh();
              }.bind(this),
              error: function () {
                sap.m.MessageToast.show("Error creating new entry.");
              },
            });
          this.byId("createDialog").close();
        },
      }
    );
  },
  true
);
