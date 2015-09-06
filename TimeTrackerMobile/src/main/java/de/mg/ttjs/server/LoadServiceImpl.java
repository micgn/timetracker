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
package de.mg.ttjs.server;

import java.util.Date;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import de.mg.tt.api.TTData;

import de.mg.ttjs.client.LoadService;
import de.mg.ttjs.shared.ServerException;
import de.mg.ttjs.shared.model.DayModel;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@SuppressWarnings("serial")
public class LoadServiceImpl extends RemoteServiceServlet implements
        LoadService {

    @Override
    public DayModel load(Date date) throws ServerException {
        try {
            String path = ServiceCommon.createDatePathPattern(date);
            String xml = RestClient.get(path);

            JAXBContext jaxbContext = JAXBContext.newInstance(TTData.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            StringReader reader = new StringReader(xml);
            TTData data = (TTData) unmarshaller.unmarshal(reader);

            DayModel model = ServiceCommon.convertFromApi(data, date);
            return model;

        } catch (IOException | JAXBException e) {
            throw new ServerException(e.getMessage(), e);
        }
    }

}
