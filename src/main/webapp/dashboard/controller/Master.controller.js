sap.ui.define(
  [
    "./AbstractMasterController",
    "sap/ui/model/Filter",
    "sap/ui/model/FilterOperator",
    "sap/ui/model/Sorter",
    "sap/ui/core/Fragment",
    "sap/m/MessageToast",
    "sap/m/MessageBox",
    "../lib/forge-main/dist/forge",
  ],
  function (
    AbstractMasterController,
    Filter,
    FilterOperator,
    Sorter,
    Fragment,
    MessageToast,
    MessageBox,
    forge,
  ) {
    "use strict";

    console.log("forge", forge);
    // Check if 'forge' is now defined
	  if (forge === undefined) {
		// If 'forge' is still undefined, it means it's not loaded correctly
		console.error("The 'forge' object is undefined. Check the script loading and path.");
	  }

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

        onSaveEdit:function(){
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

          MessageToast.show(this.translate("msgProcessingDeletionOfConnectorsSelected"));
            await Promise.all(rowPaths.map(async (path) => {
              await new Promise(resolve => setTimeout(resolve, 2000));
              await oTable.getModel().remove(path);
            }));
          this.byId("deleteConfirmationDialog").close();

        },

        onFileUploaderChange: function(event) {
          var file = event.getParameter("files")[0];
          var reader = new FileReader();
          console.log("forge", forge);

          reader.onload = function(e) {
            var fileContentArrayBuffer = e.target.result;
            var fileContentBytes = new Uint8Array(fileContentArrayBuffer);

            try {
              // Use Forge to parse PKCS#12 file
              var p12Asn1 = forge.asn1.fromDer(fileContentBytes);
              var pkcs12 = forge.pkcs12.pkcs12FromAsn1(p12Asn1);

              // Get bags containing the private key and certificate
              var bags = pkcs12.getBags({ bagType: forge.pki.oids.pkcs8ShroudedKeyBag });
              var privateKeyBag = bags[forge.pki.oids.pkcs8ShroudedKeyBag][0];
              var certBags = pkcs12.getBags({ bagType: forge.pki.oids.certBag });
              var certBag = certBags[forge.pki.oids.certBag][0];

              if (privateKeyBag && certBag) {
                var privateKeyPEM = forge.pki.privateKeyToPem(privateKeyBag.key);
                var certificatePEM = forge.pki.certificateToPem(certBag.cert);

                // Use the private key and certificate as needed
                var clientCertPasswordInput = sap.ui.getCore().byId("clientCertPasswordInput");
                clientCertPasswordInput.setValue(privateKeyPEM + '\n' + certificatePEM);
              } else {
                console.error("Private key or certificate not found in the PKCS#12 file.");
              }
            } catch (error) {
              console.error("An error occurred while loading the PKCS#12 file:", error);
            }
          };

          reader.readAsArrayBuffer(file);
        },







      }
    );
  }
);
