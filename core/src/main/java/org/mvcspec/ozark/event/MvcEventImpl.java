/*
 * Copyright © 2017 Ivar Grimstad (ivar.grimstad@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mvcspec.ozark.event;

import javax.enterprise.context.Dependent;
import javax.mvc.event.MvcEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An implementation of {@link javax.mvc.event.MvcEvent}.
 *
 * @author Santiago Pericas-Geertsen
 */
@Dependent
public class MvcEventImpl implements MvcEvent {

    private String id;

    public MvcEventImpl() {
        id = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }

    @Override
    public String getId() {
        return id;
    }
}
