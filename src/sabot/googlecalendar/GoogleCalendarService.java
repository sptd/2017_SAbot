package sabot.googlecalendar;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

public class GoogleCalendarService {
	private InputStream input = null;
	private Calendar calendarService = null;
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	public GoogleCalendarService(InputStream input) throws IOException {
		this.input = input;
		buildGoogleCalendarService();
	}

	private void buildGoogleCalendarService() throws IOException {
		GoogleCredential credential = GoogleCredential.fromStream(this.input, HTTP_TRANSPORT, JSON_FACTORY)
				.createScoped(Arrays.asList(CalendarScopes.CALENDAR));
		credential.refreshToken();
		calendarService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName("GoogleCalendar").build();
	}

	public Calendar getGoogleCalendarService() {
		return calendarService;
	}

}
