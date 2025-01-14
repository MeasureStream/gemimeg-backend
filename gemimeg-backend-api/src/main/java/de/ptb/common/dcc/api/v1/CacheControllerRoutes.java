package de.ptb.common.dcc.api.v1;

public class CacheControllerRoutes {

  private CacheControllerRoutes() {
    // Konstantenklasse
  }

  public static final String BASE_PATH = "/api/v1/cache";
  public static final String ID_PARAM_NAME = "id";
  public static final String ID_PLACEHOLDER = "{" + ID_PARAM_NAME + "}";
  public static final String ID_PATH_PART = "/" + ID_PLACEHOLDER;
  public static final String BASE_PATH_ID = BASE_PATH + ID_PATH_PART;
}
