sap.ui.define(
    ["sap/ui/model/json/JSONModel", "sap/ui/core/mvc/Controller"],
    function (JSONModel, Controller) {
      "use strict";

      return Controller.extend(
          "sap.f.ShellBarWithFlexibleColumnLayout.controller.FlexibleColumnLayout",
          {
            onInit: function () {
              this.oRouter = this.getOwnerComponent().getRouter();
              this.oRouter.attachRouteMatched(this.onRouteMatched, this);

            },

            onRouteMatched: function (oEvent) {
              let oModel = this.getOwnerComponent().getModel("Layout");

              let sLayout = oEvent.getParameters().arguments.layout;

              // If there is no layout parameter, query for the default level 0 layout (normally OneColumn)
              if (!sLayout) {
                let oNextUIState = this.getOwnerComponent()
                .getHelper()
                .getNextUIState(0);
                sLayout = oNextUIState.layout;
              }

              // Update the layout of the FlexibleColumnLayout
              if (sLayout) {
                oModel.setProperty("/layout", sLayout);
              }
              let sRouteName = oEvent.getParameter("name"),
                  oArguments = oEvent.getParameter("arguments");

              this._updateUIElements();

              // Save the current route name
              this.currentRouteName = sRouteName;
              this.currentProduct = oArguments.product;

              if (sRouteName === "master" || sRouteName
                  == 'automaticmonitoring') {
                this.getView().byId("shellbar").setShowNavButton(true)
              } else {
                this.getView().byId("shellbar").setShowNavButton(false)
              }
            },

            onStateChanged: function (oEvent) {
              let bIsNavigationArrow = oEvent.getParameter("isNavigationArrow"),
                  sLayout = oEvent.getParameter("layout");

              this._updateUIElements();

              // Replace the URL with the new layout if a navigation arrow was used
              if (bIsNavigationArrow) {
                this.oRouter.navTo(
                    this.currentRouteName,
                    {layout: sLayout, product: this.currentProduct},
                    true
                );
              }
            },

            // Update the close/fullscreen buttons visibility
            _updateUIElements: function () {
              let oModel = this.getOwnerComponent().getModel("Layout");
              let oUIState = this.getOwnerComponent()
              .getHelper()
              .getCurrentUIState();
              oModel.setData(oUIState);
            },

            handleBackButtonPressed: function () {
              this.oRouter.navTo("dashboard");
            },

            homeIconPressed: function () {
              this.oRouter.navTo("dashboard");
            },

            onExit: function () {
              this.oRouter.detachRouteMatched(this.onRouteMatched, this);
              this.oRouter.detachBeforeRouteMatched(
                  this.onBeforeRouteMatched,
                  this
              );
            },
          }
      );
    }
);
