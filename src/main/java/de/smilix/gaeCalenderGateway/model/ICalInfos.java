package de.smilix.gaeCalenderGateway.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.google.appengine.api.datastore.Text;

import de.smilix.gaeCalenderGateway.common.Utils;

/**
 * Selected information from the ical data.
 * 
 * @author Holger Cremer
 */
@Entity
public class ICalInfos extends DatastoreObject implements Serializable {
  
  public enum Status {
    PARSED, ADD_SUCCESS, ADD_ERROR;
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; 

  private Long tsCreated;

  private String uId;
  private String summary;
  private Long startTimestamp;
  private Long endTimestamp;
  private String location;
  private String organizer;
  private List<String> attendees;
  private Text description;
  private Status status = Status.PARSED;
  

  public ICalInfos() {
    this.tsCreated = System.currentTimeMillis();
  }

  public String getuId() {
    return this.uId;
  }

  public void setuId(String uId) {
    this.uId = uId;
  }

  public String getSummary() {
    return this.summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getOrganizer() {
    return this.organizer;
  }

  public void setOrganizer(String organizer) {
    this.organizer = organizer;
  }

  public List<String> getAttendees() {
    return this.attendees; 
  }

  public void setAttendees(List<String> attendees) {
    this.attendees = attendees; 
  }
 
  public String getDescription() { 
    return this.description.getValue();
  }

  public void setDescription(String description) {
    this.description = new Text(description);
  }
  
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }  

  @Override
  public Long getId() {
    return this.id;
  }

  public Long getTsCreated() {
    return this.tsCreated;
  }

  public Long getStartTimestamp() {
    return this.startTimestamp;
  }

  public void setStartTimestamp(Long startTimestamp) {
    this.startTimestamp = startTimestamp;
  }

  public Long getEndTimestamp() {
    return this.endTimestamp;
  }

  public void setEndTimestamp(Long endTimestamp) {
    this.endTimestamp = endTimestamp;
  }
  
  @Override
  public String toString() {
    return String
            .format("ICalInfos [id=%s, tsCreated=%s, uId=%s, summary=%s, startTimestamp=%s, endTimestamp=%s, location=%s, organizer=%s, attendees=%s, description=%s, status=%s]",
                    id, tsCreated, uId, summary, startTimestamp, endTimestamp, location, organizer, attendees,
                    description, status);
  }

  public String toShortSummary() {
    // @formatter:off
    return String.format(
            "[%s - %s] '%s' in '%s'. (uid: %s, tsCreated: %s)",
            Utils.timestampToFormattedDate(this.startTimestamp),
            Utils.timestampToFormattedDate(this.endTimestamp),
            this.summary, this.location, this.uId,
            Utils.DATE_FORMAT.format(this.tsCreated));
    // @formatter:on
  }
}
