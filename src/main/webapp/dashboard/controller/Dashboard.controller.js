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
          const oConnectorList = new JSONModel();
          this.getView().setModel(oConnectorList, "Connectors");
          const oCardList = new JSONModel();
          this.getView().setModel(oCardList, "Cards");
          this.setCardList();
        },

        setCardList: function(){
             const runtimeConfigModel = new sap.ui.model.odata.v2.ODataModel("../Data.svc",true);
             const oConnectorList = this.getView().getModel("Connectors");
             const oCardList = this.getView().getModel("Cards");
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
                 let invalidSMCB = 0;
                 let invalidHBA = 0;
                 const cardTypes = {"SMC_KT" : 0, "SMC_B" : 0, "HBA": 0, "EGK": 0, "KVK": 0}
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
                     fetch("dashboard/resources/ConnectorList.json"),
                     fetch("connector/card/pinStatus", {headers : getCardsHeaders}),
                     fetch("dashboard/resources/CardsCard.json")
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
                          const pinStatus = data[3]
                          for(let j = 0; j < pinStatus.length; j++)
                          {
                            const cardType = pinStatus[j].cardType;
                            cardTypes[cardType] = cardTypes[cardType] + 1;
                            const status = pinStatus[j].status;
                            if(cardType == "SMC_B" && status!="VERIFIED"){
                                invalidSMCB++;
                            }
                            if(cardType == "HBA" && status!="VERIFIED"){
                                invalidHBA++;
                            }
                          }
                        }
                        else{
                           inactiveConnectors++;
                        }
                        if(numResponses == numConfigs){
                           const statusConnectors = inactiveConnectors > 0 ? "Error" : "Success";
                           const statusTerminals = inactiveTerminals > 0 ? "Error" : "Success";
                           const infoConnectors = inactiveConnectors > 0 ? "Offline: " + inactiveConnectors : "Alles Online";
                           const infoTerminals = inactiveTerminals > 0 ? "Offline: " + inactiveTerminals : "Alles Online";
                           const connectorData = data[2];
                           const cardData = data[4];
                           const statusSMCB = invalidSMCB > 0 ? "Error" : "Success";
                           const statusHBA = invalidHBA > 0 ? "Error" : "Success";
                           const infoSMCB = invalidSMCB > 0 ? "Pin-Status: " + invalidSMCB + " nicht verifiziert" : "Pin-Status: Alles verifiziert";
                           const infoHBA = invalidHBA > 0 ? "Pin-Status: " + invalidHBA + " nicht verifiziert" : "Pin-Status: Alles verifiziert";
                           let connectorContent = [];
                           let cardContent = [];
                           connectorContent.push({
                             "Name" : "Konnektoren",
                             "Value": configs.length,
                             "Icon": url + "dashboard/images/Connector.png",
                             "State" : statusConnectors,
                             "Info": infoConnectors
                           });
                           connectorContent.push({
                             "Name" : "Kartenterminals",
                             "Value" : numTerminals,
                             "Icon": url + "dashboard/images/CardTerminal.png",
                             "State": statusTerminals,
                             "Info" : infoTerminals
                           });
                           connectorContent.push({
                             "Name" : "Karten",
                             "Value": numCards,
                             "Icon": url + "dashboard/images/Card.png",
                             "State": "None"
                           });
                           cardContent.push({
                             "Name" : "SMC-KT",
                             "Value": cardTypes["SMC_KT"],
                             "Info": "Pin-Status: Alle unbekannt   " + "Abgelaufen: 0",
                             "State": "Success"
                           });
                           cardContent.push({
                             "Name": "SMC-B",
                             "Value": cardTypes["SMC_B"],
                             "Info": infoSMCB + "   Abgelaufen: 0",
                             "State": statusSMCB
                           });
                           cardContent.push({
                             "Name": "HBA",
                             "Value": cardTypes["HBA"],
                             "Info": infoHBA + "    Abgelaufen: 0",
                             "State": statusHBA
                           });
                           cardContent.push({
                             "Name": "EGK",
                             "Value": cardTypes["EGK"],
                             "Info": "Pin-Status: Alle unbekannt   " + "Abgelaufen: 0",
                             "State": "Success"
                           });
                           cardContent.push({
                             "Name": "KVK",
                             "Value": cardTypes["KVK"],
                             "Info": "Pin-Status: Alle unbekannt   " + "Abgelaufen: 0",
                             "State": "Success"
                           });
                           console.log(cardContent);
                           connectorData.connectors["sap.card"].content.data.json = connectorContent;
                           oConnectorList.setData(connectorData);
                           cardData.cards["sap.card"].content.data.json = cardContent;
                           console.log(cardData);
                           oCardList.setData(cardData);
                        }
                     });
                   });
                 },
                 error: function (error) {
                    console.log(error);
                 },
               });
        },

        onRouteToMaster: function (oEvent) {
          this.oRouter.navTo("master");
        },
      }
    );
  }
);
