package sabot.eventhandler;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import sabot.lexevent.LexInputEvent;
import sabot.lexevent.LexResponse;
import sabot.lexevent.Message;
import sabot.lexevent.Type;
import sabot.lexevent.Message.ContentType;

public abstract class GoogleCalendarResponseHandler extends LexResponseHandler {
	protected static final String USER_FILE_FOLDER = "userInfo/";
	protected static final String USER_FILE_NAME = "user.properties";
	protected static final String GOOGLE_API_KEY_FOLDER = "apiKey/";
	protected static final String GOOGLE_API_KEY_FILE_NAME = "googleAPIkey.json";
	protected static final Region REGION = Region.getRegion(Regions.AP_NORTHEAST_1);
	protected static final String[] EXCEPT_LIST = { " ", "&", ";", ":" };
	protected static final String IVDALIDATE_MEMBER_MESSAGE = " does not exist in member lists or the input form is invalid. \r\n Please retry telling me member names using correct input. \n (ex1)\"member1,member2, member3, ...\" \n (ex2)\"all\" or \"all members\" or \"everyone\"";
	protected static final String IVDALIDATE_DATE_MESSAGE = " is not valid. \n Please tell me date at day unit. \n (For example, 2017-07-26, tomorrow, Friday, and so on).";
	protected static final String IVDALIDATE_START_MESSAGE = " is not valid or after the EndTime. \r\n Please tell me the correct time. \n (Do not use [night], [morning], [afternoon], and [evening]).";
	protected static final String IVDALIDATE_END_MESSAGE = " is not valid or before the StartTime. \r\n Please tell me the correct time. \n (Do not use [night], [morning], [afternoon], and [evening]).";
	protected static final String IVDALIDATE_LOCATION_MESSAGE = " is blank and it is not valid. \r\n Please retry.";
	protected static final String IVDALIDATE_TITLE_MESSAGE = " is blank and it is not valid. \r\n Please retry.";
	protected static final String ALL_MEMBERS_1 = "all members";
	protected static final String ALL_MEMBERS_2 = "all";
	protected static final String ALL_MEMBERS_3 = "everyone";

	public GoogleCalendarResponseHandler(LexInputEvent input, String bucketName)
			throws EventHandlerException, IOException, ParseException {
		super(input, bucketName);
	}

	protected abstract void setSlotList();

	protected void setComfirmResponse() {
		for (String key : slotList) {
			if (input.getCurrentIntent().getSlots().get(key) == null) {
				setElicitSlotResponse(key);
				return;
			}
		}
		response = new LexResponse(Type.ConfirmIntent);
		response.setIntentName(input.getCurrentIntent().getName());
		response.setSlots(input.getCurrentIntent().getSlots());
		response.setSessionAttributes(input.getSessionAttributes());
	}

	protected void setElicitSlotResponse(String slotKey) {
		response = new LexResponse(Type.ElicitSlot);
		response.setIntentName(input.getCurrentIntent().getName());
		response.setSlotToElicit(slotKey);
		response.setSlots(input.getCurrentIntent().getSlots());
		if (isFirstElicitSlot()) {
			response.setSessionAttributes(new LinkedHashMap<>());
		} else {
			response.setSessionAttributes(input.getSessionAttributes());
		}
		System.out.println("Elicit" + slotKey + "SlotResponse create OK");
	}

	protected void setInvalidateSlotResponse(String slotKey, String slotValue, String ivdalidateMessage) {
		String message = "Sorry, " + slotKey + "[" + slotValue + "]" + ivdalidateMessage;
		setElicitSlotResponse(slotKey);
		response.setMessage(new Message(ContentType.PlainText, message));
		System.out.println("Invalidate" + slotKey + "SlotResponse create OK");
	}

	protected String getNextElicitSlot(String endKey) {
		for (String key : slotList) {
			if (key != endKey && input.getCurrentIntent().getSlots().get(key) == null) {
				return key;
			}
		}
		return null;
	}

	protected boolean isFirstElicitSlot() {
		for (String key : slotList) {
			if (input.getCurrentIntent().getSlots().get(key) != null) {
				return false;
			}
		}
		return true;
	}

	protected boolean isValidateSlots() {
		for (String key : slotList) {
			if (!input.getCurrentIntent().getSlots().containsKey(key)) {
				return false;
			}
		}
		return true;
	}

	protected boolean isHavingChecked(String slotKey) {
		if (input.getSessionAttributes() == null) {
			return false;
		}
		if (input.getSessionAttributes().containsKey(slotKey) && input.getSessionAttributes().get(slotKey) != null
				&& input.getSessionAttributes().get(slotKey).equals(input.getCurrentIntent().getSlots().get(slotKey))) {
			return true;
		}
		return false;
	}

	protected String[] getMemberList(String message) {

		for (String str : EXCEPT_LIST) {
			message = message.replace(str, "");
		}
		String[] members = message.split(",", 0);
		System.out.println("Member is...");
		Arrays.stream(members).forEach(System.out::println);
		return members;
	}
}
