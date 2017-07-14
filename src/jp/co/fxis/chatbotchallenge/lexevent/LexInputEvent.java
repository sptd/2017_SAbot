package jp.co.fxis.chatbotchallenge.lexevent;

import java.util.Map;

public class LexInputEvent {
	public class CurrentIntent {
		private String name;
		private Map<String, String> slots;
		private String confirmationStatus;

		public Map<String, String> getSlots() {
			return slots;
		}

		public String getName() {
			return name;
		}

		public String getConfirmationStatus() {
			return confirmationStatus;
		}

	}

	private CurrentIntent currentIntent;

	public class Bot {
		private String name;
		private String alias;
		private String version;

		public String getName() {
			return name;
		}

		public String getAlias() {
			return alias;
		}

		public String getVersion() {
			return version;
		}

	}

	private Bot bot;
	private String userId;
	private String inputTranscript;
	private String invocationSource;
	private String outputDialogMode;
	private String messageVersion;
	private Map<String, String> sessionAttributes;

	public CurrentIntent getCurrentIntent() {
		return currentIntent;
	}

	public Bot getBot() {
		return bot;
	}

	public String getUserId() {
		return userId;
	}

	public String getInputTranscript() {
		return inputTranscript;
	}

	public String getInvocationSource() {
		return invocationSource;
	}

	public String getOutputDialogMode() {
		return outputDialogMode;
	}

	public String getMessageVersion() {
		return messageVersion;
	}

	public Map<String, String> getSessionAttributes() {
		return sessionAttributes;
	}

	public void setSessionAttributes(Map<String, String> sessionAttributes) {
		this.sessionAttributes = sessionAttributes;
	}
	
	

}
