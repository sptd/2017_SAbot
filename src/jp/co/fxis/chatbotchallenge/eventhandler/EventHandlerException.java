package jp.co.fxis.chatbotchallenge.eventhandler;

public class EventHandlerException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String MESSAGE = "The exception is occurred in event handler.";

	public EventHandlerException() {
		super(MESSAGE);
	}

	public EventHandlerException(Throwable cause) {
		super(MESSAGE, cause);
	}

}
