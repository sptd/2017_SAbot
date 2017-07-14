package sabot.eventhandler;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.TimeZone;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import sabot.googlecalendar.GoogleCalendarService;
import sabot.lexevent.FulfillmentState;
import sabot.lexevent.LexInputEvent;
import sabot.lexevent.LexResponse;
import sabot.lexevent.Message;
import sabot.lexevent.Type;
import sabot.lexevent.Message.ContentType;

public class GoogleCalendarAddHandler extends GoogleCalendarResponseHandler {
	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	protected static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	protected static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	private static final String COMFIRMED_ERROR_ADD_MESSAGE = "Sorry. I cannot calendar-schedule Addition.\r\n Please retry initially or check Lambda-execute logs at AWS CloudWatch.";
	private static final String SLOT_NAME_1 = "Member";
	private static final String SLOT_NAME_2 = "Date";
	private static final String SLOT_NAME_3 = "StartTime";
	private static final String SLOT_NAME_4 = "EndTime";
	private static final String SLOT_NAME_5 = "Location";
	private static final String SLOT_NAME_6 = "Title";
	private static final String[] addSlotList = { SLOT_NAME_1, SLOT_NAME_2, SLOT_NAME_3, SLOT_NAME_4, SLOT_NAME_5,
			SLOT_NAME_6 };
	
	private AmazonS3 s3;

	public GoogleCalendarAddHandler(LexInputEvent input, String bucketName)
			throws EventHandlerException, IOException, ParseException {
		super(input, bucketName);
	}

	@Override
	protected void setFulufillmentResponse() {
		String calendarId;
		InputStream keyFileStream;
		Properties properties = new Properties();
		try {
			Date startDate = DATETIME_FORMAT.parse(input.getCurrentIntent().getSlots().get(SLOT_NAME_2) + " "
					+ input.getCurrentIntent().getSlots().get(SLOT_NAME_3));
			Date endDate = DATETIME_FORMAT.parse(input.getCurrentIntent().getSlots().get(SLOT_NAME_2) + " "
					+ input.getCurrentIntent().getSlots().get(SLOT_NAME_4));

			if (s3 == null) {
				// s3 = AmazonS3ClientBuilder.defaultClient();
				s3 = new AmazonS3Client();
				s3.setRegion(REGION);
				System.out.println("S3 OK");
			}

			properties.load(s3.getObject(new GetObjectRequest(this.bucketName, USER_FILE_FOLDER + USER_FILE_NAME))
					.getObjectContent());
			keyFileStream = s3
					.getObject(new GetObjectRequest(this.bucketName, GOOGLE_API_KEY_FOLDER + GOOGLE_API_KEY_FILE_NAME))
					.getObjectContent();
			System.out.println("IPIKey OK");
			Calendar service = new GoogleCalendarService(keyFileStream).getGoogleCalendarService();
			System.out.println("Service OK");
			Event event = new Event();
			event.setLocation(input.getCurrentIntent().getSlots().get(SLOT_NAME_5));
			event.setSummary(input.getCurrentIntent().getSlots().get(SLOT_NAME_6));
			String[] memberList = this.getMemberList(input.getCurrentIntent().getSlots().get(SLOT_NAME_1));
			for (String memberName : memberList) {
				calendarId = properties.getProperty(memberName);
				System.out.println("CalendarID:" + calendarId);
				String timeZone = service.calendars().get(calendarId).execute().getTimeZone();
				System.out.println("Calendar execute OK");
				event.setStart(
						new EventDateTime().setDateTime(new DateTime(startDate, TimeZone.getTimeZone(timeZone))));
				event.setEnd(new EventDateTime().setDateTime(new DateTime(endDate, TimeZone.getTimeZone(timeZone))));
				service.events().insert(calendarId, event).execute();
				System.out.println("Event Addition OK");
			}

			response = new LexResponse(Type.Close);
			response.setFulfillmentState(FulfillmentState.Fulfilled);
		} catch (Exception e) {
			System.out.println("calendar Failed");
			response = new LexResponse(Type.Close);
			response.setFulfillmentState(FulfillmentState.Failed);
			response.setMessage(new Message(ContentType.PlainText, COMFIRMED_ERROR_ADD_MESSAGE));
		}

	}

	@Override
	protected void setInitializationAndValidationReseponse() throws EventHandlerException {
		if (!isValidateSlots()) {
			throw new EventHandlerException();
		}

		if (isFirstElicitSlot()) {
			System.out.println("InitialElicitMemberIntent OK");
			setElicitSlotResponse(slotList[0]);
			return;
		}
		String checkSlotKey = null;
		for (String slotKey : slotList) {
			String slotValue = input.getCurrentIntent().getSlots().get(slotKey);
			if (!isHavingChecked(slotKey)) {
				checkSlotKey = slotKey;
				try {
					boolean validator;
					String invalidateMessage;

					switch (slotKey) {
					case SLOT_NAME_1:
						slotValue = input.getInputTranscript();
						validator = isValidateMember(slotValue);
						if (validator) {
							input.getCurrentIntent().getSlots().replace(slotKey, slotValue);
						}
						invalidateMessage = IVDALIDATE_MEMBER_MESSAGE;
						break;
					case SLOT_NAME_2:
						validator = isValidateDate(slotValue);
						invalidateMessage = IVDALIDATE_DATE_MESSAGE;
						break;
					case SLOT_NAME_3:
						validator = isValidateTime(SLOT_NAME_3, slotValue);
						invalidateMessage = IVDALIDATE_START_MESSAGE;
						break;
					case SLOT_NAME_4:
						validator = isValidateTime(SLOT_NAME_4, slotValue);
						invalidateMessage = IVDALIDATE_END_MESSAGE;
						break;
					case SLOT_NAME_5:
						if (slotValue == null) {
							slotValue = input.getInputTranscript();
						}
						validator = isValidateLocation(slotValue);
						if (validator) {
							input.getCurrentIntent().getSlots().replace(slotKey, slotValue);
						}
						invalidateMessage = IVDALIDATE_LOCATION_MESSAGE;
						break;
					case SLOT_NAME_6:
						if (slotValue == null) {
							slotValue = input.getInputTranscript();
						}
						validator = isValidateTitle(slotValue);
						if (validator) {
							input.getCurrentIntent().getSlots().replace(slotKey, slotValue);
						}
						invalidateMessage = IVDALIDATE_TITLE_MESSAGE;
						break;
					default:
						throw new EventHandlerException();
					}

					if (!validator) {
						System.out.println("Invalidate" + slotKey + "Intent OK");
						setInvalidateSlotResponse(slotKey, slotValue, invalidateMessage);
						return;
					} else {
						System.out.println(slotKey + "Slot is validate");
						if (input.getSessionAttributes() == null) {
							input.setSessionAttributes(new LinkedHashMap<>());
						}
						input.getSessionAttributes().put(slotKey, slotValue);
						System.out.println(slotKey + "Slot is set in sessionAttributes");
						break;
					}

				} catch (IOException e) {
					throw new EventHandlerException(e);
				}
			}
		}

		System.out.println(checkSlotKey);
		System.out.println(getNextElicitSlot(checkSlotKey));
		if (checkSlotKey != null && getNextElicitSlot(checkSlotKey) != null) {
			System.out.println("Elicit" + getNextElicitSlot(checkSlotKey) + "SlotIntent OK");
			setElicitSlotResponse(getNextElicitSlot(checkSlotKey));
			return;
		}

		setComfirmResponse();
	}

	private boolean isValidateMember(String memberName) throws IOException {
		Properties properties = new Properties();
		String[] membersList = this.getMemberList(memberName);
		if (s3 == null) {
			// s3 = AmazonS3ClientBuilder.defaultClient();
			s3 = new AmazonS3Client();
		}
		S3Object object = s3.getObject(new GetObjectRequest(this.bucketName, USER_FILE_FOLDER + USER_FILE_NAME));
		properties.load(object.getObjectContent());
		for (String member : membersList) {
			if (!properties.containsKey(member)) {
				return false;
			}
		}
		return true;
	}

	private boolean isValidateDate(String dateString) {
		try {
			DATE_FORMAT.parse(dateString);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	private boolean isValidateTime(String slotName, String timeString) {
		try {
			TIME_FORMAT.parse(timeString);
			String startDate;
			String endDate;
			LocalDateTime startDateTime;
			LocalDateTime endDateTime;
			if (slotName == SLOT_NAME_3) {
				startDate = timeString;
				endDate = input.getCurrentIntent().getSlots().get(SLOT_NAME_4);
				if (endDate == null) {
					return true;
				}
			} else {
				startDate = input.getCurrentIntent().getSlots().get(SLOT_NAME_3);
				endDate = timeString;
				if (startDate == null) {
					return true;
				}
			}

			if (input.getCurrentIntent().getSlots().get(SLOT_NAME_2) != null) {
				startDateTime = LocalDateTime
						.parse(input.getCurrentIntent().getSlots().get(SLOT_NAME_2) + " " + startDate, formatter);
				endDateTime = LocalDateTime.parse(input.getCurrentIntent().getSlots().get(SLOT_NAME_2) + " " + endDate,
						formatter);
			} else {
				startDateTime = LocalDateTime.parse("2017-07-18" + " " + startDate, formatter);
				endDateTime = LocalDateTime.parse("2017-07-18" + " " + endDate, formatter);
			}
			if (endDateTime.isAfter(startDateTime)) {
				return true;
			}

			return false;
		} catch (ParseException e) {
			return false;
		}
	}

	private boolean isValidateLocation(String slotValue) {
		if (slotValue.isEmpty()) {
			return false;
		}
		return true;
	}

	private boolean isValidateTitle(String slotValue) {
		if (slotValue.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	protected void setSlotList() {
		slotList = addSlotList;
	}

}
