/**
 * Copyright (C) 2016 Fredrique. Samuels.
 * All rights reserved.  Email: fredriquesamuels@gmail.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of EITHER:
 *   (1) The GNU Lesser General Public License as published by the Free
 *       Software Foundation; either version 2.1 of the License, or (at
 *       your option) any later version. The text of the GNU Lesser
 *       General Public License is included with this library in the
 *       file LICENSE.TXT.
 *   (2) The BSD-style license that is included with this library in
 *       the file LICENSE-BSD.TXT.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the files
 * LICENSE.TXT and LICENSE-BSD.TXT for more details.
 *
 * @Author: Fredrique Samuels fredriquesamuels@gmail.com
 */
package org.tect.platform.document.hibernate;

import java.util.Collections;
import java.util.List;

public abstract class HibernateData {
    public abstract Long getId();
    public final boolean isPersisted(){return getId()!=null && getId()>0;}
    public List<HibernateData> getRelationShipData() {return Collections.emptyList(); }
}
