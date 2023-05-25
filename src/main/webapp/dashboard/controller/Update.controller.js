sap.ui.define(
  [
    "./AbstractController",
    "sap/ui/model/json/JSONModel"
  ],
  function (
    AbstractController,
    JSONModel
  ) {
    "use strict";

    return AbstractController.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.Update",
      {
        onInit: function () {
          this.oRouter = this.getOwnerComponent().getRouter();
          this._bDescendingSort = false;
          const oCardTerminals = new JSONModel();
          this.getView().setModel(oCardTerminals, "CardTerminals");
          this.setContent();
        },

        setContent: function(){
            const self = this;
            const runtimeConfigModel = new sap.ui.model.odata.v2.ODataModel("../Data.svc",true);
            const oCardTerminals = this.getView().getModel("CardTerminals");
            runtimeConfigModel.read("/RuntimeConfigs", {
               success: function (oData) {
                   const configs = oData.results;
                   const numConfigs = configs.length;
                   let numResponses = 0;
                   let cardTerminalContent = [];
                   configs.forEach(function (config) {
                      let getCardsHeaders = {
                         "x-user-id": config.UserId,
                         "x-client-system-id": config.ClientSystemId,
                         "x-client-certificate": config.ClientCertificate,
                         "x-client-certificate-password": config.ClientCertificatePassword,
                         "x-mandant-id": config.MandantId,
                         "x-workplace-id": config.WorkplaceId,
                         "x-host": config.Url,
                         "x-use-ssl": config.UseSSL,
                         Accept: "application/json",
                      };
                      Promise.all([
                          fetch("connector/event/get-card-terminals", {headers : getCardsHeaders}),
                          fetch("connector/sds/config", { headers: getCardsHeaders })
                      ]).then(function (responses) {
                         return Promise.all(responses.map(function (response) {
                           if(response.ok){
                             return response.json();
                           }
                         }));
                      }).then(function (data) {
                          numResponses++;
                          if(data[0]!=null){
                             const terminals = data[0].cardTerminals.cardTerminal;
                             let productCode = data[1].productInformation.productIdentification.productCode;
                             let connectorBrand;
                             if (productCode == "secu_kon") connectorBrand = "secunet";
                             else if (productCode == "RKONN") connectorBrand = "rise";
                             else if (productCode == "kocobox") connectorBrand = "kocobox";
                             let requestBody = {username: "", password: ""};
                             let updateUrl = "connector/management/" + connectorBrand + "/availableUpdate?connectorUrl=" + config.Url;
                             const updateHeaders = {
                                 "x-client-certificate": config.ClientCertificate,
                                 "x-client-certificate-password":config.ClientCertificatePassword,
                                 "Content-Type": "application/json",
                             };
                             for(let i = 0; i< terminals.length; i++){
                                if (connectorBrand =="secunet")
                                {
                                   requestBody = {
                                      username: "super",
                                      password: "konnektor3$",
                                      productCode: terminals[i].productInformation.productIdentification.productCode,
                                      productVendorID: terminals[i].productInformation.productIdentification.productVendorID,
                                      HWVersion: terminals[i].productInformation.productIdentification.productVersion.local.HWVersion,
                                      FWVersion: terminals[i].productInformation.productIdentification.productVersion.local.FWVersion
                                   }
                                   fetch(updateUrl, {
                                      headers: updateHeaders,
                                      method: "POST",
                                      body: JSON.stringify(requestBody),
                                      }).then((response) => response.json()).then((data) => {
                                        if(data.cardTerminalUpdateInformation[0].upgradeVersion){
                                            cardTerminalContent.push({
                                                "Connector": config.UserId,
                                                "URL": config.Url,
                                                "IP": terminals[i].IPAddress.IPV4Address,
                                                "Name": terminals[i].name,
                                                "Product": terminals[i].productInformation.productIdentification.productCode + " " + terminals[i].productInformation.productIdentification.productVendorID,
                                                "HWVersion": terminals[i].productInformation.productIdentification.productVersion.local.HWVersion,
                                                "FWVersion": terminals[i].productInformation.productIdentification.productVersion.local.FWVersion,
                                                "Update": data.cardTerminalUpdateInformation[0].fwVersion
                                            });
                                        }
                                   });
                                }
                             }
                          }
                          if(numResponses == numConfigs){
                             oCardTerminals.setData(cardTerminalContent);
                          }
                          });
                        });
                   }
               });
            }
        });
      }
    );