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
          const oCardCard = new JSONModel();
          this.getView().setModel(oCardCard, "CardsCard")
          this.setCardList();
        },

        setCardList: function(){
             const runtimeConfigModel = new sap.ui.model.odata.v2.ODataModel("../Data.svc",true);
             const oConnectorList = this.getView().byId("ConnectorListCard");
             const oCardList = this.getView().getModel("CardsCard");
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
                 const date = new Date().toJSON();
                 const cardTypes = {"SMC_KT" : 0, "SMC_B" : 0, "HBA": 0, "EGK": 0};
                 const invalidCertCards = {"SMC_KT" : 0, "SMC_B" : 0, "HBA": 0, "EGK": 0};
                 const verifiedPinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0};
                 const verifiablePinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0}
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
                     fetch("connector/card/pinStatus", {headers : getCardsHeaders})
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
                          for(let j = 0; j < pinStatus.length; j++){
                            if(pinStatus[j].cardType == "SMC_B"){
                               verifiedPinStatus["SMC_B"] = pinStatus[j].status == "VERIFIED" ?  verifiedPinStatus["SMC_B"] + 1 : verifiedPinStatus["SMC_B"];
                               verifiablePinStatus["SMC_B"] = pinStatus[j].status == "VERIFIABLE" ?  verifiablePinStatus["SMC_B"] + 1 : verifiablePinStatus["SMC_B"];
                            }
                            else if(pinStatus[j].cardType == "HBA" && pinStatus[j].pinType == "PIN.CH")
                            {
                                verifiedPinStatus["HBA_CH"] = pinStatus[j].status == "VERIFIED" ?  verifiedPinStatus["HBA_CH"] + 1 : verifiedPinStatus["HBA_CH"];
                                verifiablePinStatus["HBA_CH"] = pinStatus[j].status == "VERIFIABLE" ?  verifiablePinStatus["HBA_CH"] + 1 : verifiablePinStatus["HBA_CH"];
                            }
                            else if(pinStatus[j].cardType == "HBA" && pinStatus[j].pinType == "PIN.QES")
                            {
                                verifiedPinStatus["HBA_QES"] = pinStatus[j].status == "VERIFIED" ?  verifiedPinStatus["HBA_QES"] + 1 : verifiedPinStatus["HBA_QES"];
                                verifiablePinStatus["HBA_QES"] = pinStatus[j].status == "VERIFIABLE" ?  verifiablePinStatus["HBA_QES"] + 1 : verifiablePinStatus["HBA_QES"];
                            }
                            else if(pinStatus[j].cardType == "EGK"){
                                verifiedPinStatus["EGK"] = pinStatus[j].status == "VERIFIED" ?  verifiedPinStatus["EGK"] + 1 : verifiedPinStatus["EGK"];
                                verifiablePinStatus["EGK"] = pinStatus[j].status == "VERIFIABLE" ?  verifiablePinStatus["EGK"] + 1 : verifiablePinStatus["EGK"];
                            }
                          }
                        }
                        else{
                           inactiveConnectors++;
                        }
                        if(numResponses == numConfigs){
                           const connectorData = data[2];
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
                             "cardType": "EGK",
                             "Value": cardTypes["EGK"],
                             "pinType": "PIN.CH",
                             "pinInfo" : cardTypes["EGK"] > 0 ? "Verifiziert: " + verifiedPinStatus["EGK"] + " Verifizierbar: " + verifiablePinStatus["EGK"] : "-",
                             "validity" : cardTypes["EGK"] > 0 ? invalidCertCards["EGK"] > 0 ? "Abgelaufen: " + invalidCertCards["EGK"] : "Alle gültig" : "-",
                             "validityState" : invalidCertCards["EGK"] > 0 ? "Error" : "Success"
                           });
                           cardContent.push({
                             "cardType": "HBA",
                             "Value": cardTypes["HBA"],
                             "pinType": "PIN.CH",
                             "pinInfo": cardTypes["HBA"] > 0 ? "Verifiziert: " + verifiedPinStatus["HBA_CH"] + " Verifizierbar: " + verifiablePinStatus["HBA_CH"] : "-",
                             "validity" : cardTypes["HBA"] > 0 ? invalidCertCards["HBA"] > 0 ? "Abgelaufen: " + invalidCertCards["HBA"] : "Alle gültig" : "-",
                             "validityState" : invalidCertCards["HBA"] > 0 ? "Error" : "Success"
                           });
                           cardContent.push({
                             "cardType": "HBA",
                             "Value": cardTypes["HBA"],
                             "pinType": "PIN.QES",
                             "pinInfo": cardTypes["HBA"] > 0 ? "Verifiziert: " + verifiedPinStatus["HBA_QES"] + " Verifizierbar: " + verifiablePinStatus["HBA_QES"] : "-",
                             "validity" : cardTypes["HBA"] > 0 ? invalidCertCards["HBA"] > 0 ? "Abgelaufen: " + invalidCertCards["HBA"] : "Alle gültig" : "-",
                             "validityState" : invalidCertCards["HBA"] > 0 ? "Error" : "Success"
                           });
                           cardContent.push({
                             "cardType": "SMC-B",
                             "Value": cardTypes["SMC_B"],
                             "pinType": "PIN.SMC",
                             "pinInfo": cardTypes["SMC_B"] > 0 ? "Verifiziert: " + verifiedPinStatus["SMC_B"] + " Verifizierbar: " + verifiablePinStatus["SMC_B"] : "-",
                             "validity" : cardTypes["SMC_B"] > 0 ? invalidCertCards["SMC_B"] > 0 ? "Abgelaufen: " + invalidCertCards["SMC_B"] : "Alle gültig" : "-",
                             "validityState" : invalidCertCards["SMC_B"] > 0 ? "Error" : "Success"
                           });
                           cardContent.push({
                             "cardType" : "SMC-KT",
                             "Value": cardTypes["SMC_KT"],
                             "pinType": "-",
                             "pinInfo": "-",
                             "validity" : cardTypes["SMC_KT"] > 0 ? invalidCertCards["SMC_KT"] > 0 ? "Abgelaufen: " + invalidCertCards["SMC_KT"] : "Alle gültig" : "-",
                             "validityState" : invalidCertCards["SMC_KT"] > 0 ? "Error" : "Success"
                           });
                           connectorData["sap.card"].content.data.json = connectorContent;
                           oConnectorList.setManifest(connectorData);
                           oCardList.setData(cardContent);
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
          const oConnectorList = this.getView().byId("ConnectorListCard");
          oConnectorList.setManifest("./dashboard/resources/ConnectorList.json");
          this.onInit();
        },

        onRouteToMaster: function (oEvent) {
          this.oRouter.navTo("master");
        },
      }
    );
  }
);
