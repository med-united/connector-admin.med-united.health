sap.ui.define([], function () {
	"use strict";
	return {
		formatDateAndTimeWithTimezone: function (inputStamp) {

		    if ((inputStamp == undefined) || (inputStamp.length < 4)) return "--";

		    function convertTZ(date, tzString) {
                return new Date((typeof date === "string" ? new Date(date) : date).toLocaleString("en-US", {timeZone: tzString}));
            }

            function trailingZero(timeVal) {
                if (timeVal<10) return "0"+timeVal;
                else return timeVal;
            }

		    let dateAndTime = inputStamp.split("T");
		    let datePart = dateAndTime[0].split("-");
		    let timePart =  dateAndTime[1];

		    let year = datePart[0];
		    let month = datePart[1];
		    let day = datePart[2];
		    let timeOnly = timePart.split(".")[0].split("Z")[0].split("+")[0];

            if (timeOnly == "00:00:00") {
                timeOnly = "";
                return day+"."+month+"."+year;
            }

            //console.log("interm:"+day+"."+month+"."+year+"  "+timeOnly);

		    let convertedDate = convertTZ(year+"/"+month+"/"+day+" "+timeOnly, "Europe/Berlin")

            let convertedOutput = trailingZero(convertedDate.getDate())+"."+trailingZero(convertedDate.getMonth()+1)+"."
                                +convertedDate.getFullYear()+" "+trailingZero(convertedDate.getHours())+
                                ":"+trailingZero(convertedDate.getMinutes())+":"+
                                trailingZero(convertedDate.getSeconds());

		    return convertedOutput;

		}
	};
});