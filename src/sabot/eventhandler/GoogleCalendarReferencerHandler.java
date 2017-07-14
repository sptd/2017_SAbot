package sabot.eventhandler;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import sabot.googlecalendar.CalendarEvent;
import sabot.googlecalendar.GoogleCalendarService;
import sabot.lexevent.FulfillmentState;
import sabot.lexevent.LexInputEvent;
import sabot.lexevent.LexResponse;
import sabot.lexevent.Message;
import sabot.lexevent.Type;
import sabot.lexevent.Message.ContentType;

public class GoogleCalendarReferencerHandler extends GoogleCalendarResponseHandler {
	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final String COMFIRMED_ERROR_REF_MESSAGE = "Sorry. I cannot calendar-schedule reference.\r\n Please retry initially or check Lambda-execute logs at AWS CloudWatch.";
	private static final String SLOT_NAME_1 = "Member";
	private static final String SLOT_NAME_2 = "Date";
	private static final String[] referenceSlotList = { SLOT_NAME_1, SLOT_NAME_2 };
	private AmazonS3 s3;

	public GoogleCalendarReferencerHandler(LexInputEvent input, String bucketName)
			throws EventHandlerException, IOException, ParseException {
		super(input, bucketName);
	}

	@Override
	protected void setFulufillmentResponse() {
		String calendarId;
		InputStream keyFileStream;
		String dateString = input.getCurrentIntent().getSlots().get(SLOT_NAME_2);
		StringBuilder sb = new StringBuilder();

		Properties properties = new Properties();
		try {
			if (s3 == null) {
				// s3 = AmazonS3ClientBuilder.defaultClient();
				s3 = new AmazonS3Client();
				s3.setRegion(REGION);
				System.out.println("S3 OK");
			}

			properties.load(s3.getObject(new GetObjectRequest(this.bucketName, USER_FILE_FOLDER + USER_FILE_NAME))
					.getObjectContent());
			System.out.println(" Date:" + dateString);
			keyFileStream = s3
					.getObject(new GetObjectRequest(this.bucketName, GOOGLE_API_KEY_FOLDER + GOOGLE_API_KEY_FILE_NAME))
					.getObjectContent();
			System.out.println("IPIKey OK");
			Calendar service = new GoogleCalendarService(keyFileStream).getGoogleCalendarService();
			System.out.println("Service OK");
			String[] memberList = this.getMemberList(input.getCurrentIntent().getSlots().get(SLOT_NAME_1));
			for (String memberName : memberList) {
				calendarId = properties.getProperty(memberName);
				sb.append(getCalendarSchejuleMessage(service, memberName, calendarId, dateString));
			}
			System.out.println("Event Message OK");
			response = new LexResponse(Type.Close);
			response.setFulfillmentState(FulfillmentState.Fulfilled);
			response.setMessage(new Message(ContentType.PlainText, new String(sb)));
		} catch (Exception e) {
			System.out.println("calendar Failed");
			response = new LexResponse(Type.Close);
			response.setFulfillmentState(FulfillmentState.Failed);
			response.setMessage(new Message(ContentType.PlainText, COMFIRMED_ERROR_REF_MESSAGE));
		}

	}

	private String getCalendarSchejuleMessage(Calendar service, String memberName, String calendarId, String dateString)
			throws ParseException, IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("[\"" + memberName + "\" Schedule on " + dateString + "]\r\n");
		System.out.println("CalendarID:" + calendarId + "  Date:" + dateString);

		String timeZone = service.calendars().get(calendarId).execute().getTimeZone();
		System.out.println("Calendar execute OK");
		Date startDate = DATE_FORMAT.parse(dateString);
		System.out.println("Dateparse OK");
		DateTime start = new DateTime(startDate, TimeZone.getTimeZone(timeZone));
		DateTime end = new DateTime(new Date(startDate.getTime() + 24 * 60 * 60 * 1000),
				TimeZone.getTimeZone(timeZone));

		Events events = service.events().list(calendarId).setMaxResults(30).setTimeMin(start).setTimeMax(end)
				.setOrderBy("startTime").setSingleEvents(true).execute();
		System.out.println("Events OK");
		List<Event> items = events.getItems();
		List<CalendarEvent> calendarEvents = new ArrayList<>();
		for (Event e : items) {
			calendarEvents.add(new CalendarEvent(calendarId, e, timeZone));
		}
		if (calendarEvents.isEmpty()) {
			sb.append("--NO EVENT, ALL FREE.");
			sb.append("\n");
		} else {
			calendarEvents.forEach(e -> {
				if (e.isAllday()) {
					sb.append("--#All day\r\n");
				} else {
					sb.append("--#" + DateTimeFormatter.ofPattern("HH:mm").format(e.getStartDateTime()) + " ~ "
							+ DateTimeFormatter.ofPattern("HH:mm").format(e.getEndDateTime()) + "\r\n");
				}
				sb.append("---" + e.getEvent().getSummary() + " ");
				if (e.getEvent().getLocation() != null && !e.getEvent().getLocation().isEmpty()) {
					sb.append("@" + e.getEvent().getLocation());
				}
				sb.append("\n");
			});
		}
		sb.append("\n");
		return new String(sb);
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
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	protected void setSlotList() {
		slotList = referenceSlotList;

	}
}
