sap.ui.define(
  [
    "./AbstractMasterController",
    "sap/ui/model/json/JSONModel"
  ],
  function (AbstractMasterController, JSONModel) {
    "use strict";

    return AbstractMasterController.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.Dashboard",
      {
        onInit: function () {

          this.oRouter = this.getOwnerComponent().getRouter();
          this._bDescendingSort = false;
          this.setCardList();
        },

        setCardList: function(){
             const runtimeConfigModel = new sap.ui.model.odata.v2.ODataModel("../Data.svc",true);
             const Card = this.getView().byId("ConnectorListCard");
             let url = document.URL;
             url = url.endsWith("#") ? url.substring(0, url.length-1) : url.substring(0, url.length);
             runtimeConfigModel.read("/RuntimeConfigs", {
               success: function (oData) {
                 const configs = oData.results;
                 let numCards = 0;
                 let numTerminals = 0;
                 let inactiveConnectors= 0;
                 let inactiveTerminals = 0;
                 let activeConnectors= 0;
                 let activeTerminals = 0;
                 const numConfigs = configs.length;
                 let numResponses = 0;
                 configs.forEach(function (config) {
                   let getCardsHeaders = {
                     "x-client-system-id": config.ClientSystemId,
                     "x-client-certificate": config.ClientCertificate,
                     "x-client-certificate-password": config.ClientCertificatePassword,
                     "x-mandant-id": config.MandantId,
                     "x-workplace-id": config.WorkplaceId,
                     "x-host": config.Url,
                     Accept: "application/json",
                   };
                   Promise.all([
                     fetch("connector/event/get-card-terminals", {headers : getCardsHeaders}),
                     fetch("connector/event/get-cards", {headers : getCardsHeaders}),
                     fetch("dashboard/resources/ConnectorList.json")
                   ]).then(function (responses) {
                     return Promise.all(responses.map(function (response) {
                       if(response.ok){
                          return response.json();
                       }
                     }));
                     }).then(function (data) {
                        numResponses++;
                        if(data[0]!=null){
                          let terminals = data[0].cardTerminals.cardTerminal;
                          numTerminals = numTerminals + terminals.length;
                          for(let i = 0; i< terminals.length; i++){
                             terminals[i].connected ? activeTerminals++ : inactiveTerminals++;
                          }
                          let cards = data[1].cards.card;
                          numCards = numCards + cards.length;
                          activeConnectors++;
                        }
                        else{
                           inactiveConnectors++;
                        }
                        if(numResponses == numConfigs){
                           const statusConnectors = inactiveConnectors > 0 ? "Error" : "Success";
                           const statusTerminals = inactiveTerminals > 0 ? "Error" : "Success";
                           const infoConnectors = inactiveConnectors > 0 ? "Offline: " + inactiveConnectors : "Alles Online";
                           const infoTerminals = inactiveTerminals > 0 ? "Offline: " + inactiveTerminals : "Alles Online";
                           const cardData = data[2];
                           let content = [];
                           content.push({
                             "Name" : "Konnektoren",
                             "Value": configs.length,
                             "Icon": url + "dashboard/images/Connector.png",
                             "State" : statusConnectors,
                             "Info": infoConnectors
                           });
                           content.push({
                             "Name" : "Kartenterminals",
                             "Value" : numTerminals,
                             "Icon": url + "dashboard/images/CardTerminal.png",
                             "State": statusTerminals,
                             "Info" : infoTerminals
                           });
                           content.push({
                             "Name" : "Karten",
                             "Value": numCards,
                             "Icon": url + "dashboard/images/Card.png",
                             "State": "None"
                           });
                           cardData["sap.card"].content.data.json = content;
                           console.log(cardData);
                           Card.setManifest(cardData);
                        }
                     });
                   });
                 },
                 error: function (error) {
                    console.log(error);
                 },
               });
        },
        onBeforeRendering: function () {
          const oCard = this.getView().byId("ConnectorListCard");
          oCard.setManifest("./dashboard/resources/ConnectorList.json");
          this.onInit();
        },

        onRouteToMaster: function (oEvent) {
          this.oRouter.navTo("master");
        },
      }
    );
  }
);
