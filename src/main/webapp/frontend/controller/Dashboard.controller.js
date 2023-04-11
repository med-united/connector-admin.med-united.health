sap.ui.define(
  [
    "./AbstractMasterController",
  ],
  function (
    AbstractMasterController,
  ) {
    "use strict";

    return AbstractMasterController.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.Dashboard",
      {
        onInit: function () {
          this.oRouter = this.getOwnerComponent().getRouter();
          this._bDescendingSort = false;
        },
        onRouteToMaster: function (oEvent) {

          this.oRouter.navTo("master");
        },
      }
    );
  }
);
