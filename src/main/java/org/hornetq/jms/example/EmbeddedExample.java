/*
 * Copyright 2009 Red Hat, Inc.
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.hornetq.jms.example;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.hornetq.common.example.HornetQExample;
import org.hornetq.jms.server.embedded.EmbeddedJMS;

/**
 * This example demonstrates how to run a HornetQ embedded with JMS
 *
 * @author <a href="clebert.suconic@jboss.com">Clebert Suconic</a>
 * @author <a href="mailto:jmesnil@redhat.com">Jeff Mesnil</a>
 */
public class EmbeddedExample extends HornetQExample
{

   public static void main(final String[] args) throws Exception
   {
      new EmbeddedExample().runExample();
   }

   @Override
   public boolean runExample() throws Exception
   {
      try
      {
         EmbeddedJMS jmsServer = new EmbeddedJMS();
         jmsServer.start();
         
         ConnectionFactory cf = (ConnectionFactory)jmsServer.lookup("ConnectionFactory");
         Topic topic = (Topic)jmsServer.lookup("/topic/chat");

         Connection connection = null;
         try
         {
            connection = cf.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = session.createProducer(topic);
            MessageConsumer consumer = session.createConsumer(topic);

            // use JMS bytes message with UTF-8 String to send a text to Stomp clients
            String text = "message sent from a Java application at " + new Date();
            //BytesMessage message = session.createBytesMessage();
            //message.writeBytes(text.getBytes("UTF-8"));
            TextMessage message = session.createTextMessage(text);
            System.out.println("Sent message: " + text);
            System.out.println("Open up the chat/index.html file in a browser and press enter");
            System.in.read();
            producer.send(message);

            connection.start();

            message = (TextMessage)consumer.receive();
            System.out.println("Received message: " + message.getText());
         }
         finally
         {
            if (connection != null)
            {
               connection.close();
            }

            jmsServer.stop();
            System.out.println("Stopped the JMS Server");
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return false;
      }
      //return true;
	return true;
   }
}
