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

import java.sql.ResultSet;

import org.adempiere.base.IModelFactory;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.util.Env;
import org.plugin.model.MOrder; 

public class PluginModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {
		 if (tableName.equals(MInvoice.Table_Name)){
			 return MInvoice.class;
		 } else if (tableName.equals(MInvoiceLine.Table_Name)){
			 return MInvoiceLine.class;
		 } else if (tableName.equals(MInOutLine.Table_Name)){
			 return MInOutLine.class;
		 } 
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		 if (tableName.equals(MInvoice.Table_Name)) {
		     return new MInvoice(Env.getCtx(), Record_ID, trxName);
		 } else if (tableName.equals(MInvoiceLine.Table_Name)) {
		     return new MInvoiceLine(Env.getCtx(), Record_ID, trxName);
		 } else if (tableName.equals(MInOutLine.Table_Name)) {
		     return new MInOutLine(Env.getCtx(), Record_ID, trxName);
		 } 
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		 if (tableName.equals(MInvoice.Table_Name)) {
		     return new MInvoice(Env.getCtx(), rs, trxName);		 			     
		   }else if (tableName.equals(MOrderLine.Table_Name)) {
			     return new MOrderLine(Env.getCtx(), rs, trxName);		 			     
		   }else if (tableName.equals(MInOutLine.Table_Name)) {
			     return new MInOutLine(Env.getCtx(), rs, trxName);		 			     
		   }
		 return null;
	}

}
