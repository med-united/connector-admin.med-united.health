package health.medunited.architecture.model;

public enum CardHandleType {
    EGK("EGK"),
    HBA_Q_SIG("HBA-qSig"),
    HBA("HBA"),
    SMC_B("SMC-B"),
    HSM_B("HSM-B"),
    SMC_KT("SMC-KT"),
    KVK("KVK"),
    ZOD_2_0("ZOD_2.0"),
    UNKNOWN("UNKNOWN"),
    HBA_X("HBAx"),
    SM_B("SM-B");

    private final String cardHandleType;

    CardHandleType(String cardHandleType) {
        this.cardHandleType = cardHandleType;
    }

    public String getCardHandleType() {
        return cardHandleType;
    }
}
