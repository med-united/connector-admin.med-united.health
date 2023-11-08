package health.medunited.cat.lib.model;

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

  private final String cardType;

  CardHandleType(String cardType) {
    this.cardType = cardType;
  }

  public String getCardHandleType() {
    return cardType;
  }
}
