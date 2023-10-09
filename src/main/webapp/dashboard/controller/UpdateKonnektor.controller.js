sap.ui.define(
  [
    "sap/ui/core/mvc/Controller",
    "sap/ui/core/routing/History"
  ],
  function (
    Controller,
    History
  ) {
    "use strict";

    return Controller.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.UpdateKonnektor",
      {
        onInit: function () {
          
        },
        onBack: function(oEvent) {
          const oRouter = this.getOwnerComponent().getRouter();
          const oHistory = History.getInstance();
          const sPreviousHash = oHistory.getPreviousHash();

          if (sPreviousHash !== undefined) {
            window.history.go(-1);
          } else {
            oRouter.navTo("automaticmonitoring");
          }
        }
      }
    );

  }
);
