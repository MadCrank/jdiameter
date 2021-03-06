/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
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

package org.jdiameter.common.impl.controller;

import java.util.ArrayList;
import java.util.List;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Peer;
import org.jdiameter.api.URI;
import org.jdiameter.client.impl.helpers.UIDGenerator;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.common.api.statistic.IStatisticRecord;

/**
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class AbstractPeer implements Comparable<Peer> {

  public static final int INT_COMMON_APP_ID = 0xffffffff;
  protected static UIDGenerator uid = new UIDGenerator();
  // Statistic
  protected IStatistic statistic;
  protected List<IStatisticRecord> perSecondRecords = new ArrayList<IStatisticRecord>();
  protected URI uri;
  protected IStatisticManager statisticFactory;

  public AbstractPeer(URI uri, IStatisticManager statisticFactory) {
    this.uri = uri;
    this.statisticFactory = statisticFactory;
  }

  protected void createPeerStatistics() {
	  if(this.statistic != null) {
		  return;
	  }
	  String uriString = uri == null ? "local":uri.toString();
	  IStatisticRecord appGenRequestCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.AppGenRequest);
	  IStatisticRecord appGenCPSRequestCounter = statisticFactory.newPerSecondCounterRecord(uriString,IStatisticRecord.Counters.AppGenRequestPerSecond, appGenRequestCounter);
	  IStatisticRecord appGenRejectedRequestCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.AppGenRejectedRequest);

	  perSecondRecords.add(appGenCPSRequestCounter);

	  IStatisticRecord appGenResponseCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.AppGenResponse);
	  IStatisticRecord appGenCPSResponseCounter = statisticFactory.newPerSecondCounterRecord(uriString,IStatisticRecord.Counters.AppGenResponsePerSecond, appGenResponseCounter);
	  IStatisticRecord appGenRejectedResponseCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.AppGenRejectedResponse);

	  perSecondRecords.add(appGenCPSResponseCounter);

	  IStatisticRecord netGenRequestCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.NetGenRequest);
	  IStatisticRecord netGenCPSRequestCounter = statisticFactory.newPerSecondCounterRecord(uriString,IStatisticRecord.Counters.NetGenRequestPerSecond, netGenRequestCounter);
	  IStatisticRecord netGenRejectedRequestCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.NetGenRejectedRequest);

	  perSecondRecords.add(netGenCPSRequestCounter);

	  IStatisticRecord netGenResponseCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.NetGenResponse);
	  IStatisticRecord netGenCPSResponseCounter = statisticFactory.newPerSecondCounterRecord(uriString,IStatisticRecord.Counters.NetGenResponsePerSecond, netGenResponseCounter);
	  IStatisticRecord netGenRejectedResponseCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.NetGenRejectedResponse);

	  perSecondRecords.add(netGenCPSResponseCounter);

	  IStatisticRecord sysGenResponseCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.SysGenResponse);

	  this.statistic = statisticFactory.newStatistic(uriString,IStatistic.Groups.Peer,
	      appGenRequestCounter, appGenCPSRequestCounter, appGenRejectedRequestCounter,
	      appGenResponseCounter, appGenCPSResponseCounter, appGenRejectedResponseCounter,
	      netGenRequestCounter, netGenCPSRequestCounter, netGenRejectedRequestCounter,
	      netGenResponseCounter, netGenCPSResponseCounter, netGenRejectedResponseCounter,
	      sysGenResponseCounter
	  );

  }

  protected void removePeerStatistics() {
    if (this.statistic != null) {
      for (IStatisticRecord rec : this.perSecondRecords) {
        this.statisticFactory.removePerSecondCounterRecord(rec);
      }

      this.statisticFactory.removeStatistic(this.statistic);
      this.perSecondRecords.clear();
      this.statistic = null;
    }
  }
  
  
  public int compareTo(Peer o) {
    return uri.compareTo(o.getUri());
  }

	/**
	 * @throws IllegalDiameterStateException 
	 * @throws InternalException 
	 * 
	 */
	protected void disconnect(int disconnectCause) throws InternalException, IllegalDiameterStateException {
	}
}
