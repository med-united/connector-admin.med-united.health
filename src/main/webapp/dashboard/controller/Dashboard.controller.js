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
          this.setCardList();
        },

        setCardList: function(){
             const runtimeConfigModel = new sap.ui.model.odata.v2.ODataModel("../Data.svc",true);
             const oConnectorList = this.getView().getModel("Connectors");
             const url = document.URL;
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
                                    "x-client-certificate-password":
                                           config.ClientCertificatePassword,
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
                                 	// Get a JSON object from each of the responses
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
                                 	            if(terminals[i].connected)
                                 	            {
                                 	                activeTerminals++;
                                 	            }
                                 	            else{
                                 	                inactiveTerminals++;
                                 	            }
                                 	        }
                                             let cards = data[1].cards.card;
                                             numCards = numCards + cards.length;
                                             activeConnectors++;
                                 	    }
                                 	    else{
                                 	        inactiveConnectors++;
                                 	    }
                                 	    if(numResponses == numConfigs){
                                 	        let statusConnectors = "";
                                 	        let infoConnectors = "";
                                 	        if(inactiveConnectors > 0){
                                 	            statusConnectors = "Error";
                                 	            infoConnectors= "Offline: " + inactiveConnectors;
                                 	        }
                                 	        else{
                                 	            statusConnectors = "Success";
                                 	            infoConnectors = "Alles Online";
                                 	        }
                                 	        let statusTerminals = "";
                                 	        let infoTerminals = "";
                                 	        if(inactiveTerminals > 0){
                                 	            statusTerminals = "Error";
                                 	            infoTerminals = "Offline: " + inactiveTerminals;
                                 	        }
                                 	        else{
                                 	            statusTerminals = "Success";
                                 	            infoTerminals = "Alles Online"
                                 	        }
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
                                             cardData.connectors["sap.card"].content.data.json = content;
                                             oConnectorList.setData(cardData);
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
