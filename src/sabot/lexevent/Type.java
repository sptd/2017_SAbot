package sabot.lexevent;

public enum Type {
	ElicitIntent("ElicitIntent"), ConfirmIntent("ConfirmIntent"), ElicitSlot("ElicitSlot"), Close(
			"Close"), ReadyForFulfillment("ReadyForFulfillment"), Delegate("Delegate");
	private String value;

	private Type(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
