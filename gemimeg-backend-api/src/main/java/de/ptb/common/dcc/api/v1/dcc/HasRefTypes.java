/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.api.v1.dcc;

import java.util.List;

public interface HasRefTypes {

  List<String> getRefTypes();

  void setRefTypes(List<String> refTypes);
}
