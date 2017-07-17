package sabot.eventhandler;

import sabot.lexevent.FulfillmentState;
import sabot.lexevent.LexInputEvent;
import sabot.lexevent.LexResponse;
import sabot.lexevent.Message;
import sabot.lexevent.Type;
import sabot.lexevent.Message.ContentType;

public class FailedResponseHandler extends LexResponseHandler {
	public FailedResponseHandler(LexInputEvent input) throws EventHandlerException{
		super(input, null);
	}

	@Override
	protected void createLexResponse() {
		response = new LexResponse(Type.Close);
		response.setFulfillmentState(FulfillmentState.Failed);
		response.setMessage(new Message(ContentType.PlainText, INPUT_INVALID_MESSAGE));
	}

	@Override
	protected void setFulufillmentResponse() {
	}

	@Override
	protected void setInitializationAndValidationReseponse() throws EventHandlerException {
	}

	@Override
	protected void setSlotList() {
	}

}
