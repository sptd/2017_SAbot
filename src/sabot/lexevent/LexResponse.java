package sabot.lexevent;


import java.util.Map;

import com.amazonaws.services.lexruntime.model.ResponseCard;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LexResponse {
	private Map<String, String> sessionAttributes = null;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class DialogAction {
		private String type = null;
		private String fulfillmentState = null;
		private Message message = null;
		private String intentName = null;
		private Map<String, String> slots = null;
		private String slotToElicit = null;
		private ResponseCard responseCard = null;

		public DialogAction() {
			super();
		}

		public String getType() {
			return type;
		}

		public String getFulfillmentState() {
			return fulfillmentState;
		}

		public Message getMessage() {
			return message;
		}

		public String getIntentName() {
			return intentName;
		}

		public Map<String, String> getSlots() {
			return slots;
		}

		public String getSlotToElicit() {
			return slotToElicit;
		}

		public ResponseCard getResponseCard() {
			return responseCard;
		}

	}

	private DialogAction dialogAction;

	public LexResponse(Type type) {
		dialogAction = new DialogAction();
		dialogAction.type = type.toString();
	}

	public Map<String, String> getSessionAttributes() {
		return sessionAttributes;
	}

	public DialogAction getDialogAction() {
		return dialogAction;
	}

	public void setSessionAttributes(Map<String, String> sessionAttributes) {
		this.sessionAttributes = sessionAttributes;
	}

	public void setType(Type type) {
		dialogAction.type = type.toString();
	}

	public void setFulfillmentState(FulfillmentState fulfillmentState) {
		dialogAction.fulfillmentState = fulfillmentState.toString();
	}

	public void setMessage(Message message) {
		dialogAction.message = message;
	}

	public void setIntentName(String intentName) {
		dialogAction.intentName = intentName;
	}

	public void setSlots(Map<String, String> slots) {
		dialogAction.slots = slots;
	}

	public void setSlotToElicit(String slotToElicit) {
		dialogAction.slotToElicit = slotToElicit;
	}

	public void setResponseCard(ResponseCard responseCard) {
		dialogAction.responseCard = responseCard;
	}

	
}
