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

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.mg.tt.api.TTData;
import de.mg.ttjs.client.SaveService;
import de.mg.ttjs.shared.ServerException;
import de.mg.ttjs.shared.model.DayModel;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@SuppressWarnings("serial")
public class SaveServiceImpl extends RemoteServiceServlet implements
        SaveService {

    @Override
    public DayModel saveOrUpdate(DayModel model) throws ServerException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(TTData.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            TTData apiModel = ServiceCommon.convertToApi(model);
            StringWriter writer = new StringWriter();
            jaxbMarshaller.marshal(apiModel, writer);
            String apiModelXml = writer.toString();

            String path = ServiceCommon.createDatePathPattern(model.getDate());
            String apiModelResultXml = RestClient.post(path, apiModelXml);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(apiModelResultXml);
            TTData apiModelResult = (TTData) unmarshaller.unmarshal(reader);

            DayModel resultModel = ServiceCommon.convertFromApi(apiModelResult, model.getDate());
            return resultModel;

        } catch (JAXBException | IOException e) {
            throw new ServerException(e.getMessage(), e);
        }
    }

}
