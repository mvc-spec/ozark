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
package org.glassfish.ozark.test.redirectscope2;

import java.io.Serializable;
import javax.inject.Named;
import javax.mvc.annotation.RedirectScoped;

/**
 * A simple RedirectScoped bean.
 * 
 * @author Manfred Riem (manfred.riem at oracle.com)
 */
@Named("redirectBean")
@RedirectScoped
public class RedirectBean implements Serializable {

    /**
     * Stores the value.
     */
    private String value = "Initial value";
    
    /**
     * Get the value.
     * 
     * @return the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value.
     * 
     * @param value the value. 
     */
    public void setValue(String value) {
        this.value = value;
    }
}
