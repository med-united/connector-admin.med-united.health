sap.ui.define(
  [
    "./AbstractMasterController",
    "sap/ui/model/json/JSONModel",
    "sap/ui/integration/widgets/Card"
  ],
  function (AbstractMasterController, JSONModel, Card) {
    "use strict";

    return AbstractMasterController.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.Dashboard",
      {
        onInit: function () {

          var ConnectorList = {
                              	"sap.app": {
                              		"id": "ConnectorList",
                              		"type": "card",
                              		"applicationVersion": {
                              		  "version": "1.0.0"
                              		}
                              	},
                              	"sap.card": {
                              		"type": "List",
                              		"header": {
                              			"title": "Konnektorübersicht"
                              		},
                              		"content": {
                              			"data": { "json":
                              			    [
                              			    ]
                              			},
                              			"item": {
                              				"title": "{Name}",
                              				"description": "{Value}",
                              				"icon": {"src" : "{Icon}"}
                              			}
                              		}
                              	}
                              };

          this.oRouter = this.getOwnerComponent().getRouter();
          this._bDescendingSort = false;
          var oCardContainer = this.getView().byId("CardContainer");

          const runtimeConfigModel = new sap.ui.model.odata.v2.ODataModel(
            "../Data.svc",
            true
          );

           function attachCard(){
             var oCard = new Card();
             oCard.setManifest(ConnectorList);
             var items = oCardContainer.getItems();
             for(let i = 0; i< items.length; i++){
                 if(items[i].sId == "__card0"){
                    oCardContainer.removeItem("__card0");
                 }
             }
             oCardContainer.getLayout();
             oCardContainer.addItem(oCard);
           }

          runtimeConfigModel.read("/RuntimeConfigs", {
              success: function (oData) {
                const configs = oData.results;
                const content = [];
                let anz_Connectors = configs.length;
                let anz_Cards = 0;
                let anz_Terminals = 0;
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
                          fetch("/connector/event/get-cards", {
                            headers: getCardsHeaders,
                          }).then((response) => {
                            if (response.ok) {
                              response.json().then((data) => {
                                let cards = data.cards.card;
                                anz_Cards = anz_Cards + cards.length
                              });
                            }
                          });
                          fetch("connector/event/get-card-terminals", {headers : getCardsHeaders}).then((res) => {
                            numResponses++;
                            if(res.ok){
                                res.json().then((d) => {
                                    let terminals = d.cardTerminals.cardTerminal;
                                    anz_Terminals = anz_Terminals + terminals.length;
                                });
                            }
                            if(numResponses == numConfigs){
                                content.push({
                                    "Name" : "Konnektoren:",
                                    "Value": anz_Connectors,
                                    "Icon": "http://localhost:8080/dashboard/images/Connector.png"
                                });
                                content.push({
                                    "Name" : "Terminals:",
                                    "Value" : anz_Terminals,
                                    "Icon": "http://localhost:8080/dashboard/images/CardTerminal.png"
                                });
                                content.push({
                                    "Name" : "Karten:",
                                    "Value": anz_Cards,
                                    "Icon": "http://localhost:8080/dashboard/images/Card.png"
                                });
                                ConnectorList["sap.card"].content.data.json = content;
                                attachCard();
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
