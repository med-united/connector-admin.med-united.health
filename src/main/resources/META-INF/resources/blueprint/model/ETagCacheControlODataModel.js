sap.ui.define(["sap/ui/model/odata/v2/ODataModel", "sap/ui/core/cache/CacheManager"],
function(ODataModel, CacheManager) {
	"use strict";
	return ODataModel.extend("blueprint.model.ETagCacheControlODataModel", {

		constructor: function () {
			ODataModel.apply(this, arguments);
			/*
			 * This map will contains for every url that
			 * provides an etag header a pair with the following
			 * structure:
			 * {
			 *     "Todos?sap-context-token=1&$skip=60&$top=20" : {
			 *         "ETag": "9d2a55bcfc40a85c4aa7fcf83caa3e0d",
			 *         "body" : "\{...\}",
			 *         "data" : {...}
			 *     }
			 * }
			 * 
			 */
			this.oETagUrlCache = {};
			var pCacheLoaded = new Promise(function (fnResolve) {			
				// Read cache from persistent cache
				CacheManager.get("ETagUrlCache").then(function(sETagUrlCache) {
					if(sETagUrlCache !== undefined) {
					    this.oETagUrlCache = JSON.parse(sETagUrlCache);
					}
					fnResolve();
				}.bind(this));
			}.bind(this));
			var pMetadataLoaded = this.oMetadata.pLoaded;
			this.oMetadata.pLoaded = Promise.all([pMetadataLoaded, pCacheLoaded]);
		},
		_updateETag : function(oRequest, oResponse) {
			var sUrl = oRequest.requestUri.replace(this.sServiceUrl + '/', '');
			if (!sUrl.startsWith("/")) {
				sUrl = "/" + sUrl;
			}
			// Add the body of every request with the url to our cache
			// do not add responses that already come from the cache
			if(!oResponse.cache && "ETag" in oResponse.headers) {
				var contentType = "";
				if("Content-Type" in oResponse.headers) {					
					contentType = oResponse.headers["Content-Type"];
				}
				var oCacheEntry = {
					"ETag": oResponse.headers.ETag,
					"statusCode": oResponse.statusCode,
					"body": oResponse.body,
					"data": contentType.match(/^application\/json.*/) ? JSON.parse(oResponse.body).d : oResponse.body, 
					"headers": oResponse.headers,
					"Content-Type": contentType,
					"cache": true
				};
				this.oETagUrlCache[sUrl] = oCacheEntry;
				
				CacheManager.set("ETagUrlCache", JSON.stringify(this.oETagUrlCache));
				
			}
			// Add an expiration in microseconds from 1970 UTC in milliseconds
			if(!oResponse.cache && "Cache-Control" in oResponse.headers && oResponse.headers["Cache-Control"].match(/max-age=(\d+)$/)) {	
				var oNow = new Date();
				// Update Expires to the given date
				if(sUrl in this.oETagUrlCache) {
					this.oETagUrlCache[sUrl]["Expires"] = oNow.getTime() + oNow.getTimezoneOffset() * 60000 + (RegExp.$1*1000);
				}
			}
			return ODataModel.prototype._updateETag.apply(this, arguments);
		},
		_createRequest: function () {
			var oRequest = ODataModel.prototype._createRequest.apply(this, arguments);
			var sUrl = oRequest.requestUri.replace(this.sServiceUrl + '/', '');
			if (!sUrl.startsWith("/")) {
				sUrl = "/" + sUrl;
			}
			if(sUrl in this.oETagUrlCache) {
				oRequest.headers["If-None-Match"] = this.oETagUrlCache[sUrl].ETag;
			}
			return oRequest;
		},
		_submitRequest : function(oRequest, fnSuccess, fnError) {
			var fnSuccessWrapper = fnSuccess;
			if("data" in oRequest && "__batchRequests" in oRequest.data) {
				var aResponsesFromCache = [];
				for(var i=0;i<oRequest.data.__batchRequests.length; i++) {
					var oBatchRequest = oRequest.data.__batchRequests[i];
					var oNow = new Date();
					var iNowInMicroseconds = oNow.getTime() + oNow.getTimezoneOffset() * 60000;
					var sUrl = "/"+oBatchRequest.requestUri;
					if(oBatchRequest.method === "GET" && sUrl in this.oETagUrlCache && "Expires" in this.oETagUrlCache[sUrl] && iNowInMicroseconds < this.oETagUrlCache[sUrl].Expires) {
						var oCacheEntry = this.oETagUrlCache[sUrl];
						aResponsesFromCache.push({
							index: i,
							request: oBatchRequest,
							response: oCacheEntry
						});
					}
				}
				// remove all request where the answer will come from the cache
				oRequest.data.__batchRequests = oRequest.data.__batchRequests.filter(function (o,i) {
					return aResponsesFromCache.filter(function (o) { return o.index == i; }).length == 0;
				});
				fnSuccessWrapper = function (oData, oResponse) {
					for(var i in aResponsesFromCache) {
						var oCacheItem = aResponsesFromCache[i];
						oRequest.data.__batchRequests.splice(oCacheItem.index, 0, oCacheItem.request);
						oData.__batchResponses.splice(oCacheItem.index, 0, oCacheItem.response);
						// oResponse.data.__batchResponses.splice(oCacheItem.index, 0, oCacheItem.response);
					}
					fnSuccess.apply(fnSuccess, [oData, oResponse]);
				};
			}
			// if all requests can be answered from the cache
			if(oRequest.data.__batchRequests.length == 0) {
				var oSuccessData= {"__batchResponses":[]};
				fnSuccessWrapper(oSuccessData, {
					"data": oSuccessData,
					"requestUri": oRequest.requestUri,
					"headers": {"Content-Type": "multipart/mixed"},
					"status": 203,
					"statusText": "Non-Authoritative Information"
				});
				return {
					"abort": function() {
						
					}
				}
			} else {				
				var retVal =  ODataModel.prototype._submitRequest.apply(this, [oRequest, fnSuccessWrapper, fnError]);
				return retVal;
			}
		},
		_getODataHandler: function (sUrl) {
			var oHandler = ODataModel.prototype._getODataHandler.apply(this, arguments);
			if (sUrl.indexOf("$batch") > -1) {
				var oldPartHandlerRead = oHandler.partHandler.read;
				var oldRead = oHandler.read;
				oHandler.read = function(oResponse, oParameter) {
					// Replace status so the request will be recognized as success
					oResponse.body = oResponse.body.replace(/304 Not Modified/g, "299 Custom not modified");
					var oRetVal = oldRead.apply(oHandler, arguments);
					return oRetVal;
				};
				
			}
			return oHandler;
		},
		_processSuccess : function(oRequest, oResponse, fnSuccess, mGetEntities, mChangeEntities, mEntityTypes, bBatch, aRequests) {
			// if we get a not modified header back and we have the Url in our cache
			var sUrl = oRequest.requestUri.replace(this.sServiceUrl + '/', '');
			if (!sUrl.startsWith("/")) {
				sUrl = "/" + sUrl;
			}
			if((oResponse.statusCode === 299 || oResponse.statusCode === '299') && sUrl in this.oETagUrlCache) {
				oResponse.statusCode = this.oETagUrlCache[sUrl].statusCode;
				oResponse.body = this.oETagUrlCache[sUrl].body;
				oResponse.data = this.oETagUrlCache[sUrl].data;
			}
			return ODataModel.prototype._processSuccess.apply(this, arguments);
		}

	});
});
