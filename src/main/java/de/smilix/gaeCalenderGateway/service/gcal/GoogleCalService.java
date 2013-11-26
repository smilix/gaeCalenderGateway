package de.smilix.gaeCalenderGateway.service.gcal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Creator;
import com.google.api.services.calendar.model.Event.Reminders;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import de.smilix.gaeCalenderGateway.model.Config;
import de.smilix.gaeCalenderGateway.model.ICalInfos;
import de.smilix.gaeCalenderGateway.service.AuthService;
import de.smilix.gaeCalenderGateway.service.data.ConfigurationService;

public class GoogleCalService {

  private static final Logger LOG = LoggerFactory
          .getLogger(GoogleCalService.class);

  private static GoogleCalService instance;

  public static GoogleCalService get() {
    if (instance == null) {
      instance = new GoogleCalService();
    }
    return instance;
  }

  private GoogleCalService() {
  }

  private void checkEventParameter(ICalInfos event) throws GCCException {
    nullCheckWithExcpetion(event, "event object is null");
    nullCheckWithExcpetion(event.getStartTimestamp(), "start time is null");
    nullCheckWithExcpetion(event.getEndTimestamp(), "end time is null");
    // todo: more checks
  }

  private void nullCheckWithExcpetion(Object objectToCheck, String excpMessage) throws GCCException {
    if (objectToCheck == null) {
      throw new GCCException("event object is null");
    }
  }

  public void addEvent(ICalInfos ical) throws IOException {
    //    checkEventParameter(event);

    Config config = ConfigurationService.getConfig();
    String calendarId = config.getCalendarId();
    Calendar calendarSrv = AuthService.get().loadCalendarClient();

    Event event = new Event();
    event.setICalUID(ical.getuId());
    event.setSummary(ical.getSummary());
    event.setDescription(ical.getDescription());
    event.setLocation(ical.getLocation());

    event.setStart(makeTime(ical.getStartTimestamp()));
    event.setEnd(makeTime(ical.getEndTimestamp()));

    List<EventAttendee> attendees = new ArrayList<>();
    for (String attendeeName : ical.getAttendees()) {
      EventAttendee ea = new EventAttendee();
      ea.setDisplayName(attendeeName);
      ea.setEmail("none@localhost");
      attendees.add(ea); 
    }
    event.setAttendees(attendees);
    
    // add attendees as description values, because they don't have an email address
//        StringBuilder description = new StringBuilder();
//        for (String attendeeName : event.getAttendees()) {
//          description.append(attendeeName).append("\n");
//        }

    Creator creator = new Creator();
//    creator.setEmail("none@localhost");
    creator.setDisplayName(ical.getOrganizer());
    event.setCreator(creator);

    Reminders reminders = new Reminders();
    EventReminder eventReminder = new EventReminder();
    eventReminder.setMinutes(3);
    eventReminder.setMethod("popup");
    reminders.setOverrides(Arrays.asList(eventReminder));
    reminders.setUseDefault(false); // or you get cannotUseDefaultRemindersAndSpecifyOverride
    event.setReminders(reminders);
    event = calendarSrv.events().insert(calendarId, event).execute();
    LOG.info("Event added: " + event);


    // OLD copy & paste

    // add attendees as description values, because they don't have an email address
    //    for (String attendeeName : event.getAttendees()) {
    //      EventWho attendee = new EventWho();
    //      attendee.setValueString(attendeeName);
    //      attendee.setRel(Rel.EVENT_ATTENDEE);
    //      myEntry.addParticipant(attendee);
    //    }
    //    StringBuilder description = new StringBuilder();
    //    for (String attendeeName : event.getAttendees()) {
    //      description.append(attendeeName).append("\n");
    //    }
    //    description.append("------------- DESCRIPTION -------------\n").append(event.getDescription());
    //    myEntry.setContent(new PlainTextConstruct(description.toString()));


    //    String rRule = 
    //    "RRULE:FREQ=WEEKLY;COUNT=5;INTERVAL=2;BYDAY=WE;WKST=SU\r\n"+
    //    "DTSTART;TZID=W. Europe Standard Time:20120328T100000\r\n"+
    //    "DTEND;TZID=W. Europe Standard Time:20120328T121500";
    //    Recurrence recur = new Recurrence();
    //    recur.setValue(rRule);
    //    myEntry.setRecurrence(recur);

  }

  private EventDateTime makeTime(Long startTimestamp) {
    EventDateTime time = new EventDateTime();
    time.setDateTime(new DateTime(startTimestamp));
    return time;
  }

  public void addTestEvent(ICalInfos event) throws IOException {
    Config config = ConfigurationService.getConfig();

    String calendarId = config.getCalendarId();

    Calendar calendarSrv = AuthService.get().loadCalendarClient();

    final long DAY_DELTA = 1000 * 60 * 60 * 24;
    final long DAY_AND_HOUR_DELTA = DAY_DELTA + (1000 * 60 * 60);

    Event newEvent = new Event();
    newEvent.setSummary("sum" + event.getSummary());
    EventDateTime start = new EventDateTime();
    // == 16.12.2013 at 14.26 
    //    start.setDateTime(new DateTime(new Date(113, 11, 16, 14, 26)));
    start.setDateTime(new DateTime(new Date(System.currentTimeMillis() + DAY_DELTA)));

    newEvent.setStart(start);
    EventDateTime stop = new EventDateTime();
    //    stop.setDateTime(new DateTime(new Date(113, 11, 16, 16, 29)));
    stop.setDateTime(new DateTime(new Date(System.currentTimeMillis() + DAY_AND_HOUR_DELTA)));
    newEvent.setEnd(stop);
    EventAttendee ea = new EventAttendee();
    ea.setDisplayName("hans");
    ea.setEmail("lala@circlelab.de");
    newEvent.setAttendees(Arrays.asList(ea));
    Creator creator = new Creator();
    creator.setEmail("hlger@circlelab.de");
    creator.setDisplayName("holger");
    newEvent.setCreator(creator);
    newEvent.setDescription("ich bin eine <b>description</b>...");

    newEvent.setICalUID(RandomStringUtils.randomAlphabetic(10));
    Reminders reminders = new Reminders();
    EventReminder eventReminder = new EventReminder();
    eventReminder.setMinutes(3);
    eventReminder.setMethod("popup");
    reminders.setOverrides(Arrays.asList(eventReminder));
    reminders.setUseDefault(false); // or you get cannotUseDefaultRemindersAndSpecifyOverride
    newEvent.setReminders(reminders);
    newEvent.setLocation("raum 17");
    newEvent = calendarSrv.events().insert(calendarId, newEvent).execute();

    LOG.info("Event added: " + newEvent);
  }
}
