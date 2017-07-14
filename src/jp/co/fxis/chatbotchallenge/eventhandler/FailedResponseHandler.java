package jp.co.fxis.chatbotchallenge.eventhandler;

import jp.co.fxis.chatbotchallenge.lexevent.FulfillmentState;
import jp.co.fxis.chatbotchallenge.lexevent.LexInputEvent;
import jp.co.fxis.chatbotchallenge.lexevent.LexResponse;
import jp.co.fxis.chatbotchallenge.lexevent.Message;
import jp.co.fxis.chatbotchallenge.lexevent.Type;
import jp.co.fxis.chatbotchallenge.lexevent.Message.ContentType;

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
