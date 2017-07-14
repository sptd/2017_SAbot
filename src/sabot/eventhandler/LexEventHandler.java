package sabot.eventhandler;

import java.io.IOException;
import java.text.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;

import sabot.lexevent.LexInputEvent;
import sabot.lexevent.LexResponse;

public class LexEventHandler {

	private LexInputEvent lexEvent;
	private LexResponseHandler responseHandler;
	private String bucketName;
	private static final String CALENDAR_REFERENCE = "Reference"; 
	private static final String CALENDAR_ADD = "Add"; 

	public LexEventHandler(LexInputEvent lexEvent, String bucketName) throws EventHandlerException, IOException, ParseException {
		this.lexEvent = lexEvent;
		this.bucketName = bucketName;
		responseHandler = createResponseHandler();
	}

	private LexResponseHandler createResponseHandler() throws EventHandlerException, IOException, ParseException {
		if (lexEvent.getCurrentIntent().getName() == "" || lexEvent.getCurrentIntent().getName() == null) {
			return new ElicitIntentResponseHandler(lexEvent);
		} else
			switch (lexEvent.getCurrentIntent().getName()) {
			case CALENDAR_REFERENCE:
				System.out.println("Reference Intent OK");
				return new GoogleCalendarReferencerHandler(lexEvent, bucketName);
			case CALENDAR_ADD:
				System.out.println("Add Intent OK");
				return new GoogleCalendarAddHandler(lexEvent, bucketName);
			default:
				System.out.println("Intent ERROR");
				return new FailedResponseHandler(lexEvent);
			}
	}

	public LexResponse getJsonString() throws JsonProcessingException {
		System.out.println("getOutput()@EventHandler");
		return responseHandler.getJsonString();
	}

}
