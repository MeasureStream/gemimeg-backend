/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.api.v1.dto;

import java.util.List;

public interface HasRefIds {

  List<String> getRefIds();

  void setRefIds(List<String> refIds);
}
