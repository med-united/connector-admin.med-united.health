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
                              			"title": "Konnektorübersicht",
                              			"icon": {
                                        			"src": "sap-icon://overview-chart"
                                        		},
                                        "actions": [
                                        				{
                                        					"type": "Navigation",
                                        					"url": "./#/management"
                                        				}
                                        			]
                              		},
                              		"content": {
                              			"data": { "json":
                              			    [
                              			    ]
                              			},
                              			"item": {
                              				"title": "{Name}",
                              				"description": "{Value}",
                              				"icon": {"src" : "{Icon}"},
                              				"highlight": "{State}",
                              				"info": {
                                            			"value": "{Info}",
                                            			"state": "{State}"
                                            		}
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
                const urls = [
                   'connector/event/get-card-terminals',
                   "connector/event/get-cards"
                ];
                const configs = oData.results;
                const content = [];
                let anz_Connectors = configs.length;
                let anz_Cards = 0;
                let anz_Terminals = 0;
                let activeConnectors= 0;
                let activeTerminals = 0;
                let defined = false;
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
                    	fetch("connector/event/get-cards", {headers : getCardsHeaders})
                        ]).then(function (responses) {
                    	// Get a JSON object from each of the responses
                    	return Promise.all(responses.map(function (response) {
                    	    if(response.ok){
                    	        defined = true;
                    	        return response.json();
                    	    }

                    	}));
                        }).then(function (data) {
                            numResponses++;
                    	    if(defined){
                    	        activeConnectors++;
                    	        let terminals = data[0].cardTerminals.cardTerminal;
                    	        anz_Terminals = anz_Terminals + terminals.length;
                    	        for(let i = 0; i< terminals.length; i++){
                    	            if(terminals[i].connected)
                    	            {
                    	                activeTerminals++;
                    	            }
                    	        }
                                let cards = data[1].cards.card;
                                anz_Cards = anz_Cards + cards.length;
                    	        defined = false;
                    	    }
                    	    if(numResponses == numConfigs){
                    	        let StatusConnectors = "";
                    	        if(activeConnectors == anz_Connectors){
                    	            StatusConnectors = "Success";
                    	        }
                    	        else{
                    	            StatusConnectors = "Error";
                    	        }
                    	        let StatusTerminals = "";
                    	        if(activeTerminals == anz_Terminals){
                    	            StatusTerminals = "Success";
                    	        }
                    	        else{
                    	            StatusTerminals = "Error";
                    	        }
                    	        content.push({
                                    "Name" : "Konnektoren",
                                    "Value": "Gesamt: " + anz_Connectors,
                                    "Icon": "http://localhost:8080/dashboard/images/Connector.png",
                                    "State" : StatusConnectors,
                                    "Info": "Online: " + activeConnectors
                                });
                                content.push({
                                    "Name" : "Kartenterminals",
                                    "Value" : "Gesamt: " + anz_Terminals,
                                    "Icon": "http://localhost:8080/dashboard/images/CardTerminal.png",
                                    "State": StatusTerminals,
                                    "Info" : "Online: " + activeTerminals
                                });
                                content.push({
                                    "Name" : "Karten",
                                    "Value": "Gesamt: " + anz_Cards,
                                    "Icon": "http://localhost:8080/dashboard/images/Card.png",
                                    "State": "Information"
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
