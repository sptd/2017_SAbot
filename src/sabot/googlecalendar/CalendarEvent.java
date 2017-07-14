package sabot.googlecalendar;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

public class CalendarEvent {
	private final String calendarId;
	private final Event event;
	private final String zoneId;
	private final ZonedDateTime startDateTime;
	private final ZonedDateTime endDateTime;
	private boolean alldayFlag;

	public CalendarEvent(String calendarId, Event event, String zoneId) {
		this.calendarId = calendarId;
		this.event = event;
		this.zoneId = zoneId;
		System.out.println("eventTimeStart:" + this.event.getStart());
		System.out.println("eventTimeEnd:" + this.event.getEnd());

		this.startDateTime = createDateTime(event.getStart(), this.zoneId);
		this.endDateTime = createDateTime(event.getEnd(), this.zoneId);
	}

	public String getCalendarId() {
		return calendarId;
	}

	public String getZoneId() {
		return zoneId;
	}

	public Event getEvent() {
		return event;
	}

	public ZonedDateTime getStartDateTime() {
		return startDateTime;
	}

	public ZonedDateTime getEndDateTime() {
		return endDateTime;
	}

	private ZonedDateTime createDateTime(EventDateTime dateTime, String zoneId) {
		Date date = null;
		dateTime.setTimeZone(zoneId);
		if (dateTime.getDateTime() != null) {
			alldayFlag = false;
			date = new Date(dateTime.getDateTime().getValue());
		} else {
			alldayFlag = true;
			date = new Date(dateTime.getDate().getValue());
		}
		Instant instant = date.toInstant();
		return ZonedDateTime.ofInstant(instant, ZoneId.of(zoneId));
	}

	public boolean isAllday() {
		return alldayFlag;
	}

}
