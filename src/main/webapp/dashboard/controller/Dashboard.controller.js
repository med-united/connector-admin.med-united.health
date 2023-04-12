sap.ui.define(
  [
    "./AbstractMasterController",
    "../resources/libs/vega5",
    "../resources/libs/vega-embed5"
  ],
  function (
    AbstractMasterController,
    vega5js,
    vega5embedjs
  ) {
    "use strict";

    return AbstractMasterController.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.Dashboard",
      {
        onInit: function () {
          var myJSON = {
                            "$schema": "https://vega.github.io/schema/vega/v4.0.json",
                            "width": 400,
                            "height": 200,
                            "padding": 5,

                            "data": [
                              {
                                "name": "table",
                                "values": [
                                  {"category": "A", "amount": 28},
                                  {"category": "B", "amount": 55},
                                  {"category": "C", "amount": 43},
                                  {"category": "D", "amount": 91},
                                  {"category": "E", "amount": 81},
                                  {"category": "F", "amount": 53},
                                  {"category": "G", "amount": 19},
                                  {"category": "H", "amount": 87}
                                ]
                              }
                            ],

                            "signals": [
                              {
                                "name": "tooltip",
                                "value": {},
                                "on": [
                                  {"events": "rect:mouseover", "update": "datum"},
                                  {"events": "rect:mouseout",  "update": "{}"}
                                ]
                              }
                            ],

                            "scales": [
                              {
                                "name": "xscale",
                                "type": "band",
                                "domain": {"data": "table", "field": "category"},
                                "range": "width"
                              },
                              {
                                "name": "yscale",
                                "domain": {"data": "table", "field": "amount"},
                                "nice": true,
                                "range": "height"
                              }
                            ],

                            "axes": [
                              { "orient": "bottom", "scale": "xscale" },
                              { "orient": "left", "scale": "yscale" }
                            ],

                            "marks": [
                              {
                                "type": "rect",
                                "from": {"data":"table"},
                                "encode": {
                                  "enter": {
                                    "x": {"scale": "xscale", "field": "category", "offset": 1},
                                    "width": {"scale": "xscale", "band": 1, "offset": -1},
                                    "y": {"scale": "yscale", "field": "amount"},
                                    "y2": {"scale": "yscale", "value": 0}
                                  },
                                  "update": {
                                    "fill": {"value": "steelblue"}
                                  },
                                  "hover": {
                                    "fill": {"value": "red"}
                                  }
                                }
                              },
                              {
                                "type": "text",
                                "encode": {
                                  "enter": {
                                    "align": {"value": "center"},
                                    "baseline": {"value": "bottom"},
                                    "fill": {"value": "#333"}
                                  },
                                  "update": {
                                    "x": {"scale": "xscale", "signal": "tooltip.category", "band": 0.5},
                                    "y": {"scale": "yscale", "signal": "tooltip.amount", "offset": -2},
                                    "text": {"signal": "tooltip.amount"},
                                    "fillOpacity": [
                                      {"test": "datum === tooltip", "value": 0},
                                      {"value": 1}
                                    ]
                                  }
                                }
                              }
                            ]
                          };
          this.oRouter = this.getOwnerComponent().getRouter();
          this._bDescendingSort = false;
          console.log("Yeees, vega is installed:"+vega.version);

          setTimeout(myGreeting, 5000);

            function myGreeting() {
            	vegaEmbed("#__data12", myJSON)
                // result.view provides access to the Vega View API
                .then(result => console.log(result))
                .catch(console.warn);
             }
        },
        onRouteToMaster: function (oEvent) {

          this.oRouter.navTo("master");
        },
      }
    );
  }
);
