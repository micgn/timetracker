/* 
 * Copyright 2015 Michael Gnatz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mg.ttjs.shared;

import java.io.Serializable;

/**
 *
 * @author gnatz
 */
public class ServerException extends Exception implements Serializable {

    public ServerException() {
        super();
    }

    public ServerException(String msg) {
        super(msg);
    }

    public ServerException(Exception ex) {
        super(ex);
    }

    public ServerException(String msg, Exception ex) {
        super(msg, ex);
    }
}
