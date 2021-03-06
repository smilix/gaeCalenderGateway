package de.smilix.gaeCalenderGateway.web.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class RestApplication extends Application {

  private Set<Object> singletons = new HashSet<Object>();
  
  public RestApplication() {
    singletons.add(new BaseResource());
    singletons.add(new ConfigRessource());
    singletons.add(new RawMailInResource());
    singletons.add(new IcalInfoResource());
    singletons.add(new DebugResource());
  }
  
  @Override
  public Set<Object> getSingletons() {
    return singletons;
  }

}
