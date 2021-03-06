/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.persistence.hibernate.test;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.hibernate.Session;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.persistence.test.util.HelloService;
import org.jboss.seam.persistence.test.util.Hotel;
import org.jboss.seam.persistence.test.util.HotelNameProducer;
import org.jboss.seam.persistence.test.util.ManagedHibernateSessionProvider;
import org.jboss.seam.persistence.transaction.DefaultTransaction;
import org.jboss.seam.persistence.transaction.SeamTransaction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ManagedHibernateSessionELTestBase
{

   public static Class<?>[] getTestClasses()
   {
      return new Class[] { ManagedHibernateSessionELTestBase.class, Hotel.class, ManagedHibernateSessionProvider.class, HotelNameProducer.class, HelloService.class };
   }

   @Inject
   @DefaultTransaction
   SeamTransaction transaction;

   @Inject
   Session session;

   @Test
   public void testELInInquery() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException
   {
      transaction.begin();
      Hotel h = new Hotel("Hilton", "Fake St", "Wollongong", "NSW", "2518", "Australia");
      session.persist(h);
      session.flush();
      transaction.commit();

      transaction.begin();
      h = new Hotel("Other Hotel", "Real St ", "Wollongong", "NSW", "2518", "Australia");
      session.persist(h);
      session.flush();
      transaction.commit();

      transaction.begin();
      Hotel hilton = (Hotel) session.createQuery("select h from Hotel h where h.name=#{hotelName}").uniqueResult();
      Assert.assertTrue(hilton.getName().equals("Hilton"));
      Assert.assertTrue(hilton.getAddress().equals("Fake St"));
      transaction.commit();

   }

}
