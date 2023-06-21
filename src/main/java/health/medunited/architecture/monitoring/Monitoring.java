package health.medunited.architecture.monitoring;

public class Monitoring {

    private boolean aktualisierung_konnektor;
    private boolean aktualisierung_kartenterminals;
    private boolean ueberpruefung_onlinestatusti;

    public boolean isAktualisierung_konnektor() {
        return aktualisierung_konnektor;
    }

    public boolean isAktualisierung_kartenterminals() {
        return aktualisierung_kartenterminals;
    }

    public boolean isUeberpruefung_onlinestatusti() {
        return ueberpruefung_onlinestatusti;
    }

    public Monitoring(boolean aktualisierung_konnektor,
                      boolean aktualisierung_kartenterminals,
                      boolean ueberpruefung_onlinestatusti) {

        this.aktualisierung_konnektor = aktualisierung_konnektor;
        this.aktualisierung_kartenterminals = aktualisierung_kartenterminals;
        this.ueberpruefung_onlinestatusti = ueberpruefung_onlinestatusti;

    }

}

