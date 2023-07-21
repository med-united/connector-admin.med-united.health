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

        onFileUploaderChange: function(event, dialogId) {
		  var file = event.getParameter("files")[0];
		  console.log("event", event);
		  var reader = new FileReader();

		  reader.onload = (e) => {
			var fileContentArrayBuffer = e.target.result;
			var fileContentBytes = new Uint8Array(fileContentArrayBuffer);

			// Convert Uint8Array to binary string
			var binaryString = this.bytesToBinaryString(fileContentBytes);

			// Convert binary string to Base64 using JavaScript's btoa function
			var base64Data = btoa(binaryString);

			// Get the dialog by its ID and update the ClientCertificate property
			var dialog = this.byId(dialogId);

			dialog.getModel().setProperty(dialog.getBindingContext().sPath + "/ClientCertificate", base64Data);

			// Use the base64Data as needed (e.g., store it, send it to the server, etc.)
			console.log("Base64 data:", base64Data);
			console.log("Client Certificate:", dialog.getModel().getProperty(dialog.getBindingContext().sPath + "/ClientCertificate"));
			console.log("Runtime item", dialog.getModel().getProperty(dialog.getBindingContext().sPath));
		  };

		  reader.readAsArrayBuffer(file);
		},

		bytesToBinaryString: function(bytes) {
		  var binaryString = '';
		  var len = bytes.length;
		  for (var i = 0; i < len; i++) {
			binaryString += String.fromCharCode(bytes[i]);
		  }
		  return binaryString;
		},

      }
    );
  },
  true
);
