package jp.co.fxis.chatbotchallenge.eventhandler;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;

import jp.co.fxis.chatbotchallenge.lexevent.LexInputEvent;
import jp.co.fxis.chatbotchallenge.lexevent.LexResponse;
import jp.co.fxis.chatbotchallenge.lexevent.Type;

public abstract class LexResponseHandler {
	protected final String INPUT_INVALID_MESSAGE = "Sorry. the error is occurred. \r\n Please retry initially.";
	protected LexResponse response;
	protected LexInputEvent input;
	protected String bucketName;
	protected String[] slotList;

	public LexResponseHandler(LexInputEvent input, String bucketName) throws EventHandlerException {
		this.input = input;
		this.bucketName = bucketName;
		setSlotList();
		createLexResponse();
	}

	protected abstract void setSlotList();

	protected void createLexResponse() throws EventHandlerException {
		if (input == null || getConfirmationStatus() == null) {
			throw new EventHandlerException();
		} else
			switch (getConfirmationStatus()) {
			case "None":
				System.out.println("None OK");
				setInitializationAndValidationReseponse();
				break;
			case "Confirmed":
				System.out.println("Confirmed OK");
				setFulufillmentResponse();
				break;
			case "Denied":
				setDeniedResponse();
				break;
			default:
				throw new EventHandlerException();
			}
	}

	protected String getConfirmationStatus() {
		if (input == null || input.getCurrentIntent().getConfirmationStatus() == null
				|| input.getCurrentIntent().getConfirmationStatus() == "") {
			return null;
		} else {
			return input.getCurrentIntent().getConfirmationStatus();
		}
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public LexResponse getJsonString() throws JsonProcessingException {
		return response;
	}

	protected void setDeniedResponse() {
		response = new LexResponse(Type.Delegate);
		response.setSessionAttributes(new LinkedHashMap<>());
		response.setSlots(input.getCurrentIntent().getSlots());
		;
	}

	protected abstract void setFulufillmentResponse();

	protected abstract void setInitializationAndValidationReseponse() throws EventHandlerException;

}
