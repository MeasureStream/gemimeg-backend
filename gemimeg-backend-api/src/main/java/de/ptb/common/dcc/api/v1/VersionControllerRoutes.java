package de.ptb.common.dcc.api.v1;

public class VersionControllerRoutes {

  public static final String PATH_PART_NAME = "pathPart";
  public static final String PATH_PART_PLACEHOLDER = "{" + PATH_PART_NAME + "}";
  public static final String VERSION_PATH = "/api/v1/" + PATH_PART_PLACEHOLDER + "/version";
}
