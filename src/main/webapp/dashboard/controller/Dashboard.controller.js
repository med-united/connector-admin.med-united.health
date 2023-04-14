sap.ui.define(
  [
    "./AbstractMasterController",
    "sap/ui/model/json/JSONModel",
    "../resources/libs/vega5",
    "../resources/libs/vega-embed5"
  ],
  function (
    AbstractMasterController,
    JSONModel,
    vega5js,
    vega5embedjs
  ) {
    "use strict";

    return AbstractMasterController.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.Dashboard",
      {
        onInit: function () {
          let graphJSON = {
                            "$schema": "https://vega.github.io/schema/vega/v5.json",
                            "title": "Gesteckte Karten aller verfügbaren Konnektoren",
                            "width": 1000,
                            "height": 300,
                            "padding": 5,

                            "data": [
                              {
                                "name": "table",
                                "values": [],
                                "transform": [
                                  {
                                    "type": "stack",
                                    "groupby": ["x"],
                                    "sort": {"field": "c"},
                                    "field": "y"
                                  }
                                ]
                              }
                            ],

                            "scales": [
                              {
                                "name": "x",
                                "type": "band",
                                "range": "width",
                                "domain": {"data": "table", "field": "x"},
                                "paddingInner": 0.1, // distance between stacks
                                "paddingOuter": 0.1
                              },
                              {
                                "name": "y",
                                "type": "linear",
                                "range": "height",
                                "nice": 1, // x axis displaying a scale of 1
                                "zero": true,
                                "domain": {"data": "table", "field": "y1"},
                                "round": true
                              },
                              {
                                "name": "color",
                                "type": "ordinal",
                                "range": ["#5899DA", "#E8743B", "#19A979", "#ED4A7B", "#945ECF"], // SAP Fiori for Web Design Guidelines
                                "domain": ["SMC_KT", "SMC_B", "HBA", "EGK", "KVK"]
                              }
                            ],

                            "axes": [
                              {"orient": "bottom", "scale": "x", "zindex": 1, "title":"Konnektoren"},
                              {"orient": "left", "scale": "y", "zindex": 1, "format": ".0f", "title":"Gesteckte Karten"}
                            ],

                            "marks": [
                              {
                                "type": "rect",
                                "from": {"data": "table"},
                                "encode": {
                                  "enter": {
                                    "x": {"scale": "x", "field": "x"},
                                    "width": {"scale": "x", "band": 1, "offset": -1},
                                    "y": {"scale": "y", "field": "y0"},
                                    "y2": {"scale": "y", "field": "y1"},
                                    "fill": {"scale": "color", "field": "c"}
                                  },
                                  "update": {
                                    "fillOpacity": {"value": 1}
                                  },
                                  "hover": {
                                    "fillOpacity": {"value": 0.5}
                                  }
                                }
                              }
                            ],
                            "legends":[
                                {
                                    "orient":"right",
                                    "direction":"vertical",
                                    "fill":"color",
                                    "encode": {
                                       "labels": {
                                         "interactive": true,
                                         "update": {"fontSize": {"value": 12}, "fill": {"value": "black"}}
                                       }
                                    }
                                }
                            ]
                          };
          this.oRouter = this.getOwnerComponent().getRouter();
          this._bDescendingSort = false;

          console.log("Vega is installed: "+vega.version);

            // attaching "manually" to a div __data12. We need a better way to do this
            // ideally this should be loaded into a panel or a card
            function attachGraphToElement() {
            	vegaEmbed("#__data16", graphJSON)
                    //.then(result => console.log(result))
                    .catch(console.warn);
             }

          const KopsHeader = {
          "x-client-system-id": "ClientID1",
          "x-client-certificate": "data:application/x-pkcs12;base64,MIIKogIBAzCCCkwGCSqGSIb3DQEHAaCCCj0Eggo5MIIKNTCCBawGCSqGSIb3DQEHAaCCBZ0EggWZMIIFlTCCBZEGCyqGSIb3DQEMCgECoIIFQDCCBTwwZgYJKoZIhvcNAQUNMFkwOAYJKoZIhvcNAQUMMCsEFOh3T/0I6XV3UwW8DDUHswhOBnhNAgInEAIBIDAMBggqhkiG9w0CCQUAMB0GCWCGSAFlAwQBKgQQaVb1KAsCxfDFEEo6zaqwXQSCBNB+fmttvpRLl5zhEITIlniICpu3eSCwLPgnDBX1GqKLuYwxsX4L5p1pkAFGPNmgGCg45gXIba8GzpHa2xeFv/bS07Qa4vPM0bB8/0dN1P5GRbd3QdU4qf1ycikY8/l0sGe0VTvxgj1FegAJ3Rb0pByHUgqHx+yyxyeUsw40hSOH+yk008cLDNF1Z87BNlKbCtBXdxY9cgZFS895j1feh7H248i5jWiuIObto5vr8G9gOp/b4Xvdm1HEjrif+I936Uh5o0BcTN4+RsN+atnDx77z1Yfo8GydgYNCqkqf/F/WOeNTuxfsBt+aDxAnTlRni9LTaO9s272qq0t/nq5Y1pIG5uD8dsKQ1rZ75KkTkcVnCHkWcX9Da1qGZuGgDekadh8ijZsEVWROvX+zp/vpLqoLPC5Yn08oulqHmWIZTdMnwuNMq0S3y/YFg+QR0/vd5b739GsPWyix8V+V6cXnGEChPzl+lRhgq/f7eHbU5QwOuX4yVNms6WpCg8BldI7wIMVmTt8GMb76nIBnD/idLWdgo1EYkh/3GvwHIgjXTl3jXJquL9EEROtasEryfUFxxghick4safcMtl8A/CjbtZKRgV9ycTF1/zjG11m+XwPCEXYN8HTwTmDLEOje9d/s7co887hG8gRqwGXwRF7F5hXAz/W0nsjpxJrXdV27Bs7+9bxMIipZqPOLnArxu3+nJ/sg9rnrm6sQ8imMhUNmtY9FvATJt3nNEU9XnEfWntBDrVh/K00Zet0PLtUqY0LDbIPaWiGAcfnVMW+iTPDwNyXsajPKwR+EbYeVoDSL3cWKZajqVTNJTtDvCgKX0EEX5dLWPkCA3ee5TjtGnZqmRV6vfbuyY9jeggEbFKrQU7+YEW8Wmkca8Bcr6rYnAlRtoWUvDg2Iw0LCGpRjm6xPwXhhajb/+H8wteKSAB/TMECbRr18ov2u//DTnHkFjY0TzvUIkPzyPhtT3/J6rc24CjUS/qn9VQXf7RWGQC2mQpfkoYGr/bernd/MjBoLDfVIY5+bdKptzJc6OL8srjVNFkF0B4M7MCyjHomyU91kSqeGtN7Iy2kuuqfFZyhq18NbfdqqnFEs1d6EAJg6bauuD0uefUdkB7YrB+XK84hfnfPXy57pI0AWZC5Jf1TcSAO2ZJHlCE3ohd2aPuvcvcwSttRJVX+5vU5eK+/1VSGz3GOdjUcxAP8E7tfesNNceMOP8R8XqwbE1w7Sm/swggXc5u3yC70t4vgbLg4q2fNIwQppEUiK4MiGvp1dbZVLICHqkxBxPN9OhY8I5Yssi41fb3rdixCa9FtRvzL071AUa2vvkKpWn6ZSSyOl7VywA4fy1DMrrs4BWbpzkV42L9JYuM7hlfdV5INhPKj4DFrnc2wlInu9YeJ73uACHGCh7Af0CWXE7GQ7DKOhch1aWZaMpqy65DI7hwxpJxhhCuav1XI2bo1/67XTv8k+chJAPF3eKO702lmz6CMKqB+D7wkToRAWPgQOOHUGIWt0910QzYwg9CxaTZqgEYA8TX11C8Oc8DUj+dyXP6lOyv3q5QpBV7VLnAov1Xyp6zbVgu04YS/981U84J51VZHzp+egC+6OJpjuAGimr1tYf3NugsbPC9USgkE/q59wb5rCMMctpMLHXTE+MBkGCSqGSIb3DQEJFDEMHgoAdgBhAGwAaQBkMCEGCSqGSIb3DQEJFTEUBBJUaW1lIDE2Nzg3ODU2NTM3MzQwggSBBgkqhkiG9w0BBwagggRyMIIEbgIBADCCBGcGCSqGSIb3DQEHATBmBgkqhkiG9w0BBQ0wWTA4BgkqhkiG9w0BBQwwKwQUsLy9NHaPC+TK8uPzZ2UA0piqesoCAicQAgEgMAwGCCqGSIb3DQIJBQAwHQYJYIZIAWUDBAEqBBDYhRS+U3IJUHan07PbXicqgIID8Mn9Q5quyi3Ny5ZOiR5LK/aqolNDnqlcoyylNtb5mOug/QtrcAjBDZ0Fco5kbVFNlhhoTfAw+x+icP/Ch+cuOeA7TweEIW/FujZ//oBjsH7eyE1T58ISWXV7BgF2+MF847IFYfpZsc2vre9WYv4M7cCV9XpciddwsZ7XGVCbV476e7SUm/bqcX7v2fKWiPoUJOr3VB+ZUi2fW9KcybNzBkU0kKNWEWgNTyUGDYi3q89gfPKtmjhcv1HXYBowldOeuRaZnzMgEVs1ZCyyJJwNk8OuKECP7UunbjN0qFo56auPLeCwuOCZNHn4eiQ8ENCcawF/HgLFTVKqq6FghPMIJA0n9M0rTamzRBHXUGxogqrKEd5v+LWTwpIzx4WkBKcOPtmKyA5g6wt6/iDPHW0j/qo0LyJUPydumrgqbmkRmwy3WHFsCTS54484Dt21cIhDt35LP2Vy3huA7MxarkEdmryacJ3y0wJqV5Bx30SUdcdLAC91Zr2kRWQwPXSiPhxVC+2Hoia62hXJYJeBicThDN0hsaT53943yqsewhwBdXMXgyBsAHTbrixKzDFRJPglhRydvGrtTpBaAhl16z9PvJtTiOB31T4GyNeDuZyDaCB6Vj6019eZQSqNS3UNs3jWt1K1vaYPVawdDQIru034id86rV0OYUMwOpUw7udPBmfwKdapziBKPp8Y8OrWnD7z5AxSSefM8rk7a2fdE8MNXSPeKtr463nUeT62KHwcs7ieQrQDvSJbkDDJl1ntgIUjY92wif6yM9gkK51MjgYRThWk0nzxI80BLW6aR8KAY5famA48Tot7fnoyp/MOLfFka3KMsG4XwlFwg0NSEyWisj2tbFXtN+UKeibxMKrgroDF+23KB7mjp/uy+0S/qj2Y6tQuRGvntql/ytq7aEZQOta16xB98iegAhwavZwAsYzGw0Jvu94QIb3928VqcNNK81juVfjUMsgA6DqkAC7SRr6jmi2Il7dmCpgO1JUepgJYCw++oj6eDWuxfxieOhbxQ5zQddwbeYLrjZxfTu1l+2xwZxn4czJmEsPgIQJn1gVoD+RYSs+iip5UnA9RZdJ/8dWob1HEDGNoiH2jHzPzmxjcyh+jy+lRSHuUKFctgWA0KDX3Bim+8l/4BFXD96p0+QswqBaKmo5m4Dfjia/m7Sgs2oqtL2YLWKx1InFVm10pI/Ort/lGJVVTnoIlRpKriiOCOmktFFJ40dPnF8opgS/6XvmWM5IUo9pcNLhKlv0mgGOnL41CJNg7PBceQ+3irUzRsf56xNm32jkO78kVbdIawjgr3rR3c+xn4V5epO1oh5IgrWrztwZhmmIaACMnoDBNMDEwDQYJYIZIAWUDBAIBBQAEIPobL70qtgwOHJwHyhzAg/jbmwaKWQDiAjTYS4Vtum4HBBRNJOHdqs8cbDCOAd6P5/mGkCbDzQICJxA=",
          "x-client-certificate-password":"123456",
          "x-mandant-id": "Mandant1",
          "x-workplace-id": "Workplace1",
          "x-host": "https://127.0.0.1",
          "Accept": "application/json",
          };

          const incentHeader = {
            "x-client-system-id": "Incentergy",
            "x-client-certificate": "data:application/x-pkcs12;base64,MIACAQMwgAYJKoZIhvcNAQcBoIAkgASCA+gwgDCABgkqhkiG9w0BBwGggCSABIID6DCCBVIwggVOBgsqhkiG9w0BDAoBAqCCBPswggT3MCkGCiqGSIb3DQEMAQMwGwQUsKEOeyT+A3MT3WRjP46E5LSVojwCAwDIAASCBMgHnMdsUgs2bjrjwwbS5e/ZCf8d4Sx0XzaswWMXZ0O8fiA+D1w2C4aOrjS4eTXAOHWcNi/L7yuU5XmZkqwJ4Vu/XJ0HQyDPKp/cpNxOAd/9BCILpxonVm7ztY1tsXK6B8yJG8KHlxbNMsBWnwsliM9WUf7S+FWDeqVPSJw6dRfXMMogTULFkK9ACJYeQwsvfHuum3Qv+BWc+Y/xVJo5sJPS5/kryciioO4kplyYeEK3BOxFPGhYyahD4GO/UW4X4cerSLAr+NPbLbcgF5as2e6jytI/bxiVcUsOmQCAJaF26qq//oV9HfMCtGVaiSE5ynR0PCbzhpkCrqUcSTjyo6UXL8EAyPHM4SeVhMxsEVYbdqR9Dxw90ICNeDG1zikk4GEQev/IwBzlCpwawTS+yT+q0iANX0Crk+KLyy6HtyylBMsqDgWL+GORUijZDOhA2nJSGQECLp/sytweOxhiEJrVAYOBZGsViDoSvgLDk2afgs+qODZpNHwqqq8T0SKGHygF/IqSVhx3S8YrBvb2YnYoZwagNdMuXVpZo5m+cHEvprzTqzYvnv5Ac5SIoGyyqFfW+bFCIv18RdmrpnRkzuPdGR39T0ybol2Bg4wB8AOTcDjTuyPmZvgAtx2/AncLYamicXp96sKz7rzhpPngfqztp9I5x6M98bglLJgofwfN7p9lM9ygiIH1d/A9mdAu3VzYZ7DbOoV/l1I0hWezUuP/iutLYvTKCWNjs98z+qmmx9T9MP/C21PH8a0uHecXDyHs+Pu0WHfeNjHM06EZjUhxk8pjXA6HeKtO1hWJNX6GSIti2LH2RCfEKDeWWbT4ZV7eVhEvNHG1X6XYlNraXnMUTem62JFyisafnXmVoMRzpwZENvBygLznjQE9vwGe3mQnA/T2cVZkPQ3SQBCRm/kFC5lOZkDu0oDWsDRoxVVB1kgWGWnsYb76tFmGymQKcZZVEAzkcmzL7FwWCKMBvYwGrq5se7ikCqCQHBeZsC/KYqunYT3Y3DdNZDRXQp/kGlXdFTkZdX/NZjueDOqP+sucqhWy4dvqReOysk40mXlvj+6d5kvZIYb4Z6QsxDROY1Ap5eVy51Oxh68ywmu+KcLcRwaD7CXOD7zid3FT2eS8PdwPHaF/SvLexKKE02WJsMJbv8K+aCgl2rb+KRZEb+kG/nwK7odDxdeWRk02XLLVYegrSekGBIID6MHx+y///JKUl0Nhxm89FAKmJQwOAIP/BIIBbhkjWOapF2cAtdyJPvnxC9rRN60OK5IM6HD2a+CSWYRUoY3v0RqlHtB8m9a3Ve8d75Gb5OO0AtA7mxeEf5jPk9YkNaPlH9W3Qw7qysEmrqCgFH6VRacX7gcClkGCimNnp+VRKATh4bSdwg/2NPbY6PIC2Y4Pljnt5M18D7LRX2oNRZDQC0Ej+OLaSncjDZlB4psrdA8cuyEzpydF8VoSvk+Pb22kb9zQRFDg0UMjWgDBhSWTCZm4gJEozZxgRyLfc24z2psHbCcGRyvGtz+S0S0LCWr0A0AT/3upXKzZ6PCrcf1E2pWqAVg7PePH3lg0g0tqd/dfVLE25eESVKETbfO9UhgI6GAjTgrQhjB6sEdI7jIKmAmg2AC6ONYLSHNgDhlBvZRB7ilpcFT3KjFAMBkGCSqGSIb3DQEJFDEMHgoAdgBhAGwAaQBkMCMGCSqGSIb3DQEJFTEWBBQ0gn+WEEW2jqtu8ZFDpMrMy2XR3QAAAAAAADCABgkqhkiG9w0BBwaggDCAAgEAMIAGCSqGSIb3DQEHATApBgoqhkiG9w0BDAEGMBsEFHSdfN8iGJ3Wv75K6LAgKWlH9iORAgMAyACggASCA+im7Nnu0Y1QxslFJtLK/XhB5E+ldYrEKKO13/zxmK2igscj3EmHhquUKM9Ob8XuOv0U0CRPktRTCu+SAUOmqb8o8zvgGE6OT8GowsLFvLDA7k9hNAThtVx3jRG7W1PkbEvI7zL5h1WDC/caXJFWId+1wDJRrRAcLDwzCsnqpeSUiUZOfFGlkyGo2P8huZYfdkSnasBpPrxIZUygRU3qmhDbBpnPFmyn5CJxjFMsfWbIaPnkykR8RRc0l+FsLyj3sklo9k7o3vFtdY0nQUNOvgf670GEE3Fsd6atLr33z7dlAQu5U6uZEBIOo4Qr5Xc5H99wa/USvguiKo9RALXHPOgCvk4JEWqMlJxTpKf1IWZVBZDADly80hrILcfvSDxZTmHNxm49PjfblFCNvTxsNsYp049Zxu7//RQtf/Hmc31BYHs2MhidPCJs5H9/JUBWVvHF4hVYbOWbbBH5f+HYkyr/DdSFIIkPLhrbX0cT4ldGzXsnhdGKxOept+QmUnTAXpFWYoqFDRWupVcaKhfuYoAHRxUJmnBQ7fGQXrQhStbJ50Q2R0FObo3sSmbedCcWKD9D1GXTv9s7J+5yMatAqTkm7xmY0hmI1cKDqx1ZcSrl3HLKDBmtjNzNFs8UYp4t8cYuFKhGTlQRrP9QxrY0ByFDqLN/bQd2CyafaCj3vFK5oQzuPTobKAcEggHtrjb91YJSfL6x22Fwz3pyCcl5WPU33k36Ki+qKHw36BJXN9Ca6NqAgdu4cl6LK9mYz3HZEkWibFhio0xoJdArH+q+fqyAfYyC45FQiKIE+odEqDlhCl2cxUP7dYL7RDj7E+HEopL3v4WM2XId1CUWOcYJwqjnmbOlahuBVhwZef2djQN4TrP3OuSAY+Lt4qZgZ2KOuaVgjNNqBL16v6DIFKUJKCraI86t+0eRgr4tbDjkU9HjxJ5m8GYNonyHhv6+zvARbUT04RbEYuj31fkwn4R4Unw7pUtJB7rIUH9ljPZc/+/loL0Fs6Y/FNFb4EVqqPdSdFUC5/a+9FrASOAlebtVaIRk8F/mMRjwFt4nan2oWCoTc+X3v9pafaEEdPlY5f/9WOa2tr9FQpROwYyf7gJ2wVGo+32PO55mqTbje+9mLvLpMK0Fkl6DZrdiyheNj2ZS4NfTwmQ1loCrnlJdjsnRG5eVZTTn9PpLC5OGwjpII9QOr4ouEcmLUUjbqEij6Rzy8wiisMGs/oHrH26rJs9Frh4TgYESrLD0FFaO8s/ha7JrO6qpKOlBzoXdBHsuHWjlncipfJUM0n5gKMKkZAj/a+cHcbK8juYiJWm8TYX3qjJE1ZDpH9rJKnX6N9oqFQAAAAAAAAAAAAAAAAAAAAAAADA+MCEwCQYFKw4DAhoFAAQUdnEvbILSKagRVWeJJUGg8vKnZUEEFGy1SHud+6JHd/+/uxUwq+g/wgzcAgMBkAAAAA==",
            "x-client-certificate-password":"N4rouwibGRhne2Fa",
            "x-mandant-id": "Incentergy",
            "x-workplace-id": "1786_A1",
            "x-host": "https://192.168.178.42",
            "Accept": "application/json",
          };


          fetch("/connector/event/get-cards", { headers: KopsHeader })
            .then(response => {
                response.json().then(data => {
                    let content = [];
                    let cards = data.cards.card;
                    let konnektor = "Kops";
                    for(let i=0;i<cards.length;i++) {
                        let type = cards[i].cardType;
                        content.push({
                                "x" : konnektor,
                                "y" : 1,
                                "c" : type
                            });
                        }
                    graphJSON.data[0].values = content;
                    attachGraphToElement();
                });
            });

        },
        onRouteToMaster: function (oEvent) {

          this.oRouter.navTo("master");
        },
      }
    );
  }
);