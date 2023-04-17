sap.ui.define([], function () {
	"use strict";
	return {
		formatDateAndTimeWithTimezone: function (sStatus) {
		    let dateAndTime = sStatus.split("T");
		    let datePart = dateAndTime[0].split("-");
		    let timePart =  dateAndTime[1];



		    let year = datePart[0];
		    let month = datePart[1];
		    let day = datePart[2];
		    let timeOnly = timePart.split(".")[0].split("Z")[0];

		    return day+"."+month+"."+year+" "+timeOnly;


		}
	};
});