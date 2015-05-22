/******************************************************************************
 * Product: iDempiere Free ERP Project based on Compiere (2006)               *
 * Copyright (C) 2014 Redhuan D. Oon All Rights Reserved.                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *  FOR NON-COMMERCIAL DEVELOPER USE ONLY                                     *
 *  @author Redhuan D. Oon  - red1@red1.org  www.red1.org                     *
 *****************************************************************************/

package org.idempiere.component;

import java.util.List;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventTopics;
import org.adempiere.base.event.LoginEventData;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrderLine;
import org.compiere.model.MRMA;
import org.compiere.model.MRMALine;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.osgi.service.event.Event;

/**
 *  @author red1
 */
public class PluginDocEvent extends AbstractEventHandler{
	private static CLogger log = CLogger.getCLogger(PluginDocEvent.class);
	private String trxName = "";
	private PO po = null; 
	@Override
	protected void initialize() { 
	//register EventTopics and TableNames
		registerTableEvent(IEventTopics.DOC_BEFORE_PREPARE, MInvoice.Table_Name);
		log.info("<PLUGIN> INVOICE MINUS RETURNS IS NOW INITIALIZED");
		}

	@Override
	protected void doHandleEvent(Event event) {
		String type = event.getTopic();
		//testing that it works at login
		if (type.equals(IEventTopics.AFTER_LOGIN)) {
			LoginEventData eventData = getEventData(event);
			log.fine(" topic="+event.getTopic()+" AD_Client_ID="+eventData.getAD_Client_ID()
					+" AD_Org_ID="+eventData.getAD_Org_ID()+" AD_Role_ID="+eventData.getAD_Role_ID()
					+" AD_User_ID="+eventData.getAD_User_ID());
			}
		else 
		{
			setPo(getPO(event));
			setTrxName(po.get_TrxName());
			log.info(" topic="+event.getTopic()+" po="+po);
			if (po instanceof MInvoice){
				if (IEventTopics.DOC_BEFORE_PREPARE == type){
					boolean gotRMA = false;
					//get the Sales Order document
					MInvoice invoice = (MInvoice)po;
					setTrxName(trxName); 
					invoice.getDocStatus();
					List<MInvoiceLine> lines = new Query(Env.getCtx(),MInvoiceLine.Table_Name,MInvoice.COLUMNNAME_C_Invoice_ID+"=?",trxName)
					.setParameters(invoice.getC_Invoice_ID()).list();
					for (MInvoiceLine line:lines){
						int iol = line.getM_InOutLine_ID();
						if (iol==0 && line.getC_OrderLine_ID()>0){
							MInOutLine oline = new Query(Env.getCtx(), MInOutLine.Table_Name,MInOutLine.COLUMNNAME_C_OrderLine_ID+"=?",trxName)
							.setParameters(line.getC_OrderLine_ID()).first();
							if (oline.get_ID()>0)
								iol = oline.get_ID();
						}
							
						if (iol>0){
							MRMALine rmaline = new Query(Env.getCtx(),MRMALine.Table_Name,MRMALine.COLUMNNAME_M_InOutLine_ID+"=?",trxName)
							.setParameters(iol).first();
							if (rmaline!=null) {
								log.fine("Invoice has RMA: "+invoice.getDocumentNo()+ " for Product: "+line.getM_Product() +" QTY: "+rmaline.getQty());
								if (rmaline.getQty().compareTo(line.getQtyInvoiced())==0)
									gotRMA = line.delete(true);
								else {
									line.setQty(line.getQtyInvoiced().subtract(rmaline.getQty()));	
									line.saveEx(trxName);
								}
																
							 }								
						 }
					 }
					 if (gotRMA)
						 invoice.saveEx(trxName);
					
				}
			}
		}
	}
	private void setPo(PO eventPO) {
		 po = eventPO;
	}

	private void setTrxName(String get_TrxName) {
		trxName = get_TrxName;		
	}
}
