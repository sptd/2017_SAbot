package jp.co.fxis.chatbotchallenge.lexevent;

public enum FulfillmentState {
	Fulfilled("Fulfilled"), Failed("Failed");

	private String value;

	private FulfillmentState(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

}
