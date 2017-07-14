package sabot.eventhandler;

import java.io.IOException;
import java.text.ParseException;

import sabot.lexevent.LexInputEvent;
import sabot.lexevent.LexResponse;
import sabot.lexevent.Type;

public class ElicitIntentResponseHandler extends LexResponseHandler {

	public ElicitIntentResponseHandler(LexInputEvent input) throws EventHandlerException, IOException, ParseException {
		super(input, null);
	}

	@Override
	protected void createLexResponse() {
		response = new LexResponse(Type.ElicitIntent);
		response.setSessionAttributes(input.getSessionAttributes());
	}

	@Override
	protected void setFulufillmentResponse() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	protected void setInitializationAndValidationReseponse() throws EventHandlerException {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	protected void setSlotList() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

}
