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
                 let invalidHBA = 0;
                 let invalidSMCB = 0;
                 let invalidEGK = 0;
                 const date = new Date().toJSON();
                 const cardTypes = {"SMC_KT" : 0, "SMC_B" : 0, "HBA": 0, "EGK": 0};
                 const invalidCertCards = {"SMC_KT" : 0, "SMC_B" : 0, "HBA": 0, "EGK": 0};
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
                          const cards = data[1].cards.card;
                          numCards = numCards + cards.length;
                          for(let k = 0; k < cards.length; k++){
                            const cardType = cards[k].cardType;
                            cardTypes[cardType] = cardTypes[cardType] + 1;
                            invalidCertCards[cardType] = cards[k].certificateExpirationDate < date ? invalidCertCards[cardType] + 1 : invalidCertCards[cardType];
                          }
                          activeConnectors++;
                          const pinStatus = data[3];
                          for(let j = 0; j < pinStatus.length; j++)
                          {
                            if(pinStatus[j].cardType == "SMC_B" && pinStatus[j].status!="VERIFIED"){
                                invalidSMCB++;
                            }
                            else if(pinStatus[j].cardType == "HBA" && pinStatus[j].status!="VERIFIED"){
                                invalidHBA++;
                            }
                            else if(pinStatus[j].cardType =="EGK" && pinStatus[j].status!="VERIFIED"){
                                invalidEGK++;
                            }
                          }
                        }
                        else{
                           inactiveConnectors++;
                        }
                        if(numResponses == numConfigs){
                           const connectorData = data[2];
                           const cardData = data[4];
                           let connectorContent = [];
                           let cardContent = [];
                           connectorContent.push({
                             "Name" : "Konnektoren",
                             "Value": configs.length,
                             "Icon": url + "dashboard/images/Connector.png",
                             "State" : inactiveConnectors > 0 ? "Error" : "Success",
                             "Info": inactiveConnectors > 0 ? "Offline: " + inactiveConnectors : "Alle Online"
                           });
                           connectorContent.push({
                             "Name" : "Kartenterminals",
                             "Value" : numTerminals,
                             "Icon": url + "dashboard/images/CardTerminal.png",
                             "State": inactiveTerminals > 0 ? "Error" : "Success",
                             "Info" : inactiveTerminals > 0 ? "Offline: " + inactiveTerminals : "Alle Online"
                           });
                           connectorContent.push({
                             "Name" : "Karten",
                             "Value": numCards,
                             "Icon": url + "dashboard/images/Card.png",
                             "State": "None"
                           });
                           cardContent.push({
                             "Name": "eGK",
                             "Value": cardTypes["EGK"],
                             "PinInfo" : invalidEGK > 0 ? "Nicht verifiziert: " + invalidEGK : "Alle verifiziert",
                             "PinStatus": invalidEGK > 0 ? "Error" : "Success",
                             "CertInfo" : invalidCertCards["EGK"] > 0 ? "Abgelaufen: " + invalidCertCards["EGK"] : "Alle gültig",
                             "CertStatus": invalidCertCards["EGK"] > 0 ? "Error" : "Success"
                           });
                           cardContent.push({
                             "Name": "HBA",
                             "Value": cardTypes["HBA"],
                             "PinInfo": invalidHBA > 0 ? "Nicht verifiziert: " + invalidHBA : "Alle verifiziert",
                             "PinStatus": invalidHBA > 0 ? "Error" : "Success",
                             "CertInfo" : invalidCertCards["HBA"] > 0 ? "Abgelaufen: " + invalidCertCards["HBA"] : "Alle gültig",
                             "CertStatus": invalidCertCards["HBA"] > 0 ? "Error" : "Success"
                           });
                           cardContent.push({
                             "Name": "SMC-B",
                             "Value": cardTypes["SMC_B"],
                             "PinInfo": invalidSMCB > 0 ? "Nicht verifiziert: " + invalidSMCB : "Alle verifiziert",
                             "PinStatus": invalidSMCB > 0 ? "Error" : "Success",
                             "CertInfo" : invalidCertCards["SMC_B"] > 0 ? "Abgelaufen: " + invalidCertCards["SMC_B"] : "Alle gültig",
                             "CertStatus": invalidCertCards["SMC_B"] > 0 ? "Error" : "Success"
                           });
                           cardContent.push({
                             "Name" : "SMC-KT",
                             "Value": cardTypes["SMC_KT"],
                             "CertInfo" : invalidCertCards["SMC_KT"] > 0 ? "Abgelaufen: " + invalidCertCards["SMC_KT"] : "Alle gültig",
                             "CertStatus": invalidCertCards["SMC_KT"] > 0 ? "Error" : "Success"
                           });
                           connectorData.connectors["sap.card"].content.data.json = connectorContent;
                           oConnectorList.setData(connectorData);
                           cardData.cards["sap.card"].content.data.json = cardContent;
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
        onBeforeRendering: function () {
         this.onInit();
        },

        onRouteToMaster: function (oEvent) {
          this.oRouter.navTo("master");
        },
      }
    );
  }
);
